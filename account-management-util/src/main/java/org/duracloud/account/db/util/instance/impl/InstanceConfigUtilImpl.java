/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.instance.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.config.AmaEndpoint;
import org.duracloud.account.db.model.ComputeProviderAccount;
import org.duracloud.account.db.model.DuracloudInstance;
import org.duracloud.account.db.model.DuracloudMill;
import org.duracloud.account.db.model.ServerDetails;
import org.duracloud.account.db.model.StorageProviderAccount;
import org.duracloud.account.db.repo.DuracloudAccountRepo;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.repo.DuracloudStorageProviderAccountRepo;
import org.duracloud.account.db.util.DuracloudMillConfigService;
import org.duracloud.account.db.util.instance.InstanceConfigUtil;
import org.duracloud.account.db.util.instance.InstanceUtil;
import org.duracloud.account.db.util.notification.NotificationMgrConfig;
import org.duracloud.appconfig.domain.DurabossConfig;
import org.duracloud.appconfig.domain.DuradminConfig;
import org.duracloud.appconfig.domain.DurastoreConfig;
import org.duracloud.appconfig.domain.NotificationConfig;
import org.duracloud.storage.domain.AuditConfig;
import org.duracloud.storage.domain.StorageAccount;
import org.duracloud.storage.domain.impl.StorageAccountImpl;

/**
 * @author: Bill Branan
 * Date: 2/22/11
 */
public class InstanceConfigUtilImpl implements InstanceConfigUtil {

    protected static final String DEFAULT_SSL_PORT = "443";
    protected static final String NOTIFICATION_TYPE = "EMAIL";

    private DuracloudInstance instance;
    private DuracloudRepoMgr repoMgr;
    private NotificationMgrConfig notMgrConfig;
    private DuracloudMillConfigService duracloudMillService;
    private AmaEndpoint amaEndpoint;

    public InstanceConfigUtilImpl(DuracloudInstance instance,
                                  DuracloudRepoMgr repoMgr,
                                  NotificationMgrConfig notMgrConfig,
                                  AmaEndpoint amaEndpoint,
                                  DuracloudMillConfigService duracloudMillService) {
        this.instance = instance;
        this.repoMgr = repoMgr;
        this.notMgrConfig = notMgrConfig;
        this.amaEndpoint = amaEndpoint;
        this.duracloudMillService = duracloudMillService;
        
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
        Set<StorageAccount> storageAccts = new HashSet<>();
        ServerDetails serverDetails = getAccount().getServerDetails();

        // Primary Storage Provider
        StorageProviderAccount primaryProviderAccount =
            serverDetails.getPrimaryStorageProviderAccount();
        storageAccts.add(getStorageAccount(primaryProviderAccount,
                                           true));
        // Secondary Storage Providers
        Set<StorageProviderAccount> storageProviderAccounts =
            serverDetails.getSecondaryStorageProviderAccounts();
        for(StorageProviderAccount storageProviderAccount : storageProviderAccounts) {
            storageAccts.add(getStorageAccount(storageProviderAccount,
                                               false));
        }

        DuracloudMill mill = duracloudMillService.get();
        
        ComputeProviderAccount compute = serverDetails.getComputeProviderAccount();
        AuditConfig audit = config.getAuditConfig();
        audit.setAuditQueueName(mill.getAuditQueue());
        audit.setAuditUsername(compute.getUsername());
        audit.setAuditPassword(compute.getPassword());
        config.setStorageAccounts(storageAccts);
        return config;
    }

    private AccountInfo getAccount() {
        DuracloudAccountRepo accountRepo = repoMgr.getAccountRepo();
        return accountRepo.findOne(instance.getAccount().getId());
    }

    private StorageAccount getStorageAccount(StorageProviderAccount provider,
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

        Map<String, String> providerProps = provider.getProperties();
        if(null != providerProps && providerProps.size() > 0) {
            for(String propKey : providerProps.keySet()) {
                String propValue = providerProps.get(propKey);
                storageAccount.setOption(propKey, propValue);
            }
        }

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
