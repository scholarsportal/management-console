/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.easymock.classextension.EasyMock;
import org.junit.Assert;
import org.junit.Test;

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
        rootService = new RootAccountManagerServiceImpl(repoMgr);

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
        rootService = new RootAccountManagerServiceImpl(repoMgr);

        String filter = "org-1";
        Set<AccountInfo> acctInfos = rootService.listAllAccounts(filter);
        Assert.assertNotNull(acctInfos);
        Assert.assertEquals(1, acctInfos.size());
    }

    @Test
    public void testListAllUsersNoFilter() throws Exception {
        setUpListAllUsers();
        rootService = new RootAccountManagerServiceImpl(repoMgr);

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

        replayMocks();
    }

    @Test
    public void testListAllUsersFilter() throws Exception {
        setUpListAllUsers();
        rootService = new RootAccountManagerServiceImpl(repoMgr);

        String filter = "a-user-name-2";
        Set<DuracloudUser> users = rootService.listAllUsers(filter);
        Assert.assertNotNull(users);
        Assert.assertEquals(1, users.size());
    }

}
