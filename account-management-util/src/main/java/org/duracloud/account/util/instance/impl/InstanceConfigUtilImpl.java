/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.instance.impl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AmaEndpoint;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.ServerImage;
import org.duracloud.account.common.domain.ServicePlan;
import org.duracloud.account.common.domain.ServiceRepository;
import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudStorageProviderAccountRepo;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.error.DuracloudProviderAccountNotAvailableException;
import org.duracloud.account.util.error.DuracloudServerImageNotAvailableException;
import org.duracloud.account.util.error.DuracloudServiceRepositoryNotAvailableException;
import org.duracloud.account.util.error.InstanceAccountNotFoundException;
import org.duracloud.account.util.instance.InstanceConfigUtil;
import org.duracloud.account.util.instance.InstanceUtil;
import org.duracloud.appconfig.domain.DuradminConfig;
import org.duracloud.appconfig.domain.DuraserviceConfig;
import org.duracloud.appconfig.domain.DurastoreConfig;
import org.duracloud.appconfig.domain.DurareportConfig;
import org.duracloud.storage.domain.StorageAccount;
import org.duracloud.storage.domain.impl.StorageAccountImpl;

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
        config.setAmaUrl(AmaEndpoint.getUrl());
        return config;
    }

    public DurastoreConfig getDurastoreConfig() {
        DurastoreConfig config = new DurastoreConfig();
        DuracloudStorageProviderAccountRepo storageProviderAcctRepo =
            repoMgr.getStorageProviderAccountRepo();
        Set<StorageAccount> storageAccts = new HashSet<StorageAccount>();

        AccountInfo account = getAccount();

        // Primary Storage Provider
        int primaryProviderAccountId =
            account.getPrimaryStorageProviderAccountId();
        storageAccts.add(getStorageAccount(storageProviderAcctRepo,
                                           primaryProviderAccountId,
                                           true));
        // Secondary Storage Providers
        Set<Integer> providerAccountIds =
            account.getSecondaryStorageProviderAccountIds();
        for(int providerAccountId : providerAccountIds) {
            storageAccts.add(getStorageAccount(storageProviderAcctRepo,
                                               providerAccountId,
                                               false));
        }

        config.setStorageAccounts(storageAccts);
        return config;
    }

    private AccountInfo getAccount() {
        DuracloudAccountRepo accountRepo = repoMgr.getAccountRepo();
        try {
            return accountRepo.findById(instance.getAccountId());
        } catch(DBNotFoundException e) {
            throw new InstanceAccountNotFoundException(instance.getId(),
                                                       instance.getAccountId());
        }
    }

    private StorageAccount getStorageAccount(
        DuracloudStorageProviderAccountRepo storageProviderAcctRepo,
        int providerAccountId,
        boolean primary) {
        try {
            StorageProviderAccount provider =
                storageProviderAcctRepo.findById(providerAccountId);
            StorageAccount storageAccount =
                new StorageAccountImpl(String.valueOf(providerAccountId),
                                   provider.getUsername(),
                                   provider.getPassword(),
                                   provider.getProviderType());
            storageAccount.setPrimary(primary);
            return storageAccount;
        } catch(DBNotFoundException e) {
            String error = "Storage Provider Account with ID: " +
                providerAccountId + " does not exist in the database.";
            throw new DuracloudProviderAccountNotAvailableException(error, e);
        }
    }

    public DuraserviceConfig getDuraserviceConfig() {
        DuraserviceConfig config = new DuraserviceConfig();

        String instanceHost = instance.getHostName();

        // Get the Server Image (for version)
        String imageVersion;
        String servicesAdminVersion;
        try {
            ServerImage image =
                repoMgr.getServerImageRepo().findById(instance.getImageId());
            imageVersion= image.getVersion();
            servicesAdminVersion =
                imageVersion.replaceAll("-SNAPSHOT", ".SNAPSHOT");
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
            DEFAULT_SERVICES_ADMIN_CONTEXT_PREFIX + servicesAdminVersion);
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

        ServicePlan servicePlan = getAccount().getServicePlan();
        ServiceRepository serviceRepo;
        try {
            serviceRepo = repoMgr.getServiceRepositoryRepo()
                .findByVersionAndPlan(imageVersion, servicePlan);
        } catch(DBNotFoundException e) {
            String error = "No Service Repositories for version " +
                            imageVersion + " exist in the database.";
            throw new DuracloudServiceRepositoryNotAvailableException(error, e);
        }

        /* TODO: Make use of secondary service repos
        Set<Integer> secondaryRepoIds =
            getAccount().getSecondaryServiceRepositoryIds();
        */

        serviceStoreHost = serviceRepo.getHostName();
        serviceStoreSpaceId = serviceRepo.getSpaceId();
        serviceStoreUsername = serviceRepo.getUsername();
        serviceStorePassword = serviceRepo.getPassword();

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

    public DurareportConfig getDurareportConfig() {
        DurareportConfig config = new DurareportConfig();
        config.setDurastoreHost(instance.getHostName());
        config.setDurastorePort(DEFAULT_SSL_PORT);
        config.setDurastoreContext(DurastoreConfig.QUALIFIER);
        config.setDuraserviceHost(instance.getHostName());
        config.setDuraservicePort(DEFAULT_SSL_PORT);
        config.setDuraserviceContext(DuraserviceConfig.QUALIFIER);
        
        config.setDurareportContext(InstanceUtil.DURAREPORT_CONTEXT);
        return config;
    }
    
}
