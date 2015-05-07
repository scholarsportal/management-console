/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.impl;

import org.duracloud.account.compute.ComputeProviderUtil;
import org.duracloud.account.compute.DuracloudComputeProvider;
import org.duracloud.account.compute.error.DuracloudInstanceNotAvailableException;
import org.duracloud.account.db.model.*;
import org.duracloud.account.db.repo.DuracloudInstanceRepo;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.repo.DuracloudServerImageRepo;
import org.duracloud.account.db.util.DuracloudInstanceManagerService;
import org.duracloud.account.db.util.DuracloudInstanceService;
import org.duracloud.account.db.util.DuracloudInstanceServiceFactory;
import org.duracloud.account.db.util.error.DuracloudInstanceCreationException;
import org.duracloud.account.db.util.util.AccountClusterUtil;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
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
    private AccountClusterUtil accountClusterUtil;
    private ComputeProviderUtil computeUtil;
    private DuracloudInstanceServiceFactory instanceServiceFactory;

    public DuracloudInstanceManagerServiceImpl(DuracloudRepoMgr repoMgr,
                                               AccountClusterUtil accountClusterUtil,
                                               ComputeProviderUtil computeUtil,
                                               DuracloudInstanceServiceFactory instanceServiceFactory) {
        this.repoMgr = repoMgr;
        this.accountClusterUtil = accountClusterUtil;
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
        List<ServerImage> images = imageRepo.findAll();
        Set<ServerImage> serverImages = new HashSet<ServerImage>();
        serverImages.addAll(images);
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
     * - The Image information for the latest release was
     *     added to the AMA database
     */
    @Override
    public DuracloudInstanceService createInstance(Long accountId,
                                                   String version,
                                                   InstanceType instanceType) {
        Set<ServerImage> serverImages = getServerImages();
        for (ServerImage image : serverImages) {
            if (version.equals(image.getVersion())) {
                return createInstance(accountId, image, instanceType);
            }
        }

        String err = "The DuraCloud version " + version + " is not available";
        throw new DuracloudInstanceCreationException(err);
    }

    private DuracloudInstanceService createInstance(Long accountId,
                                                    ServerImage image,
                                                    InstanceType instanceType) {
        log.info("Creating new instance for account {} using image {} ",
                 accountId, image.getDescription());
        DuracloudInstance instance = doCreateInstance(accountId, image, instanceType);
        return initializeInstance(instance);
    }

    protected DuracloudInstance doCreateInstance(Long accountId,
                                                 ServerImage image,
                                                 InstanceType instanceType) {
        // Get Account information
        AccountInfo account = repoMgr.getAccountRepo().findOne(accountId);

        if(account.getType().equals(AccountType.COMMUNITY)) {
            throw new DuraCloudRuntimeException("Cannot associate instance " +
                                                "with a community account");
        }

        // Create entry for new instance in DB
        String hostName = account.getSubdomain() + HOST_SUFFIX;
        DuracloudInstance instance = new DuracloudInstance();
        instance.setImage(image);
        instance.setAccount(account);
        instance.setHostName(hostName);
        instance.setProviderInstanceId(DuracloudInstance.PLACEHOLDER_PROVIDER_ID);
        instance.setInitialized(false);

        instance = repoMgr.getInstanceRepo().save(instance);


        // Get info about compute provider account associated with this account
        ServerDetails serverDetails = account.getServerDetails();
        if(serverDetails == null) {
            String err = "Cannot start instance for account with ID " +
                accountId +
                ". No ServerDetails are associated with this account.";
            throw new DuraCloudRuntimeException(err);
        }

        ComputeProviderAccount computeProviderAcct = serverDetails.getComputeProviderAccount();

        // Get access to a duracloud compute provider
        DuracloudComputeProvider computeProvider =
            computeUtil.getComputeProvider(computeProviderAcct.getUsername(),
                                           computeProviderAcct.getPassword());

        // Start the instance
        String providerInstanceId = null;
        
        try{
            providerInstanceId =
                computeProvider.start(image.getProviderImageId(),
                                      image.getIamRole(),
                                      computeProviderAcct.getSecurityGroup(),
                                      computeProviderAcct.getKeypair(),
                                      computeProviderAcct.getElasticIp(),
                                      instanceType,
                                      account.getSubdomain()+".duracloud.org");
    
        }catch(RuntimeException ex){
            repoMgr.getInstanceRepo().delete(instance.getId());
            throw new DuraCloudRuntimeException(ex.getMessage(), ex);
        }

        instance.setProviderInstanceId(providerInstanceId);
        repoMgr.getInstanceRepo().save(instance);
        return instance;
    }

    private DuracloudInstanceService initializeInstance(DuracloudInstance instance) {
        DuracloudInstanceService instanceService =
            instanceServiceFactory.getInstance(instance);

        instanceService.initialize();

        return instanceService;
    }


    @Override
    public DuracloudInstanceService getInstanceService(Long instanceId)
        throws DuracloudInstanceNotAvailableException {
        DuracloudInstanceService instanceService = null;
        DuracloudInstanceRepo instanceRepo = repoMgr.getInstanceRepo();
        DuracloudInstance instance = instanceRepo.findOne(instanceId);
        instanceService = instanceServiceFactory.getInstance(instance);

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
    public Set<DuracloudInstanceService> getInstanceServices(Long accountId) {

        List<DuracloudInstance> instances = getInstances(accountId);
        Set<DuracloudInstanceService> instanceServices =
            new HashSet<DuracloudInstanceService>();

        if(null != instances) {
            for(DuracloudInstance instance : instances) {
                try {
                    instanceServices.add(getInstanceService(instance.getId()));
                } catch(DuracloudInstanceNotAvailableException e) {
                    log.error("The instance with ID: " + instance.getId() +
                              " was found to be associated with the " +
                              "account with ID: " +  accountId +
                              " but the instance could not be found!");
                }
            }
        }
        return instanceServices;
    }

    private List<DuracloudInstance> getInstances(Long accountId) {
        DuracloudInstanceRepo instanceRepo = repoMgr.getInstanceRepo();
        return instanceRepo.findByAccountId(accountId);
    }

    @Override
    public Set<DuracloudInstanceService> getClusterInstanceServices(Long accountId) {
        AccountInfo account = repoMgr.getAccountRepo().findOne(accountId);
        Set<Long> accountIds = accountClusterUtil.getClusterAccountIds(account);
        Set<DuracloudInstanceService> allInstanceServices =
            new HashSet<DuracloudInstanceService>();
        for(Long clusterAccountId : accountIds) {
            Set<DuracloudInstanceService> instanceServices =
                getInstanceServices(clusterAccountId);
            if(null != instanceServices && instanceServices.size() > 0) {
                allInstanceServices.addAll(instanceServices);
            }
        }
        return allInstanceServices;
    }
}
