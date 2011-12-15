/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.ComputeProviderAccount;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.notification.Emailer;
import org.duracloud.storage.domain.StorageProviderType;
import org.duracloud.computeprovider.domain.ComputeProviderType;
import org.easymock.classextension.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

import java.util.HashSet;
import java.util.Set;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class RootAccountManagerServiceImplTest extends DuracloudServiceTestBase {

    private RootAccountManagerServiceImpl rootService;

    @Before
    @Override
    public void before() throws Exception {
        super.before();

        rootService = new RootAccountManagerServiceImpl(repoMgr,
                                                        notificationMgr,
                                                        propagator,
                                                        instanceManagerService);
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
    public void testDeleteUser() throws Exception {
        setUpDeleteUser();

        rootService.deleteUser(1);
    }

    private void setUpDeleteUser() throws Exception {
        Set<AccountRights> accountRights = new HashSet<AccountRights>();
        accountRights.add(new AccountRights(1,1,1,null));

        EasyMock.expect(rightsRepo.findByUserId(EasyMock.anyInt()))
            .andReturn(accountRights);

        rightsRepo.delete(EasyMock.anyInt());
        EasyMock.expectLastCall();

        propagator.propagateRevocation(EasyMock.anyInt(), EasyMock.anyInt());
        EasyMock.expectLastCall();

        userRepo.delete(EasyMock.anyInt());
        EasyMock.expectLastCall();
        replayMocks();
    }

    @Test
    public void testDeleteAccount() throws Exception {
        setUpDeleteAccount();

        rootService.deleteAccount(1);
    }

    private void setUpDeleteAccount() throws Exception {
        EasyMock.expect(instanceManagerService.getInstanceServices(EasyMock.anyInt()))
            .andReturn(new HashSet<DuracloudInstanceService>());

        EasyMock.expect(accountRepo.findById(EasyMock.anyInt()))
            .andReturn(newAccountInfo(1));

        storageProviderAcctRepo.delete(EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();

        computeProviderAcctRepo.delete(EasyMock.anyInt());
        EasyMock.expectLastCall();

        Set<AccountRights> accountRights = new HashSet<AccountRights>();
        accountRights.add(new AccountRights(1,1,1,null));

        EasyMock.expect(rightsRepo.findByAccountIdSkipRoot(EasyMock.anyInt()))
            .andReturn(accountRights);

        rightsRepo.delete(EasyMock.anyInt());
        EasyMock.expectLastCall();

        EasyMock.expect(invitationRepo.findByAccountId(EasyMock.anyInt()))
            .andReturn(new HashSet<UserInvitation>());

        invitationRepo.delete(EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();

        accountRepo.delete(EasyMock.anyInt());
        EasyMock.expectLastCall();
        replayMocks();
    }

    @Test
    public void testGetSecondaryStorageProviders() throws Exception {
        setUpGetSecondaryStorageProviders();

        rootService.getSecondaryStorageProviders(1);
    }

    private void setUpGetSecondaryStorageProviders() throws Exception {
        EasyMock.expect(accountRepo.findById(EasyMock.anyInt()))
            .andReturn(newAccountInfo(1));
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
        setUpResetUsersPassword();

        rootService.resetUsersPassword(1);
    }

    private void setUpResetUsersPassword() throws Exception {
        DuracloudUser user = newDuracloudUser(1, "test");

        EasyMock.expect(userRepo.findById(EasyMock.anyInt()))
            .andReturn(user);
        
        userRepo.save(user);
        EasyMock.expectLastCall();

        Set<AccountRights> rights = new HashSet<AccountRights>();
        rights.add(
            new AccountRights(0, 1, 1, Role.ROLE_ADMIN.getRoleHierarchy()));

        EasyMock.expect(rightsRepo.findByUserId(EasyMock.anyInt()))
            .andReturn(rights);

        propagator.propagateUserUpdate(EasyMock.anyInt(), EasyMock.anyInt());
        EasyMock.expectLastCall();

        Emailer emailer = EasyMock.createMock("Emailer",
                                              Emailer.class);
        emailer.send(EasyMock.anyObject(String.class),
                     EasyMock.anyObject(String.class),
                     EasyMock.anyObject(String.class));
        EasyMock.expectLastCall();

        EasyMock.expect(notificationMgr.getEmailer())
            .andReturn(emailer);

        EasyMock.replay(emailer);
        replayMocks();
    }

}
