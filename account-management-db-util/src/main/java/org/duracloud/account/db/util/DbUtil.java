/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.BaseDomainData;
import org.duracloud.account.common.domain.ComputeProviderAccount;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.common.domain.ServerImage;
import org.duracloud.account.common.domain.ServiceRepository;
import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.db.BaseRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.amazonsimple.AmazonSimpleDBRepoMgr;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.db.impl.IdUtilImpl;
import org.duracloud.account.init.domain.AmaConfig;
import org.duracloud.account.init.xml.AmaInitDocumentBinding;
import org.duracloud.storage.domain.StorageProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Performs the work of the Account Management DB Util.
 *
 * @author: Bill Branan
 * Date: Dec 21, 2010
 */
public class DbUtil {

    public enum COMMAND {GET, PUT, CLEAR, FILL};

    private final Logger log = LoggerFactory.getLogger(DbUtil.class);    

    private DuracloudRepoMgr repoMgr;
    private File workDir;

    public DbUtil(File configFile, File workDir) {
        this(getAmaConfig(configFile), workDir);
    }

    public DbUtil(AmaConfig config, File workDir) {
        this.repoMgr = getRepoManager(config);
        this.workDir = workDir;
    }

    /**
     * Only to be used by unit tests
     */
    protected DbUtil(DuracloudRepoMgr repoMgr, File workDir) {
        this.repoMgr = repoMgr;
        this.workDir = workDir;
    }

    public void runCommand(COMMAND command) {
        System.out.println("Running DB Util with command " + command.name() +
            "\n\t using work directory: " + workDir.getAbsolutePath());

        if(COMMAND.GET.equals(command)) {
            doGet();
        } else if(COMMAND.PUT.equals(command)) {
            doPut();
        } else if(COMMAND.CLEAR.equals(command)) {
            doGet();
            doClear();
        } else if(COMMAND.FILL.equals(command)) {
            doGet();
            DbUtilFiller filler = new DbUtilFiller(repoMgr);
            filler.fill();
        }
    }

    private static AmaConfig getAmaConfig(File configFile) {
        FileInputStream configStream = null;
        AmaConfig config = null;
        try {
            configStream = new FileInputStream(configFile);
            config = AmaInitDocumentBinding.createAmaConfigFrom(configStream);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);

        } finally {
            IOUtils.closeQuietly(configStream);
        }

        if (null == config) {
            throw new RuntimeException("Error creating AmaConfig.");
        }

