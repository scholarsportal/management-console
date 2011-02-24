/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.instance.impl;

import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.ProviderAccount;
import org.duracloud.account.common.domain.ServerImage;
import org.duracloud.account.common.domain.ServiceRepository;
import org.duracloud.account.db.DuracloudProviderAccountRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.error.DuracloudProviderAccountNotAvailableException;
import org.duracloud.account.util.error.DuracloudServerImageNotAvailableException;
import org.duracloud.account.util.error.DuracloudServiceRepositoryNotAvailableException;
import org.duracloud.account.util.instance.InstanceConfigUtil;
import org.duracloud.appconfig.domain.DuradminConfig;
import org.duracloud.appconfig.domain.DuraserviceConfig;
import org.duracloud.appconfig.domain.DurastoreConfig;
import org.duracloud.storage.domain.StorageAccount;
import org.duracloud.storage.domain.StorageProviderType;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: Bill Branan
 * Date: 2/22/11
 */
public class InstanceConfigUtilImpl implements InstanceConfigUtil {

    protected static final String DEFAULT_SSL_PORT = "443";
    protected static final String DEFAULT_SERVICES_ADMIN_PORT = "8089";
    protected static final String DEFAULT_SERVICES_ADMIN_CONTEXT_PREFIX =
        "org.duracloud.services.admin_";
    protected static final String DEFAULT_DURASTORE_CONTEXT = "durastore";
    protected static final String DEFAULT_MSG_BROKER_PORT = "61617";
    protected static final String DEFAULT_SERVICE_COMPUTE_TYPE = "AMAZON_EC2";
    protected static final String DEFAULT_SERVICE_COMPUTE_IMAGE_ID = "unknown";

    private DuracloudInstance instance;
    private DuracloudRepoMgr repoMgr;

    public InstanceConfigUtilImpl(DuracloudInstance instance,
                                  DuracloudRepoMgr repoMgr) {
        this.instance = instance;
        this.repoMgr = repoMgr;
    }

    public DuradminConfig getDuradminConfig() {
        DuradminConfig config = new DuradminConfig();
        config.setDurastoreHost(instance.getHostName());
        config.setDurastorePort(DEFAULT_SSL_PORT);
        config.setDurastoreContext(DurastoreConfig.QUALIFIER);
        config.setDuraserviceHost(instance.getHostName());
        config.setDuraservicePort(DEFAULT_SSL_PORT);
        config.setDuraserviceContext(DuraserviceConfig.QUALIFIER);
        return config;
    }

    public DurastoreConfig getDurastoreConfig() {
        DurastoreConfig config = new DurastoreConfig();
        DuracloudProviderAccountRepo providerAcctRepo =
            repoMgr.getProviderAccountRepo();
        Set<StorageAccount> storageAccts = new HashSet<StorageAccount>();

        // Primary Storage Provider
        int primaryProviderAccountId =
            instance.getPrimaryStorageProviderAccountId();
        storageAccts.add(getStorageAccount(providerAcctRepo,
                                           primaryProviderAccountId));
        // Secondary Storage Providers
        Set<Integer> providerAccountIds =
            instance.getSecondaryStorageProviderAccountIds();
        for(int providerAccountId : providerAccountIds) {
            storageAccts.add(getStorageAccount(providerAcctRepo,
                                               providerAccountId));
        }

        config.setStorageAccounts(storageAccts);
        return config;
    }

    private StorageAccount getStorageAccount(
        DuracloudProviderAccountRepo providerAcctRepo,
        int providerAccountId) {
        try {
            ProviderAccount provider =
                providerAcctRepo.findById(providerAccountId);
            StorageAccount storageAccount =
                new StorageAccount(String.valueOf(providerAccountId),
                                   provider.getUsername(),
                                   provider.getPassword(),
                                   translateType(provider.getProviderType()));
            return storageAccount;
        } catch(DBNotFoundException e) {
            String error = "Storage Provider Account with ID: " +
                providerAccountId + " does not exist in the database.";
            throw new DuracloudProviderAccountNotAvailableException(error, e);
        }
    }

