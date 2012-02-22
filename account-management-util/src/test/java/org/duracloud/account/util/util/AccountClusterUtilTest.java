/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.util;

import org.duracloud.account.common.domain.AccountCluster;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.DuracloudAccountClusterRepo;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author: Bill Branan
 * Date: 2/17/12
 */
public class AccountClusterUtilTest {

    private DuracloudRepoMgr repoMgr;
    private DuracloudRightsRepo rightsRepo;
    private DuracloudUserRepo userRepo;
    private DuracloudAccountRepo accountRepo;
    private DuracloudAccountClusterRepo clusterRepo;
    private AccountInfo account;
    private AccountClusterUtil clusterUtil;
    private int accountId = 50;
    private int accountClusterId = 51;
    private int clusterAcctId1 = 22;
    private int clusterAcctId2 = 33;
    private String clusterName = "clusterName";

    @Before
    public void setup() {
        repoMgr = EasyMock.createMock("DuracloudRepoMgr",
                                      DuracloudRepoMgr.class);
        rightsRepo = EasyMock.createMock("DuracloudRightsRepo",
                                         DuracloudRightsRepo.class);
        userRepo = EasyMock.createMock("DuracloudUserRepo",
                                       DuracloudUserRepo.class);
        accountRepo = EasyMock.createMock("DuracloudAccountRepo",
                                          DuracloudAccountRepo.class);
        clusterRepo = EasyMock.createMock("DuracloudAccountClusterRepo",
                                          DuracloudAccountClusterRepo.class);
        account = EasyMock.createMock("AccountInfo", AccountInfo.class);

        clusterUtil = new AccountClusterUtil(repoMgr);
    }

    private void replayMocks() {
        EasyMock.replay(repoMgr, rightsRepo, userRepo, accountRepo,
                        clusterRepo, account);
    }

    @After
    public void teardown() {
        EasyMock.verify(repoMgr, rightsRepo, userRepo, accountRepo,
                        clusterRepo, account);
    }

    @Test
    public void testGetClusterAccountIds() throws Exception {
        setupGetClusterAccountIds();

        replayMocks();

        Set<Integer> acctIds = clusterUtil.getClusterAccountIds(account);
        assertNotNull(acctIds);
        assertEquals(3, acctIds.size());
        assertTrue(acctIds.contains(new Integer(accountId)));
        assertTrue(acctIds.contains(new Integer(clusterAcctId1)));
        assertTrue(acctIds.contains(new Integer(clusterAcctId2)));
    }

    private void setupGetClusterAccountIds() throws Exception {
        EasyMock.expect(account.getId()).andReturn(accountId);

        EasyMock.expect(account.getAccountClusterId())
                .andReturn(accountClusterId);

        EasyMock.expect(repoMgr.getAccountClusterRepo())
                .andReturn(clusterRepo);

        Set<Integer> acctIds = new HashSet<Integer>();
        acctIds.add(clusterAcctId1);
        acctIds.add(clusterAcctId2);
        AccountCluster cluster = new AccountCluster(0, "cluster-name", acctIds);
        EasyMock.expect(clusterRepo.findById(accountClusterId))
                .andReturn(cluster);
    }

    @Test
    public void testGetAccountClusterUsers() throws Exception {
        setupGetClusterAccountIds();

        EasyMock.expect(repoMgr.getRightsRepo()).andReturn(rightsRepo);
        EasyMock.expect(repoMgr.getUserRepo()).andReturn(userRepo);

        int userId = 5;
        Set<AccountRights> accountRights = new HashSet<AccountRights>();
        Set<Role> roles = new HashSet<Role>();
        roles.add(Role.ROLE_USER);
        accountRights.add(new AccountRights(0, accountId, userId, roles));
        EasyMock.expect(rightsRepo.findByAccountId(EasyMock.anyInt()))
            .andReturn(accountRights)
            .times(3);

        DuracloudUser user = new DuracloudUser(userId, "user", "pass", "first",
                                               "last","email", "ques", "ans");
        EasyMock.expect(userRepo.findById(userId))
            .andReturn(user)
            .times(3);

        replayMocks();

        Set<DuracloudUser> users =
            clusterUtil.getAccountClusterUsers(account);
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals(userId, user.getId());
        assertEquals(Role.ROLE_USER, user.getRoleByAcct(accountId));
    }

    @Test
    public void testAddAccountToCluster() throws Exception {
        EasyMock.expect(repoMgr.getAccountClusterRepo())
                .andReturn(clusterRepo);

        AccountCluster cluster =
            new AccountCluster(accountId, clusterName, new HashSet<Integer>());
        EasyMock.expect(clusterRepo.findById(accountClusterId))
                .andReturn(cluster);

        Capture<AccountCluster> capCluster = new Capture<AccountCluster>();
        clusterRepo.save(EasyMock.capture(capCluster));
        EasyMock.expectLastCall();

        replayMocks();

        clusterUtil.addAccountToCluster(accountId, accountClusterId);
        AccountCluster updatedCluster = capCluster.getValue();
        assertNotNull(updatedCluster);
        Set<Integer> clusterAccts = updatedCluster.getClusterAccountIds();
        assertEquals(1, clusterAccts.size());
        assertTrue(clusterAccts.contains(accountId));
    }

    @Test
    public void testRemoveAccountFromCluster() throws Exception {
        EasyMock.expect(repoMgr.getAccountClusterRepo())
                .andReturn(clusterRepo);

        Set<Integer> startAccts = new HashSet<Integer>();
        startAccts.add(accountId);
        AccountCluster cluster =
            new AccountCluster(accountId, clusterName, startAccts);
        EasyMock.expect(clusterRepo.findById(accountClusterId))
                .andReturn(cluster);

        Capture<AccountCluster> capCluster = new Capture<AccountCluster>();
        clusterRepo.save(EasyMock.capture(capCluster));
        EasyMock.expectLastCall();

        replayMocks();

        clusterUtil.removeAccountFromCluster(accountId, accountClusterId);
        AccountCluster updatedCluster = capCluster.getValue();
        assertNotNull(updatedCluster);
        Set<Integer> clusterAccts = updatedCluster.getClusterAccountIds();
        assertEquals(0, clusterAccts.size());
        assertFalse(clusterAccts.contains(accountId));
    }

    @Test
    public void testSetAccountCluster() throws Exception {
        EasyMock.expect(repoMgr.getAccountRepo())
                .andReturn(accountRepo);

        AccountInfo account = new AccountInfo(0, "sub", "name", "org", "dept",
                                              -1, -1, -1, null, null);
        EasyMock.expect(accountRepo.findById(accountId))
                .andReturn(account);

        Capture<AccountInfo> capAccount = new Capture<AccountInfo>();
        accountRepo.save(EasyMock.capture(capAccount));
        EasyMock.expectLastCall();

        replayMocks();

        clusterUtil.setAccountCluster(accountId, accountClusterId);
        AccountInfo updatedAccount = capAccount.getValue();
        assertNotNull(updatedAccount);
        assertEquals(accountClusterId, updatedAccount.getAccountClusterId());
    }

}
