/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountType;
import org.duracloud.account.common.domain.ComputeProviderAccount;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.ServerDetails;
import org.duracloud.account.common.domain.ServerImage;
import org.duracloud.account.compute.ComputeProviderUtil;
import org.duracloud.account.compute.DuracloudComputeProvider;
import org.duracloud.account.compute.error.DuracloudInstanceNotAvailableException;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudServerImageRepo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.DuracloudInstanceManagerService;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.DuracloudInstanceServiceFactory;
import org.duracloud.account.util.error.DuracloudInstanceCreationException;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: Bill Branan
 * Date: Feb 3, 2011
 */
public class DuracloudInstanceManagerServiceImpl implements DuracloudInstanceManagerService {

	private Logger log =
        LoggerFactory.getLogger(DuracloudInstanceManagerServiceImpl.class);

    private static final String HOST_SUFFIX = ".duracloud.org";

    private DuracloudRepoMgr repoMgr;
    private ComputeProviderUtil computeUtil;
    private DuracloudInstanceServiceFactory instanceServiceFactory;

    public DuracloudInstanceManagerServiceImpl(DuracloudRepoMgr repoMgr,
                                               ComputeProviderUtil computeUtil,
                                               DuracloudInstanceServiceFactory instanceServiceFactory) {
        this.repoMgr = repoMgr;
        this.computeUtil = computeUtil;
        this.instanceServiceFactory = instanceServiceFactory;
    }

    @Override
    public Set<String> getVersions() {
        Set<ServerImage> serverImages = getServerImages();
        Set<String> versions = new HashSet<String>();
        for(ServerImage image : serverImages) {
            versions.add(image.getVersion());
        }
        return versions;
    }

    @Override
    public String getLatestVersion() {
        DuracloudServerImageRepo imageRepo = repoMgr.getServerImageRepo();
        ServerImage image = imageRepo.findLatest();
        return image.getVersion();
    }

    private Set<ServerImage> getServerImages() {
        DuracloudServerImageRepo imageRepo = repoMgr.getServerImageRepo();
        Set<Integer> imageIds = imageRepo.getIds();
        Set<ServerImage> serverImages = new HashSet<ServerImage>();
        for(int imageId : imageIds) {
            try {
                serverImages.add(imageRepo.findById(imageId));
            } catch(DBNotFoundException e) {
                log.error("Error retrieving ServerImage with ID " + imageId +
                          " from list of images due to: " + e.getMessage(), e);
            }
        }
        return serverImages;
    }

    /*
     * Prior to this call, these actions should have occurred:
     * - The user's AWS account was created
     * - The user's secondary storage provider accounts were created
     * - In EC2, an elastic IP, keypair, and security group were configured
     * - The user's host name (using their selected subdomain) was pointed to
     *     the configured elastic IP
     * - The ComputeProviderAccount and StorageProviderAccount information for
     *     this user was updated in the AMA database to include credentials,
     *     elastic IP, security group, and keypair values
     * - The ServiceRepository and Image information for the latest release was
     *     added to the AMA database
     */
    @Override
    public DuracloudInstanceService createInstance(int accountId, String version) {
        Set<ServerImage> serverImages = getServerImages();
        for(ServerImage image : serverImages) {
            if(version.equals(image.getVersion())) {
                return createInstance(accountId, image);
            }
        }

        String err = "The DuraCloud version " + version + " is not available";
        throw new DuracloudInstanceCreationException(err);
    }

    private DuracloudInstanceService createInstance(int accountId,
                                                    ServerImage image) {
        log.info("Creating new instance for account {} using image {} ",
                 accountId, image.getDescription());

        try {
            DuracloudInstance instance = doCreateInstance(accountId, image);
            return initializeInstance(instance);
        } catch(DBNotFoundException e) {
            String err = "Could not create instance for account with ID " +
                         accountId + " based on image with ID " +
                         image.getId() + " due to error: " + e.getMessage();
            throw new DuracloudInstanceCreationException(err, e);
        }
    }