    protected StorageProviderType translateType(ProviderAccount.ProviderType type) {
        if(type.equals(type.AMAZON)) {
            return StorageProviderType.AMAZON_S3;
        } else if(type.equals(type.RACKSPACE)) {
            return StorageProviderType.RACKSPACE;
        } else if(type.equals(type.MICROSOFT)) {
            return StorageProviderType.MICROSOFT_AZURE;
        } else {
            return StorageProviderType.UNKNOWN;
        }
    }

    public DuraserviceConfig getDuraserviceConfig() {
        DuraserviceConfig config = new DuraserviceConfig();

        String instanceHost = instance.getHostName();

        // Get the Server Image (for version)
        String version;
        try {
            ServerImage image =
                repoMgr.getServerImageRepo().findById(instance.getImageId());
            version = image.getVersion();
        } catch(DBNotFoundException e) {
            String error = "Server Image with ID: " + instance.getImageId() +
                           " does not exist in the database.";
            throw new DuracloudServerImageNotAvailableException(error, e);
        }

        // Primary Instance
        DuraserviceConfig.PrimaryInstance primaryInstance =
            new DuraserviceConfig.PrimaryInstance();
        primaryInstance.setHost(instanceHost);
        primaryInstance.setServicesAdminPort(DEFAULT_SERVICES_ADMIN_PORT);
        primaryInstance.setServicesAdminContext(
            DEFAULT_SERVICES_ADMIN_CONTEXT_PREFIX + version);
        config.setPrimaryInstance(primaryInstance);

        // User Store
        DuraserviceConfig.UserStore userStore
            = new DuraserviceConfig.UserStore();
        userStore.setHost(instanceHost);
        userStore.setPort(DEFAULT_SSL_PORT);
        userStore.setContext(DEFAULT_DURASTORE_CONTEXT);
        userStore.setMsgBrokerUrl("tcp://" + instanceHost + ":" +
                                      DEFAULT_MSG_BROKER_PORT);
        config.setUserStore(userStore);

        // Get Service Repo (for: host, space-id, username, password)
        // Just use the first item on the service-repos list for now
        String serviceStoreHost;
        String serviceStoreSpaceId;
        String serviceStoreUsername;
        String serviceStorePassword;
        Set<Integer> repoIds = instance.getServiceRepositoryIds();
        if(null != repoIds && repoIds.size() > 0) {
            int serviceRepoId = repoIds.iterator().next();
            try {
                ServiceRepository serviceRepo =
                    repoMgr.getServiceRepositoryRepo().findById(serviceRepoId);
                serviceStoreHost = serviceRepo.getHostName();
                serviceStoreSpaceId = serviceRepo.getSpaceId();
                serviceStoreUsername = serviceRepo.getUsername();
                serviceStorePassword = serviceRepo.getPassword();
            } catch(DBNotFoundException e) {
                String error = "Server Image with ID: " + instance.getImageId() +
                               " does not exist in the database.";
                throw new DuracloudServiceRepositoryNotAvailableException(error, e);
            }
        } else {
            String error = "Instance with ID: " + instance.getId() +
                           " does not include any service repositories " +
                           "in the database.";
            throw new DuracloudServiceRepositoryNotAvailableException(error);
        }

        // Service Store
        DuraserviceConfig.ServiceStore serviceStore
            = new DuraserviceConfig.ServiceStore();
        serviceStore.setHost(serviceStoreHost);
        serviceStore.setPort(DEFAULT_SSL_PORT);
        serviceStore.setContext(DEFAULT_DURASTORE_CONTEXT);
        serviceStore.setSpaceId(serviceStoreSpaceId);
        config.setServiceStore(serviceStore);

        // Service Compute
        DuraserviceConfig.ServiceCompute serviceCompute
            = new DuraserviceConfig.ServiceCompute();
        serviceCompute.setType(DEFAULT_SERVICE_COMPUTE_TYPE);
        serviceCompute.setImageId(DEFAULT_SERVICE_COMPUTE_IMAGE_ID);
        serviceCompute.setUsername(serviceStoreUsername);
        serviceCompute.setPassword(serviceStorePassword);
        config.setServiceCompute(serviceCompute);

        return config;
    }
    
}
