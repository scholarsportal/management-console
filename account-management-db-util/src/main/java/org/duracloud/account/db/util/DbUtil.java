/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.AccountRights;
import org.duracloud.account.db.model.BaseEntity;
import org.duracloud.account.db.model.ComputeProviderAccount;
import org.duracloud.account.db.model.DuracloudGroup;
import org.duracloud.account.db.model.DuracloudInstance;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.model.Role;
import org.duracloud.account.db.model.ServerDetails;
import org.duracloud.account.db.model.ServerImage;
import org.duracloud.account.db.model.StorageProviderAccount;
import org.duracloud.account.db.model.UserInvitation;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.storage.domain.StorageProviderType;
import org.hibernate.LazyInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Performs the work of the Account Management DB Util.
 * 
 * @author: Bill Branan Date: Dec 21, 2010
 */
public class DbUtil {

    public enum COMMAND {
        GET, PUT, CLEAR
    };

    private final Logger log = LoggerFactory.getLogger(DbUtil.class);

    private DuracloudRepoMgr repoMgr;
    private File workDir;

    public DbUtil(DuracloudRepoMgr repoMgr, File workDir) {
        this.repoMgr = repoMgr;
        this.workDir = workDir;
    }

    public void runCommand(COMMAND command) {
        System.out.println("Running DB Util with command " + command.name()
                + "\n\t using work directory: " + workDir.getAbsolutePath());

        if (COMMAND.PUT.equals(command)) {
            doPut();
        } else {
            throw new UnsupportedOperationException("The " + command + " command is no longer supported.");
        }
    }


    private void doPut() {
        // Defines the order to import the different entity types. This is
        // necessary
        // because of defined and enforced JPA relationships.
        String[] files = { "AccountCluster", "ServiceRepository",
                "StorageProviderAccount", "ComputeProviderAccount",
                "ServerDetails", "AccountInfo", "DuracloudUser", "ServerImage",
                "DuracloudInstance", "DuracloudGroup", "UserInvitation",
                "AccountRights" };

        for (String fileName : files) {
            File inputFile = new File(workDir, fileName + ".xml");
            if (inputFile.length() > 0) {
                String xml = readFromFile(inputFile);
                List<BaseEntity> entities = (List<BaseEntity>) deserialize(xml);
                saveEntities(entities);
            }
        }
    }

