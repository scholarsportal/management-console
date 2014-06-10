/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import java.util.HashSet;
import java.util.Set;

import org.duracloud.account.common.domain.AccountCluster;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.ComputeProviderAccount;
import org.duracloud.account.common.domain.DuracloudGroup;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.ServerImage;
import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.computeprovider.domain.ComputeProviderType;
import org.duracloud.storage.domain.StorageProviderType;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class RootAccountManagerServiceImplTest extends DuracloudServiceTestBase {

    private RootAccountManagerServiceImpl rootService;

    private DuracloudUserService us;
    
    @Before
    @Override
    public void before() throws Exception {
        super.before();

        us = EasyMock.createMock(DuracloudUserService.class);

        rootService = new RootAccountManagerServiceImpl(repoMgr,
                                                        notificationMgr,
                                                        propagator,
                                                        accountUtil,
                                                        instanceManagerService,
                                                        us);
        


    }

    @Override
    protected void replayMocks() {
        super.replayMocks();
        EasyMock.replay(us);

    }
    
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        EasyMock.verify(us);

    }
    @Test
    public void testAddDuracloudImage() {
        //TODO implement test;
        replayMocks();
    }

    @Test
    public void testListAllAccounts() throws Exception {
        setUpListAllAccounts();

        String filter = null;
        Set<AccountInfo> accountInfos = rootService.listAllAccounts(filter);
        Assert.assertNotNull(accountInfos);
        Assert.assertEquals(NUM_ACCTS, accountInfos.size());
    }

    private void setUpListAllAccounts() throws Exception {
        for (int acctId : createIds(NUM_ACCTS)) {
            AccountInfo acctInfo = newAccountInfo(acctId);
            EasyMock.expect(accountRepo.findById(acctId)).andReturn(acctInfo);
        }
        replayMocks();
    }

    @Test
    public void testListAllAccountsWithFilter() throws Exception {
        setUpListAllAccounts();

        String filter = "org-1";
        Set<AccountInfo> acctInfos = rootService.listAllAccounts(filter);
        Assert.assertNotNull(acctInfos);
        Assert.assertEquals(1, acctInfos.size());
    }

    @Test
    public void testListAllUsersNoFilter() throws Exception {
        setUpListAllUsers();

        String filter = null;
        Set<DuracloudUser> users = rootService.listAllUsers(filter);
        Assert.assertNotNull(users);
        Assert.assertEquals(NUM_USERS, users.size());
    }

    private void setUpListAllUsers() throws Exception {
        String username = "a-user-name-";
        for (int userId : createIds(NUM_USERS)) {
            EasyMock.expect(userRepo.findById(EasyMock.anyInt())).andReturn(
                newDuracloudUser(userId, username + userId));
        }
        EasyMock.expect(rightsRepo.findByUserId(EasyMock.anyInt()))
            .andReturn(null)
            .anyTimes();
        replayMocks();
    }

    @Test
    public void testListAllUsersFilter() throws Exception {
        setUpListAllUsers();

        String filter = "a-user-name-2";
        Set<DuracloudUser> users = rootService.listAllUsers(filter);
        Assert.assertNotNull(users);
        Assert.assertEquals(1, users.size());
    }

    @Test
    public void testListAllServerImages() throws Exception {
        int id = 1;
        ServerImage serverImage = new ServerImage(id,
                                                  1,
                                                  "",
                                                  "",
                                                  "",
                                                  "",
                                                  true);

        Set<Integer> ids = new HashSet<Integer>();
        ids.add(id);

        EasyMock.expect(serverImageRepo.getIds()).andReturn(ids);
        EasyMock.expect(serverImageRepo.findById(id)).andReturn(serverImage);

        replayMocks();

        String filter = null;
        Set<ServerImage> serverImages = rootService.listAllServerImages(filter);
        Assert.assertNotNull(serverImages);
        Assert.assertEquals(1, serverImages.size());
    }


    @Test
    public void testCreateServerImage() throws Exception {
        EasyMock.expect(idUtil.newServerImageId())
            .andReturn(1);
        serverImageRepo.save(EasyMock.isA(ServerImage.class));
        EasyMock.expectLastCall();
        EasyMock.expect(serverImageRepo.getIds()).andReturn(new HashSet<Integer>());
        replayMocks();

        rootService.createServerImage(1,
                                    "",
                                    "",
                                    "",
                                    "",
                                    false);
    }

    @Test
    public void testEditServerImage() throws Exception {
        ServerImage serverImage = new ServerImage(1,
                                                  1,
                                                  "",
                                                  "",
                                                  "",
                                                  "",
                                                  false);
        EasyMock.expect(serverImageRepo.findById(EasyMock.anyInt()))
            .andReturn(serverImage);

        serverImageRepo.save(EasyMock.isA(ServerImage.class));
        EasyMock.expectLastCall();

        replayMocks();

        rootService.editServerImage(1,
                                    1,
                                    "",
                                    "",
                                    "",
                                    "",
                                    false);
    }

    @Test
    public void testGetServerImage() throws Exception {
        EasyMock.expect(serverImageRepo.findById(EasyMock.anyInt()))
            .andReturn(null);

        replayMocks();
        
        rootService.getServerImage(1);
    }

    @Test
    public void testDeleteServerImage() throws Exception {
        serverImageRepo.delete(EasyMock.anyInt());
        EasyMock.expectLastCall();

        replayMocks();
        
        rootService.deleteServerImage(1);
    }


    @Test
    public void testDeleteUser() throws Exception {
        int userId = 21;
        setUpDeleteUser(userId);

        rootService.deleteUser(userId);
    }

    private void setUpDeleteUser(int userId) throws Exception {
        int accountId = 27;
        int rightsId = 28;
        Set<AccountRights> accountRights = new HashSet<AccountRights>();
        accountRights.add(new AccountRights(rightsId, accountId, userId, null));

        EasyMock.expect(rightsRepo.findByUserId(userId))
            .andReturn(accountRights);

        rightsRepo.delete(rightsId);
        EasyMock.expectLastCall();

        propagator.propagateRevocation(accountId, userId);
        EasyMock.expectLastCall();

        // Two groups have this user as a member, both should be updated
        Set<DuracloudGroup> groups = new HashSet<DuracloudGroup>();
        Set<Integer> groupUserIds1 = new HashSet<Integer>();
        groupUserIds1.add(userId);
        groupUserIds1.add(userId + 1);
        DuracloudGroup group1 =
            new DuracloudGroup(1, "group-1", 1, groupUserIds1);
        groups.add(group1);

        Set<Integer> groupUserIds2 = new HashSet<Integer>();
        groupUserIds2.add(userId);
        groupUserIds2.add(userId + 1);
        DuracloudGroup group2 =
            new DuracloudGroup(2, "group-2", 2, groupUserIds2);
        groups.add(group2);

        // Group update calls
        EasyMock.expect(groupRepo.findAllGroups()).andReturn(groups);
        groupRepo.save(group1);
        EasyMock.expectLastCall();
        groupRepo.save(group2);
        EasyMock.expectLastCall();

        userRepo.delete(userId);
        EasyMock.expectLastCall();
        replayMocks();
    }

    @Test
    public void testDeleteAccountCluster() throws Exception {
        int clusterId = 12;
        String clusterName = "clusterName";
        int acctId1 = 35;
        int acctId2 = 36;
        Set<Integer> accounts = new HashSet<Integer>();
        accounts.add(acctId1);
        accounts.add(acctId2);
        AccountCluster cluster =
            new AccountCluster(clusterId, clusterName, accounts);
        EasyMock.expect(accountClusterRepo.findById(clusterId))
                .andReturn(cluster);

        // First account set to have no cluster
        EasyMock.expect(accountRepo.findById(acctId1))
                .andReturn(newAccountInfo(acctId1));
        Capture<AccountInfo> capAcct1 = new Capture<AccountInfo>();
        accountRepo.save(EasyMock.capture(capAcct1));

        // Second account set to have no cluster
        EasyMock.expect(accountRepo.findById(acctId2))
                .andReturn(newAccountInfo(acctId2));
        Capture<AccountInfo> capAcct2 = new Capture<AccountInfo>();
        accountRepo.save(EasyMock.capture(capAcct2));

        propagator.propagateClusterUpdate(acctId1, clusterId);
        EasyMock.expectLastCall();

        propagator.propagateClusterUpdate(acctId2, clusterId);
        EasyMock.expectLastCall();

        accountClusterRepo.delete(clusterId);
        EasyMock.expectLastCall();

        replayMocks();

        rootService.deleteAccountCluster(clusterId);

        // Check first account
        AccountInfo info1 = capAcct1.getValue();
        Assert.assertNotNull(info1);
        Assert.assertEquals(acctId1, info1.getId());
        Assert.assertEquals(-1, info1.getAccountClusterId());

        // Check second account
        AccountInfo info2 = capAcct2.getValue();
        Assert.assertNotNull(info2);
        Assert.assertEquals(acctId2, info2.getId());
        Assert.assertEquals(-1, info2.getAccountClusterId());
    }


    @Test
    public void testDeleteAccount() throws Exception {
        int acctId = 7;
        Capture<AccountCluster> clusterCapture = setUpDeleteAccount(acctId);

        rootService.deleteAccount(acctId);

        AccountCluster cluster = clusterCapture.getValue();
        Assert.assertNotNull(cluster);
        Set<Integer> clusterAcctIds = cluster.getClusterAccountIds();
        Assert.assertNotNull(clusterAcctIds);
        Assert.assertEquals(1, clusterAcctIds.size());
        Assert.assertFalse(clusterAcctIds.contains(acctId));
    }

    private Capture<AccountCluster> setUpDeleteAccount(int acctId) throws Exception {
        EasyMock.expect(instanceManagerService.getInstanceServices(EasyMock.anyInt()))
            .andReturn(new HashSet<DuracloudInstanceService>());

        AccountInfo account = newAccountInfo(acctId);
        EasyMock.expect(accountRepo.findById(EasyMock.anyInt()))
            .andReturn(account);

        EasyMock.expect(accountUtil.getServerDetails(account))
            .andReturn(newServerDetails(0));


        storageProviderAcctRepo.delete(EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();

        computeProviderAcctRepo.delete(EasyMock.anyInt());
        EasyMock.expectLastCall();

        serverDetailsRepo.delete(EasyMock.anyInt());
        EasyMock.expectLastCall();

        Set<AccountRights> accountRights = new HashSet<AccountRights>();
        accountRights.add(new AccountRights(acctId,1,1,null));

        EasyMock.expect(rightsRepo.findByAccountIdSkipRoot(EasyMock.anyInt()))
            .andReturn(accountRights);

        rightsRepo.delete(EasyMock.anyInt());
        EasyMock.expectLastCall();

        Set<DuracloudGroup> groups = new HashSet<DuracloudGroup>();
        int group1Id = 1;
        int group2Id = 2;
        groups.add(new DuracloudGroup(group1Id, "group-1", acctId, null));
        groups.add(new DuracloudGroup(group2Id, "group-2", acctId, null));
        EasyMock.expect(groupRepo.findByAccountId(acctId)).andReturn(groups);
        groupRepo.delete(group1Id);
        EasyMock.expectLastCall();
        groupRepo.delete(group2Id);
        EasyMock.expectLastCall();

        EasyMock.expect(invitationRepo.findByAccountId(EasyMock.anyInt()))
            .andReturn(new HashSet<UserInvitation>());

        invitationRepo.delete(EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();

        // Set up removeAccountFromCluster()
        Set<Integer> accounts = new HashSet<Integer>();
        accounts.add(acctId);
        accounts.add(acctId + 1);
        int clusterId = account.getAccountClusterId();
        AccountCluster cluster =
            new AccountCluster(clusterId, "clusterName", accounts);
        EasyMock.expect(accountClusterRepo.findById(clusterId))
                .andReturn(cluster);
        Capture<AccountCluster> clusterCapture = new Capture<AccountCluster>();
        accountClusterRepo.save(EasyMock.capture(clusterCapture));
        EasyMock.expectLastCall();

        propagator.propagateClusterUpdate(acctId, clusterId);
        EasyMock.expectLastCall();

        accountRepo.delete(EasyMock.anyInt());
        EasyMock.expectLastCall();
        replayMocks();

        return clusterCapture;
    }

    @Test
    public void testGetSecondaryStorageProviders() throws Exception {
        setUpGetSecondaryStorageProviders();

        rootService.getSecondaryStorageProviders(1);
    }

    private void setUpGetSecondaryStorageProviders() throws Exception {
        AccountInfo account = newAccountInfo(1);
        EasyMock.expect(accountRepo.findById(EasyMock.anyInt()))
            .andReturn(account);
        EasyMock.expect(accountUtil.getServerDetails(account))
            .andReturn(newServerDetails(0));
        EasyMock.expect(storageProviderAcctRepo.findById(EasyMock.anyInt()))
            .andReturn(null)
            .anyTimes();
        replayMocks();
    }

    @Test
    public void testSetupStorageProvider() throws Exception {
        setUpStorageProvider();

        rootService.setupStorageProvider(1, "test", "test");
    }

    private void setUpStorageProvider() throws Exception {
        StorageProviderAccount provider = new StorageProviderAccount(1,
                                                                     StorageProviderType.AMAZON_S3,
                                                                     "test",
                                                                     "test",
                                                                     true);

        EasyMock.expect(storageProviderAcctRepo.findById(EasyMock.anyInt()))
            .andReturn(provider);

        storageProviderAcctRepo.save(EasyMock.isA(StorageProviderAccount.class));
        EasyMock.expectLastCall();
        replayMocks();
    }

    @Test
    public void testSetupComputeProvider() throws Exception {
        setUpComputeProvider();

        rootService.setupComputeProvider(1,
                                         "test",
                                         "test",
                                         "test",
                                         "test",
                                         "test");
    }

    private void setUpComputeProvider() throws Exception {
        ComputeProviderAccount provider = new ComputeProviderAccount(1,
                                                                     ComputeProviderType.AMAZON_EC2,
                                                                     "test",
                                                                     "test",
                                                                     "test",
                                                                     "test",
                                                                     "test");

        EasyMock.expect(computeProviderAcctRepo.findById(EasyMock.anyInt()))
            .andReturn(provider);

        computeProviderAcctRepo.save(EasyMock.isA(ComputeProviderAccount.class));
        EasyMock.expectLastCall();
        replayMocks();
    }

    @Test
    public void testGetAccount() throws Exception {
        setUpGetAccount();

        rootService.getAccount(1);
    }

    private void setUpGetAccount() throws Exception {
        EasyMock.expect(accountRepo.findById(EasyMock.anyInt()))
            .andReturn(null);
        replayMocks();
    }

    @Test
    public void testActivateAccount() throws Exception {
        setUpActivateAccount();

        rootService.activateAccount(1);
    }

    private void setUpActivateAccount() throws Exception {
        EasyMock.expect(accountRepo.findById(EasyMock.anyInt()))
            .andReturn(newAccountInfo(1));

        accountRepo.save(EasyMock.isA(AccountInfo.class));
        EasyMock.expectLastCall();
        replayMocks();
    }

    @Test
    public void testResetUsersPassword() throws Exception {
        DuracloudUser user = newDuracloudUser(1, "test");

        EasyMock.expect(userRepo.findById(EasyMock.anyInt()))
            .andReturn(user);
        
        us.forgotPassword(EasyMock.isA(String.class),EasyMock.isA(String.class),EasyMock.isA(String.class));
        EasyMock.expectLastCall();
        replayMocks();
        
        rootService.resetUsersPassword(1);

    }


}