        return config;
    }

    private DuracloudRepoMgr getRepoManager(AmaConfig config) {
        DuracloudRepoMgr repoMgr = new AmazonSimpleDBRepoMgr(new IdUtilImpl());
        repoMgr.initialize(config);
        return repoMgr;
    }

    private void doGet() {
        for(BaseRepo repo : repoMgr.getAllRepos()) {
            writeRepo(repo);
        }
    }

    private void writeRepo(BaseRepo repo) {
        try {
            List items = new ArrayList();
            for(int id : (Set<Integer>)repo.getIds()) {
                items.add(repo.findById(id));
            }
            if(items.size() > 0) {
                String serialized = serialize(items);
                String name = items.get(0).getClass().getSimpleName() + ".xml";
                writeToFile(serialized, name);
            }
        } catch (DBNotFoundException e) {
            log.error("Item not found: " + e.getMessage());
        }
    }

    private void writeToFile(String serialized, String fileName) {
        File outFile = new File(workDir, fileName);
        try {
            FileUtils.writeStringToFile(outFile, serialized, "UTF-8");
        } catch(IOException e) {
            throw new RuntimeException("Could not write to file " +
                                       outFile.getAbsolutePath() +
                                       " due to error " + e.getMessage());
        }
    }

    private void doPut() {
        for(File inputFile : workDir.listFiles()) {
            if(inputFile.length() > 0) {
                String xml = readFromFile(inputFile);
                for(BaseDomainData item : (List<BaseDomainData>)deserialize(xml)) {
                    saveItem(item);
                }
            }
        }
    }

    private String readFromFile(File inFile) {
        try {
        return FileUtils.readFileToString(inFile, "UTF-8");
        } catch(IOException e) {
            throw new RuntimeException("Could not read from file " + inFile +
                                       " due to error " + e.getMessage());
        }
    }

    private void saveItem(BaseDomainData item) {
        BaseRepo repo = getRepo(item);
        try {
            BaseDomainData repoVersion =
                (BaseDomainData)repo.findById(item.getId());
            if(item.equals(repoVersion)) {
                return; // No reason to update
            }
        } catch(DBNotFoundException e) {
            // Item does not exist, set counter to 0 and continue
            item.setCounter(0);
        }

        try {
            System.out.println("Updating item of type " +
                               item.getClass().getSimpleName() +
                               " with ID " + item.getId());
            repo.save(item);
        } catch (DBConcurrentUpdateException e) {
            log.error("Unable to save item of type " +
                item.getClass().getName() + " with ID " + item.getId() +
                " due to concurrent update exception: " + e.getMessage());
        }
    }

    private BaseRepo getRepo(BaseDomainData item) {
        BaseRepo repo;
        if(item instanceof DuracloudUser) {
            repo = repoMgr.getUserRepo();
        } else if(item instanceof AccountInfo) {
            repo = repoMgr.getAccountRepo();
        } else if(item instanceof AccountRights) {
            repo = repoMgr.getRightsRepo();
        } else if(item instanceof UserInvitation) {
            repo = repoMgr.getUserInvitationRepo();
        } else if(item instanceof DuracloudInstance) {
            repo = repoMgr.getInstanceRepo();
        } else if(item instanceof ServerImage) {
            repo = repoMgr.getServerImageRepo();
        } else if(item instanceof ComputeProviderAccount) {
            repo = repoMgr.getComputeProviderAccountRepo();
        } else if(item instanceof StorageProviderAccount) {
            repo = repoMgr.getStorageProviderAccountRepo();
        } else if(item instanceof ServiceRepository) {
            repo = repoMgr.getServiceRepositoryRepo();
        } else {
            throw new RuntimeException("Item is not a known type: " +
                                       item.getClass().getName());
        }
        return repo;
    }

    private void doClear() {
        for(BaseRepo repo : repoMgr.getAllRepos()) {
            System.out.println("Removing all items from repo " +
                repo.getClass().getSimpleName());
            for(int id : (Set<Integer>)repo.getIds()) {
                repo.delete(id);
            }
        }
    }

    private String serialize(Object obj) {
        return getXStream().toXML(obj);
    }

    private Object deserialize(String xml) {
        return getXStream().fromXML(xml);
    }

    private XStream getXStream() {
        XStream xstream = new XStream(new DomDriver());
        xstream.setMode(XStream.NO_REFERENCES);        
        xstream.alias(DuracloudUser.class.getSimpleName(), DuracloudUser.class);
        xstream.alias(AccountInfo.class.getSimpleName(), AccountInfo.class);
        xstream.alias(AccountRights.class.getSimpleName(), AccountRights.class);
        xstream.alias(UserInvitation.class.getSimpleName(), UserInvitation.class);
        xstream.alias(DuracloudInstance.class.getSimpleName(),
                      DuracloudInstance.class);
        xstream.alias(ServerImage.class.getSimpleName(),
                      ServerImage.class);
        xstream.alias(ComputeProviderAccount.class.getSimpleName(),
                      ComputeProviderAccount.class);
        xstream.alias(StorageProviderAccount.class.getSimpleName(),
                      StorageProviderAccount.class);
        xstream.alias(ServiceRepository.class.getSimpleName(),
                      ServiceRepository.class);        
        xstream.alias(Role.class.getSimpleName(), Role.class);
        xstream.alias(StorageProviderType.class.getSimpleName(),
                      StorageProviderType.class);

        return xstream;
    }

}
