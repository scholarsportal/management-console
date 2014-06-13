/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.instance.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AmaEndpoint;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.ServerDetails;
import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudServerDetailsRepo;
import org.duracloud.account.db.DuracloudServerImageRepo;
import org.duracloud.account.db.DuracloudStorageProviderAccountRepo;
import org.duracloud.account.init.domain.AmaConfig;
import org.duracloud.account.util.instance.InstanceUtil;
import org.duracloud.account.util.notification.NotificationMgrConfig;
import org.duracloud.account.util.util.AccountUtil;
import org.duracloud.appconfig.domain.DurabossConfig;
import org.duracloud.appconfig.domain.DuradminConfig;
import org.duracloud.appconfig.domain.DurastoreConfig;
import org.duracloud.appconfig.domain.NotificationConfig;
import org.duracloud.storage.domain.StorageAccount;
import org.duracloud.storage.domain.StorageProviderType;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author: Bill Branan
 * Date: 2/24/11
 */
public class InstanceConfigUtilImplTest {

    protected InstanceConfigUtilImpl instanceConfigUtil;

    protected DuracloudInstance instance;
    protected AccountInfo account;
    protected ServerDetails serverDetails;
    protected DuracloudRepoMgr repoMgr;
    protected AmaConfig amaConfig;
    protected AccountUtil accountUtil;
    protected DuracloudAccountRepo accountRepo;
    protected DuracloudServerDetailsRepo serverDetailsRepo;
    protected DuracloudStorageProviderAccountRepo storageProviderAcctRepo;
    protected DuracloudServerImageRepo serverImageRepo;
    private String notificationUsername = "notUser";
    private String notificationPassword = "notPass";
    private String notificationFromAddress = "notAddress";
    private String adminAddr1 = "admin-addr-1";
    private String adminAddr2 = "admin-addr-2";
    private Collection<String> notificationAdminAddresses;

    @Before
    public void setup() throws Exception {
        Set<Integer> ids = new HashSet<Integer>();
        ids.add(1);

        notificationAdminAddresses = new ArrayList<String>();
        notificationAdminAddresses.add(adminAddr1);
        notificationAdminAddresses.add(adminAddr2);

        instance = EasyMock.createMock("DuracloudInstance",
                                       DuracloudInstance.class);
        account = EasyMock.createMock("AccountInfo", AccountInfo.class);
        serverDetails =
            EasyMock.createMock("ServerDetails", ServerDetails.class);
        repoMgr = EasyMock.createMock("DuracloudRepoMgr",
                                      DuracloudRepoMgr.class);
        accountUtil = EasyMock.createMock("AccountUtil", AccountUtil.class);
        accountRepo = EasyMock.createMock("DuracloudAccountRepo",
                                          DuracloudAccountRepo.class);
        storageProviderAcctRepo =
            EasyMock.createMock("DuracloudStorageProviderAccountRepo",
                                DuracloudStorageProviderAccountRepo.class);
        serverImageRepo =
            EasyMock.createMock("DuracloudServerImageRepo",
                                DuracloudServerImageRepo.class);
        serverDetailsRepo =
            EasyMock.createMock("DuracloudServerDetailsRepo",
                                DuracloudServerDetailsRepo.class);

        amaConfig =
                EasyMock.createMock("AmaConfig",
                                    AmaConfig.class);
        
        NotificationMgrConfig notConfig =
            new NotificationMgrConfig(notificationFromAddress,
                                      notificationUsername,
                                      notificationPassword,
                                      notificationAdminAddresses);

        instanceConfigUtil = new InstanceConfigUtilImpl(instance,
                                                        repoMgr,
                                                        accountUtil,
                                                        notConfig,
                                                        amaConfig);
    }

    protected void replayMocks() {
        EasyMock.replay(instance,
                        account,
                        serverDetails,
                        repoMgr,
                        accountUtil,
                        accountRepo,
                        storageProviderAcctRepo,
                        serverImageRepo,
                        serverDetailsRepo,
                        amaConfig);
    }

    @After
    public void teardown() {
        EasyMock.verify(instance,
                        account,
                        serverDetails,
                        repoMgr,
                        accountUtil,
                        accountRepo,
                        storageProviderAcctRepo,
                        serverImageRepo,
                        serverDetailsRepo,
                        amaConfig);
    }

    @Test
    public void testGetDuradminConfig() {
        String hostName = "host";
        EasyMock.expect(instance.getHostName())
            .andReturn(hostName)
            .times(1);

        replayMocks();

        DuradminConfig config = instanceConfigUtil.getDuradminConfig();
        assertNotNull(config);

        assertEquals(hostName, config.getDurastoreHost());
        assertEquals(InstanceConfigUtilImpl.DEFAULT_SSL_PORT,
                     config.getDurastorePort());
        assertEquals(DurastoreConfig.QUALIFIER,
                     config.getDurastoreContext());

        assertEquals(AmaEndpoint.getUrl(), config.getAmaUrl());

    }