    protected DuracloudInstance doCreateInstance(int accountId,
                                                 ServerImage image)
        throws DBNotFoundException {
        // Get Account information
        AccountInfo account = repoMgr.getAccountRepo().findById(accountId);

        if(account.getType().equals(AccountType.COMMUNITY)) {
            throw new DuraCloudRuntimeException("Cannot associate instance " +
                                                "with a community account");
        }

        // Create entry for new instance in DB
        String hostName = account.getSubdomain() + HOST_SUFFIX;
        int instanceId = repoMgr.getIdUtil().newInstanceId();
        DuracloudInstance instance = new DuracloudInstance(instanceId,
                                                           image.getId(),
                                                           accountId,
                                                           hostName,
                                                           DuracloudInstance.PLACEHOLDER_PROVIDER_ID,
                                                           false);
        try {
            repoMgr.getInstanceRepo().save(instance);
        } catch(DBConcurrentUpdateException e) {
            log.error("Error encountered attempting to save new Instance: " +
                      e.getMessage());
        }

        // Get info about compute provider account associated with this account
        int serverDetailsId = account.getServerDetailsId();
        if(serverDetailsId < 0) {
            String err = "Cannot start instance for account with ID " +
                accountId +
                ". No ServerDetails are associated with this account.";
            throw new DuraCloudRuntimeException(err);
        }

        ServerDetails details = repoMgr.getServerDetailsRepo()
                                       .findById(serverDetailsId);
        int cpAccountId = details.getComputeProviderAccountId();
        ComputeProviderAccount computeProviderAcct =
            repoMgr.getComputeProviderAccountRepo().findById(cpAccountId);

        // Get access to a duracloud compute provider
        DuracloudComputeProvider computeProvider =
            computeUtil.getComputeProvider(computeProviderAcct.getUsername(),
                                           computeProviderAcct.getPassword());

        // Start the instance
        String providerInstanceId = null;
        
        try{
            providerInstanceId = computeProvider.start(image.getProviderImageId(),
                                      computeProviderAcct.getSecurityGroup(),
                                      computeProviderAcct.getKeypair(),
                                      computeProviderAcct.getElasticIp());
    
        }catch(RuntimeException ex){
            repoMgr.getInstanceRepo().delete(instance.getId());
            throw new DuraCloudRuntimeException(ex.getMessage(), ex);
        }

        instance = repoMgr.getInstanceRepo().findById(instanceId);
        instance.setProviderInstanceId(providerInstanceId);

        try {
            repoMgr.getInstanceRepo().save(instance);
        } catch(DBConcurrentUpdateException e) {
            log.error("Error encountered attempting to save new Instance: " +
                      e.getMessage());
        }

        return instance;
    }

    private DuracloudInstanceService initializeInstance(DuracloudInstance instance)
        throws DBNotFoundException {
        DuracloudInstanceService instanceService =
            instanceServiceFactory.getInstance(instance);

        instanceService.initialize();

        return instanceService;
    }


    @Override
    public DuracloudInstanceService getInstanceService(int instanceId)
        throws DuracloudInstanceNotAvailableException {
        DuracloudInstanceService instanceService = null;
        DuracloudInstanceRepo instanceRepo = repoMgr.getInstanceRepo();
        try {
            DuracloudInstance instance = instanceRepo.findById(instanceId);
            instanceService = instanceServiceFactory.getInstance(instance);

        } catch(DBNotFoundException e) {
            throw new DuracloudInstanceNotAvailableException(e.getMessage(), e);
        }

        // Ensure that the instance exists. This throws if not.
        try {
            instanceService.getStatusInternal();

        } catch (DuracloudInstanceNotAvailableException e) {
            log.warn("Instance {} does not exist, deleting.", instanceId);
            instanceRepo.delete(instanceId);
            throw e;
        }
        return instanceService;
    }

    @Override
    public Set<DuracloudInstanceService> getInstanceServices(int accountId) {

        Set<Integer> instanceIds = getInstanceIds(accountId);
        Set<DuracloudInstanceService> instanceServices =
            new HashSet<DuracloudInstanceService>();

        if(null != instanceIds) {
            for(int instanceId : instanceIds) {
                try {
                    instanceServices.add(getInstanceService(instanceId));
                } catch(DuracloudInstanceNotAvailableException e) {
                    log.error("The instance with ID: " + instanceId +
                              " was found to be associated with the " +
                              "account with ID: " +  accountId +
                              " but the instance could not be found!");
                }
            }
        }
        return instanceServices;
    }

    private Set<Integer> getInstanceIds(int accountId) {
        try {
            DuracloudInstanceRepo instanceRepo = repoMgr.getInstanceRepo();
            return instanceRepo.findByAccountId(accountId);
        } catch (DBNotFoundException e) {
            return new HashSet<Integer>();
        }
    }
}
