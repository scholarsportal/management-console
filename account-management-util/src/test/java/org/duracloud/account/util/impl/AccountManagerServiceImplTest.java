/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountCluster;
import org.duracloud.account.common.domain.AccountCreationInfo;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.AccountType;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.common.domain.ServerDetails;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountClusterDescriptor;
import org.duracloud.account.util.AccountClusterService;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.error.AccountClusterNotFoundException;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.sys.EventMonitor;
import org.duracloud.storage.domain.StorageProviderType;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class AccountManagerServiceImplTest extends DuracloudServiceTestBase {

    private AccountManagerServiceImpl accountManagerService;
    private DuracloudUserService userService;

    private EventMonitor systemMonitor;
    private EventMonitor customerMonitor;
    private Set<EventMonitor> eventMonitors;

    private static final int NUM_ACCTS = 4;
    private static final int NOT_AN_ACCT_ID = 98;

    @Before
    @Override
    public void before() throws Exception {
        super.before();

        systemMonitor = EasyMock.createMock("SystemMonitor",
                                            EventMonitor.class);
        customerMonitor = EasyMock.createMock("CustomerMonitor",
                                              EventMonitor.class);

        eventMonitors = new HashSet<EventMonitor>();
        eventMonitors.add(systemMonitor);
        eventMonitors.add(customerMonitor);
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        EasyMock.verify(systemMonitor, customerMonitor);
    }

    @Override
    protected void replayMocks() {
        super.replayMocks();
        EasyMock.replay(systemMonitor, customerMonitor);
    }

    private void setUpAccountManagerService() {
        accountManagerService =
            new AccountManagerServiceImpl(repoMgr,
                                          userService,
                                          accountServiceFactory,
                                          providerAccountUtil,
                                          clusterUtil,
                                          eventMonitors);
    }

    @Test
    public void testCreateFullAccount() throws Exception {
        testCreateAccount(AccountType.FULL);
    }

    @Test
    public void testCreateCommunityAccount() throws Exception {
        testCreateAccount(AccountType.COMMUNITY);
    }

    private void testCreateAccount(AccountType type) throws Exception {
        setUpCreateAccount(AccountType.FULL.equals(type));
        setUpAccountManagerService();

        int userId = 0;
        DuracloudUser user = new DuracloudUser(userId,
                                               "testuser",
                                               "password",
                                               "Primo",
                                               "Ultimo",
                                               "primo@ultimo.org",
                                               "question",
                                               "answer");

        int acctId = 0;
        String subdomain = "testdomain";
        AccountCreationInfo info =
            newAccountCreationInfo(acctId, subdomain, type);

        AccountService as = accountManagerService.createAccount(info, user);
        Assert.assertNotNull(as);
    }

    private void setUpCreateAccount(boolean fullAccount) throws Exception {
        if(fullAccount) {
            EasyMock.expect(idUtil.newServerDetailsId()).andReturn(0);
            serverDetailsRepo.save(EasyMock.isA(ServerDetails.class));
            EasyMock.expectLastCall();

            EasyMock.expect(
                providerAccountUtil.createEmptyComputeProviderAccount())
                .andReturn(1)
                .times(1);
            EasyMock.expect(
                providerAccountUtil.createEmptyStorageProviderAccount(
                    EasyMock.isA(StorageProviderType.class)))
                .andReturn(1)
                .times(1);
        }

        userService = EasyMock.createMock(DuracloudUserService.class);
        EasyMock.expect(userService.setUserRights(EasyMock.anyInt(),
                                                  EasyMock.anyInt(),
                                                  EasyMock.isA(Role.class)))
            .andReturn(true);
        EasyMock.replay(userService);

        EasyMock.expect(accountRepo.findBySubdomain(EasyMock.isA(String.class)))
            .andThrow(new DBNotFoundException("sudomain not found"))
            .times(1);

        accountRepo.save(EasyMock.isA(AccountInfo.class));
        EasyMock.expectLastCall().anyTimes();

        Set<Role> roles = new HashSet<Role>();
        roles.add(Role.ROLE_USER);
        AccountRights rights = new AccountRights(0, 0, 0, roles);
        EasyMock.expect(rightsRepo.findByAccountIdAndUserId(EasyMock.anyInt(),
                                                            EasyMock.anyInt()))
            .andReturn(rights)
            .anyTimes();

        EasyMock.expect(idUtil.newAccountId()).andReturn(NUM_ACCTS + 1);

        systemMonitor.accountCreated(EasyMock.isA(AccountCreationInfo.class),
                                     EasyMock.isA(DuracloudUser.class));
        EasyMock.expectLastCall();

        customerMonitor.accountCreated(EasyMock.isA(AccountCreationInfo.class),
                                       EasyMock.isA(DuracloudUser.class));
        EasyMock.expectLastCall();

        AccountService accountService = EasyMock.createMock("AccountService",
                                                            AccountService.class);
        EasyMock.replay(accountService);

        clusterUtil.addAccountToCluster(EasyMock.anyInt(), EasyMock.anyInt());
        EasyMock.expectLastCall();

        EasyMock.expect(accountServiceFactory.getAccount(EasyMock.isA(
            AccountInfo.class))).andReturn(accountService);

        replayMocks();
    }

    @Test
    public void testLookupAccountsByUsername() throws Exception {
        setUpLookupAccountsByUsername();
        setUpAccountManagerService();

        int userId = 1;
        Set<AccountInfo> infos = accountManagerService.findAccountsByUserId(
            userId);
        Assert.assertNotNull(infos);
        Assert.assertTrue(infos.size() > 0);
    }

    private void setUpLookupAccountsByUsername() throws Exception {
        Set<Role> roles = new HashSet<Role>();
        roles.add(Role.ROLE_USER);
        AccountRights rights = new AccountRights(0, 0, 0, roles);
        Set<AccountRights> rightsSet = new HashSet<AccountRights>();
        rightsSet.add(rights);
        EasyMock.expect(rightsRepo.findByUserId(EasyMock.anyInt())).andReturn(
            rightsSet).anyTimes();

        int acctId = 1;
        EasyMock.expect(accountRepo.findById(EasyMock.anyInt())).andReturn(
            newAccountInfo(acctId));

        replayMocks();
    }

    @Test
    public void testGetAccount() throws Exception {
        setUpGetAccount();
        setUpAccountManagerService();

        // success case
        int accountId = 1;
        AccountService service = accountManagerService.getAccount(accountId);
        Assert.assertNotNull(service);

        // failure case
        try {
            accountManagerService.getAccount(NOT_AN_ACCT_ID);
            Assert.fail("Exception expected");

        } catch (AccountNotFoundException e) {
            Assert.assertTrue(true);
        }
    }

    private void setUpGetAccount() throws Exception {
        int acctId = 1;
        AccountService service = EasyMock.createMock("AccountService",
                                                     AccountService.class);
        EasyMock.replay(service);

        EasyMock.expect(accountServiceFactory.getAccount(acctId)).andReturn(
            service);

        EasyMock.expect(accountServiceFactory.getAccount(NOT_AN_ACCT_ID))
            .andThrow(new AccountNotFoundException(NOT_AN_ACCT_ID));

        replayMocks();
    }

    @Test
    public void testCheckSubdomainTrue() throws Exception {
        String subdomain = "subdomain-available";
        EasyMock.expect(accountRepo.findBySubdomain(subdomain))
            .andThrow(new DBNotFoundException(subdomain + "not found"))
            .times(1);
        replayMocks();

        setUpAccountManagerService();

        // success case
        Assert.assertTrue(accountManagerService.subdomainAvailable(subdomain));
    }

    @Test
    public void testCheckSubdomainFalse() throws Exception {
        int acctId = 1;
        String subdomain = "subdomain-not-available";

        EasyMock.expect(accountRepo.findBySubdomain(subdomain))
            .andReturn(newAccountInfo(acctId))
            .times(1);
        replayMocks();

        setUpAccountManagerService();

        Assert.assertFalse(accountManagerService.subdomainAvailable(subdomain));
    }

    @Test
    public void testGetAccountCluster() throws Exception {
        int clusterId = 27;
        String clusterName = "clustery";
        AccountCluster cluster =
            new AccountCluster(clusterId, clusterName, new HashSet<Integer>());

        EasyMock.expect(accountClusterRepo.findById(clusterId))
                .andReturn(cluster);

        replayMocks();
        setUpAccountManagerService();

        // Expect failure
        try {
            accountManagerService.getAccountCluster(-5);
            Assert.fail("Expection expected with negative cluster ID");
        } catch(AccountClusterNotFoundException expected) {
            Assert.assertNotNull(expected);
        }

        // Expect success
        AccountClusterService clusterService =
            accountManagerService.getAccountCluster(clusterId);
        Assert.assertNotNull(clusterService);
        Assert.assertEquals(cluster, clusterService.retrieveAccountCluster());
    }

    @Test
    public void testCreateAccountCluster() throws Exception {
        int clusterId = 28;
        String clusterName = "clustery";
        EasyMock.expect(idUtil.newAccountClusterId())
                .andReturn(clusterId);
        accountClusterRepo.save(EasyMock.isA(AccountCluster.class));
        EasyMock.expectLastCall();

        replayMocks();
        setUpAccountManagerService();

        AccountClusterService clusterService =
            accountManagerService.createAccountCluster(clusterName);
        Assert.assertNotNull(clusterService);

        AccountCluster cluster = clusterService.retrieveAccountCluster();
        Assert.assertNotNull(cluster);
        Assert.assertEquals(clusterId, cluster.getId());
        Assert.assertEquals(clusterName, cluster.getClusterName());
    }

    
    @Test
    public void testAccountClusterList() throws Exception {

        int clusterId = 1;
        String clusterName = "clustery";
        AccountCluster cluster =
            new AccountCluster(clusterId, clusterName, new HashSet<Integer>());

        Set<Integer> ids = new HashSet<Integer>();
        ids.add(clusterId);

        EasyMock.expect(accountClusterRepo.getIds()).andReturn(ids).times(2);
        EasyMock.expect(accountClusterRepo.findById(clusterId)).andReturn(cluster).times(2);
        replayMocks();
        setUpAccountManagerService();
        
        //first pass no filter
        Set<AccountClusterDescriptor> clusters = accountManagerService.listAccountClusters(null);
        Assert.assertEquals(1, clusters.size());
        Assert.assertEquals(clusterName, clusters.iterator().next().getName());
        
        //second pass with filter
        Assert.assertEquals(0, accountManagerService.listAccountClusters("notclustery").size());
        
    }
    
}