    @Test
    public void testGetDurastoreConfig() throws Exception {
        int accountId = 12;
        setUpGetAccount(accountId);
        setUpGetServerDetails();

        int primaryProviderId = 0;
        EasyMock.expect(serverDetails.getPrimaryStorageProviderAccountId())
            .andReturn(primaryProviderId)
            .times(1);

        int secondaryProviderId = 1;
        Set<Integer> secondaryProviderIds = new HashSet<Integer>();
        secondaryProviderIds.add(secondaryProviderId);
        EasyMock.expect(serverDetails.getSecondaryStorageProviderAccountIds())
            .andReturn(secondaryProviderIds)
            .times(1);

        String pUser = "primaryUsername";
        String pPass = "primaryPassword";
        StorageProviderType primaryType = StorageProviderType.AMAZON_S3;
        StorageProviderAccount primaryStorageProviderAccount =
            new StorageProviderAccount(primaryProviderId, primaryType, pUser, pPass, true);
        EasyMock.expect(storageProviderAcctRepo.findById(primaryProviderId))
            .andReturn(primaryStorageProviderAccount);

        String sUser = "secondaryUsername";
        String sPass = "secondaryPassword";
        StorageProviderType secondaryType = StorageProviderType.RACKSPACE;
        StorageProviderAccount secondaryStorageProviderAccount =
            new StorageProviderAccount(secondaryProviderId, secondaryType, sUser, sPass, false);
        EasyMock.expect(storageProviderAcctRepo.findById(secondaryProviderId))
            .andReturn(secondaryStorageProviderAccount);

        EasyMock.expect(repoMgr.getStorageProviderAccountRepo())
            .andReturn(storageProviderAcctRepo)
            .times(1);

        EasyMock.expect(amaConfig.getAuditQueue()).andReturn("audit-queue");
        EasyMock.expect(amaConfig.getUsername()).andReturn("username");
        EasyMock.expect(amaConfig.getPassword()).andReturn("password");

        replayMocks();

        DurastoreConfig config = instanceConfigUtil.getDurastoreConfig();
        assertNotNull(config);

        Collection<StorageAccount> storageAccounts = config.getStorageAccounts();
        assertNotNull(storageAccounts);
        assertEquals(2, storageAccounts.size());

        for(StorageAccount storageAccount : storageAccounts) {
            assertNotNull(storageAccount);
            int storageAccountId = Integer.parseInt(storageAccount.getId());
            if(storageAccountId == primaryProviderId) {
                assertEquals(primaryType,
                             storageAccount.getType());
                assertEquals(pUser, storageAccount.getUsername());
                assertEquals(pPass, storageAccount.getPassword());
                assertEquals("rrs", storageAccount.getOptions().get(
                    StorageAccount.OPTS.STORAGE_CLASS.name()));
            } else if(storageAccountId == secondaryProviderId) {
                assertEquals(secondaryType,
                             storageAccount.getType());
                assertEquals(sUser, storageAccount.getUsername());
                assertEquals(sPass, storageAccount.getPassword());
                assertEquals("standard", storageAccount.getOptions().get(
                    StorageAccount.OPTS.STORAGE_CLASS.name()));
            } else {
                fail("Unexpected storage account, has ID:" + storageAccountId);
            }
        }
    }

    private void setUpGetAccount(int accountId) throws Exception {
        EasyMock.expect(repoMgr.getAccountRepo())
            .andReturn(accountRepo)
            .times(1);
        EasyMock.expect(accountRepo.findById(accountId))
            .andReturn(account)
            .times(1);
        EasyMock.expect(instance.getAccountId())
            .andReturn(accountId)
            .times(1);
    }

    private void setUpGetServerDetails() throws Exception {
        AccountInfo info = EasyMock.isA(AccountInfo.class);
        EasyMock.expect(accountUtil.getServerDetails(info))
            .andReturn(serverDetails);
    }



    @Test
    public void testGetDurabossConfig() {
        String hostName = "host";
        EasyMock.expect(instance.getHostName())
            .andReturn(hostName)
            .times(1);

        replayMocks();

        DurabossConfig config = instanceConfigUtil.getDurabossConfig();
        assertNotNull(config);

        assertEquals(hostName, config.getDurastoreHost());
        assertEquals(InstanceConfigUtilImpl.DEFAULT_SSL_PORT,
                     config.getDurastorePort());
        assertEquals(DurastoreConfig.QUALIFIER,
                     config.getDurastoreContext());

        assertEquals(InstanceUtil.DURABOSS_CONTEXT,
                     config.getDurabossContext());

        Collection<NotificationConfig> notConfigs =
            config.getNotificationConfigs();
        assertNotNull(notConfigs);
        assertEquals(1, notConfigs.size());

        NotificationConfig notConfig = notConfigs.iterator().next();
        assertNotNull(notConfig);
        assertEquals("EMAIL", notConfig.getType());
        assertEquals(notificationUsername, notConfig.getUsername());
        assertEquals(notificationPassword, notConfig.getPassword());
        assertEquals(notificationFromAddress, notConfig.getOriginator());

        List<String> admins = notConfig.getAdmins();
        assertTrue(admins.contains(adminAddr1));
        assertTrue(admins.contains(adminAddr2));
    }

}
