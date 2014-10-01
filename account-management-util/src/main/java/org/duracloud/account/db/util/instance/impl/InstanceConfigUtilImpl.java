/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.instance.impl;

import org.duracloud.account.compute.DuracloudComputeProvider;
import org.duracloud.account.db.model.*;
import org.duracloud.account.db.repo.DuracloudAccountRepo;
import org.duracloud.account.db.repo.DuracloudComputeProviderAccountRepo;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.repo.DuracloudStorageProviderAccountRepo;
import org.duracloud.account.db.util.instance.InstanceConfigUtil;
import org.duracloud.account.db.util.instance.InstanceUtil;
import org.duracloud.account.db.util.notification.NotificationMgrConfig;
import org.duracloud.appconfig.domain.*;
import org.duracloud.storage.domain.AuditConfig;
import org.duracloud.storage.domain.StorageAccount;
import org.duracloud.storage.domain.impl.StorageAccountImpl;

import java.util.*;

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
    protected static final String NOTIFICATION_TYPE = "EMAIL";

    private DuracloudInstance instance;
    private DuracloudRepoMgr repoMgr;
    private NotificationMgrConfig notMgrConfig;
    private AmaEndpoint amaEndpoint;

    public InstanceConfigUtilImpl(DuracloudInstance instance,
                                  DuracloudRepoMgr repoMgr,
                                  NotificationMgrConfig notMgrConfig,
                                  AmaEndpoint amaEndpoint) {
        this.instance = instance;
        this.repoMgr = repoMgr;
        this.notMgrConfig = notMgrConfig;
        this.amaEndpoint = amaEndpoint;
    }

    public DuradminConfig getDuradminConfig() {
        DuradminConfig config = new DuradminConfig();
        config.setDurastoreHost(instance.getHostName());
        config.setDurastorePort(DEFAULT_SSL_PORT);
        config.setDurastoreContext(DurastoreConfig.QUALIFIER);
        config.setAmaUrl(amaEndpoint.getUrl());
        return config;
    }

    public DurastoreConfig getDurastoreConfig() {
        DurastoreConfig config = new DurastoreConfig();
        DuracloudStorageProviderAccountRepo storageProviderAcctRepo =
            repoMgr.getStorageProviderAccountRepo();
        Set<StorageAccount> storageAccts = new HashSet<StorageAccount>();
        ServerDetails serverDetails = getAccount().getServerDetails();

        // Primary Storage Provider
        StorageProviderAccount primaryProviderAccount =
            serverDetails.getPrimaryStorageProviderAccount();
        storageAccts.add(getStorageAccount(storageProviderAcctRepo,
                                           primaryProviderAccount,
                                           true));
        // Secondary Storage Providers
        Set<StorageProviderAccount> storageProviderAccounts =
            serverDetails.getSecondaryStorageProviderAccounts();
        for(StorageProviderAccount storageProviderAccount : storageProviderAccounts) {
            storageAccts.add(getStorageAccount(storageProviderAcctRepo,
                    storageProviderAccount,
                                               false));
        }

        ComputeProviderAccount compute = serverDetails.getComputeProviderAccount();
        AuditConfig audit = config.getAuditConfig();
        audit.setAuditQueueName(compute.getAuditQueue());
        audit.setAuditUsername(compute.getUsername());
        audit.setAuditPassword(compute.getPassword());
        config.setStorageAccounts(storageAccts);
        return config;
    }

    private AccountInfo getAccount() {
        DuracloudAccountRepo accountRepo = repoMgr.getAccountRepo();
        return accountRepo.findOne(instance.getAccount().getId());
    }

    private StorageAccount getStorageAccount(
        DuracloudStorageProviderAccountRepo storageProviderAcctRepo,
        StorageProviderAccount provider,
        boolean primary) {

        StorageAccount storageAccount =
            new StorageAccountImpl(String.valueOf(provider.getId()),
                               provider.getUsername(),
                               provider.getPassword(),
                               provider.getProviderType());
        storageAccount.setPrimary(primary);

        String storageClass = "rrs";
        if(!provider.isRrs())
            storageClass = "standard";

        storageAccount.setOption(StorageAccount.OPTS.STORAGE_CLASS.name(),
                                 storageClass);

        return storageAccount;
    }

    public DurabossConfig getDurabossConfig() {
        DurabossConfig config = new DurabossConfig();
        config.setDurastoreHost(instance.getHostName());
        config.setDurastorePort(DEFAULT_SSL_PORT);
        config.setDurastoreContext(DurastoreConfig.QUALIFIER);

        NotificationConfig notificationConfig = new NotificationConfig();
        notificationConfig.setType(NOTIFICATION_TYPE);
        notificationConfig.setUsername(notMgrConfig.getUsername());
        notificationConfig.setPassword(notMgrConfig.getPassword());
        notificationConfig.setOriginator(notMgrConfig.getFromAddress());

        List<String> admins = new ArrayList<>();
        admins.add(notMgrConfig.getAdminAddress());
        notificationConfig.setAdmins(admins);

        Map<String, NotificationConfig> notificationConfigMap =
            new HashMap<String, NotificationConfig>();
        notificationConfigMap.put("0", notificationConfig);
        config.setNotificationConfigs(notificationConfigMap);

        config.setDurabossContext(InstanceUtil.DURABOSS_CONTEXT);
        return config;
    }
    
}