    private String readFromFile(File inFile) {
        try {
            return FileUtils.readFileToString(inFile, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException("Could not read from file " + inFile
                    + " due to error " + e.getMessage());
        }
    }

    private void saveEntities(List<BaseEntity> entities) {
        if (!entities.isEmpty()) {
            final JpaRepository repo = getRepo(entities.get(0));

            for (final BaseEntity entity : entities) {

                // Entities with relationship need to have their related
                // entities looked up and then set to save properly.
                if (entity instanceof ServerDetails) {
                    ServerDetails sd = (ServerDetails) entity;
                    sd.setComputeProviderAccount(repoMgr
                            .getComputeProviderAccountRepo().findOne(
                                    sd.getComputeProviderAccount().getId()));
                    sd.setPrimaryStorageProviderAccount(repoMgr
                            .getStorageProviderAccountRepo().findOne(
                                    sd.getPrimaryStorageProviderAccount()
                                            .getId()));

                    Set<StorageProviderAccount> storageProviderAccounts = sd
                            .getSecondaryStorageProviderAccounts();
                    try {
                        if (storageProviderAccounts.size() > 0) {
                            Set<StorageProviderAccount> accounts = new HashSet<>();
                            for (StorageProviderAccount sp : storageProviderAccounts) {
                                StorageProviderAccount account = repoMgr
                                        .getStorageProviderAccountRepo()
                                        .findOne(sp.getId());
                                accounts.add(account);
                            }
                            sd.setSecondaryStorageProviderAccounts(accounts);
                            log.warn("Set a secondary storage provider to ServerDetails with id "
                                    + sd.getId());
                        }
                    } catch (LazyInitializationException e) {
                        // do nothing, there's no secondary storage providers
                        // for this ServerDetails entity
                    } catch (Exception e) {
                        log.warn("Exception not handled!!!");
                        log.warn(e.toString());
                    }
                    repo.saveAndFlush(sd);
                } else if (entity instanceof AccountInfo) {
                    AccountInfo ai = (AccountInfo) entity;
                    ai.setServerDetails(repoMgr.getServerDetailsRepo().findOne(
                            ai.getServerDetails().getId()));
                    repo.saveAndFlush(ai);
                } else if (entity instanceof DuracloudInstance) {
                    DuracloudInstance di = (DuracloudInstance) entity;
                    di.setImage(repoMgr.getServerImageRepo().findOne(
                            di.getImage().getId()));
                    di.setAccount(repoMgr.getAccountRepo().findOne(
                            di.getAccount().getId()));
                    repo.saveAndFlush(entity);
                } else if (entity instanceof DuracloudGroup) {
                    DuracloudGroup dg = (DuracloudGroup) entity;
                    dg.setAccount(repoMgr.getAccountRepo().findOne(
                            dg.getAccount().getId()));
                    if (dg.getUsers().size() > 0) {
                        Set<DuracloudUser> users = new HashSet<>();
                        for (DuracloudUser user : dg.getUsers()) {
                            users.add(repoMgr.getUserRepo().findOne(
                                    user.getId()));
                        }
                        dg.setUsers(users);
                    }
                    repo.saveAndFlush(dg);
                } else if (entity instanceof AccountRights) {
                    AccountRights rights = (AccountRights) entity;
                    rights.setAccount(repoMgr.getAccountRepo().findOne(
                            rights.getAccount().getId()));

                    rights.setUser(repoMgr.getUserRepo().findOne(
                            rights.getUser().getId()));
                    repo.saveAndFlush(rights);
                } else if (entity instanceof UserInvitation) {
                    UserInvitation ui = (UserInvitation) entity;
                    ui.setAccount(repoMgr.getAccountRepo().findOne(
                            ui.getAccount().getId()));
                    repo.saveAndFlush(ui);
                } else {
                    repo.saveAndFlush(entity);
                }
            }
        }
    }

    private JpaRepository getRepo(BaseEntity item) {
        JpaRepository repo;
        if (item instanceof DuracloudUser) {
            repo = repoMgr.getUserRepo();
        } else if (item instanceof AccountInfo) {
            repo = repoMgr.getAccountRepo();
        } else if (item instanceof AccountRights) {
            repo = repoMgr.getRightsRepo();
        } else if (item instanceof UserInvitation) {
            repo = repoMgr.getUserInvitationRepo();
        } else if (item instanceof DuracloudInstance) {
            repo = repoMgr.getInstanceRepo();
        } else if (item instanceof ServerImage) {
            repo = repoMgr.getServerImageRepo();
        } else if (item instanceof ComputeProviderAccount) {
            repo = repoMgr.getComputeProviderAccountRepo();
        } else if (item instanceof StorageProviderAccount) {
            repo = repoMgr.getStorageProviderAccountRepo();
        } else if(item instanceof DuracloudGroup) {
            repo = repoMgr.getGroupRepo();
        } else if (item instanceof ServerDetails) {
            repo = repoMgr.getServerDetailsRepo();
        } else {
            throw new RuntimeException("Item is not a known type: "
                    + item.getClass().getName());
        }
        return repo;
    }

    private Object deserialize(String xml) {
        return getXStream().fromXML(xml);
    }

    private XStream getXStream() {
        XStream xstream = new XStream(new DomDriver());
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.alias(DuracloudUser.class.getSimpleName(), DuracloudUser.class);
        xstream.alias(DuracloudGroup.class.getSimpleName(),
                DuracloudGroup.class);
        xstream.alias(AccountInfo.class.getSimpleName(), AccountInfo.class);
        xstream.alias(ServerDetails.class.getSimpleName(), ServerDetails.class);
        xstream.alias(AccountRights.class.getSimpleName(), AccountRights.class);
        xstream.alias(UserInvitation.class.getSimpleName(),
                UserInvitation.class);
        xstream.alias(DuracloudInstance.class.getSimpleName(),
                DuracloudInstance.class);
        xstream.alias(ServerImage.class.getSimpleName(), ServerImage.class);
        xstream.alias(ComputeProviderAccount.class.getSimpleName(),
                ComputeProviderAccount.class);
        xstream.alias(StorageProviderAccount.class.getSimpleName(),
                      StorageProviderAccount.class);
        xstream.alias(Role.class.getSimpleName(), Role.class);
        xstream.alias(StorageProviderType.class.getSimpleName(),
                StorageProviderType.class);
        
        return xstream;
    }

}
