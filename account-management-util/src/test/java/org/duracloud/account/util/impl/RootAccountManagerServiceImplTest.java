/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.notification.Emailer;
import org.easymock.classextension.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class RootAccountManagerServiceImplTest extends DuracloudServiceTestBase {

    private RootAccountManagerServiceImpl rootService;

    @Test
    public void testAddDuracloudImage() {
        //TODO implement test;
        replayMocks();
    }

    @Test
    public void testListAllAccounts() throws Exception {
        setUpListAllAccounts();
        rootService = new RootAccountManagerServiceImpl(repoMgr,
                                                        notificationMgr,
                                                        propagator);

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
        rootService = new RootAccountManagerServiceImpl(repoMgr,
                                                        notificationMgr,
                                                        propagator);

        String filter = "org-1";
        Set<AccountInfo> acctInfos = rootService.listAllAccounts(filter);
        Assert.assertNotNull(acctInfos);
        Assert.assertEquals(1, acctInfos.size());
    }

    @Test
    public void testListAllUsersNoFilter() throws Exception {
        setUpListAllUsers();
        rootService = new RootAccountManagerServiceImpl(repoMgr,
                                                        notificationMgr,
                                                        propagator);

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
        rootService = new RootAccountManagerServiceImpl(repoMgr,
                                                        notificationMgr,
                                                        propagator);

        String filter = "a-user-name-2";
        Set<DuracloudUser> users = rootService.listAllUsers(filter);
        Assert.assertNotNull(users);
        Assert.assertEquals(1, users.size());
    }

    @Test
    public void testDeleteUser() throws Exception {
        setUpDeleteUser();
        rootService = new RootAccountManagerServiceImpl(repoMgr,
                                                        notificationMgr,
                                                        propagator);

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
    public void testResetUsersPassword() throws Exception {
        setUpResetUsersPassword();
        rootService = new RootAccountManagerServiceImpl(repoMgr,
                                                        notificationMgr,
                                                        propagator);

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

        propagator.propagatePasswordUpdate(EasyMock.anyInt(), EasyMock.anyInt());
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
