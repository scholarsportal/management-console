/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountCreationInfo;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.sys.EventMonitor;
import org.duracloud.storage.domain.StorageProviderType;
import org.easymock.Capture;
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

    @Test
    public void testCreateAccount() throws Exception {
        setUpCreateAccount();
        accountManagerService = new AccountManagerServiceImpl(repoMgr,
                                                              userService,
                                                              accountServiceFactory,
                                                              providerAccountUtil,
                                                              eventMonitors);

        int userId = 0;
        DuracloudUser user = new DuracloudUser(userId,
                                               "testuser",
                                               "password",
                                               "Primo",
                                               "Ultimo",
                                               "primo@ultimo.org");

        int acctId = 0;
        String subdomain = "testdomain";
        AccountCreationInfo info = newAccountCreationInfo(acctId, subdomain);

        AccountService as = accountManagerService.createAccount(info, user);
        Assert.assertNotNull(as);
    }

    private void setUpCreateAccount() throws Exception {
        EasyMock.expect(providerAccountUtil.createEmptyComputeProviderAccount())
            .andReturn(1)
            .times(1);
        EasyMock.expect(
            providerAccountUtil.createEmptyStorageProviderAccount(
                EasyMock.isA(StorageProviderType.class)))
            .andReturn(1)
            .times(1);

        userService = EasyMock.createMock(DuracloudUserService.class);
        EasyMock.expect(userService.setUserRights(EasyMock.anyInt(),
                                                  EasyMock.anyInt(),
                                                  EasyMock.isA(Role.class)))
            .andReturn(true);
        EasyMock.replay(userService);

        for (int id : createIds(NUM_ACCTS)) {
            EasyMock.expect(accountRepo.findById(EasyMock.anyInt())).andReturn(
                newAccountInfo(id));
        }

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

        EasyMock.expect(accountServiceFactory.getAccount(EasyMock.isA(
            AccountInfo.class))).andReturn(accountService);

        replayMocks();
    }

    @Test
    public void testLookupAccountsByUsername() throws Exception {
        setUpLookupAccountsByUsername();
        accountManagerService = new AccountManagerServiceImpl(repoMgr,
                                                              userService,
                                                              accountServiceFactory,
                                                              providerAccountUtil,
                                                              eventMonitors);

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
        accountManagerService = new AccountManagerServiceImpl(repoMgr,
                                                              userService,
                                                              accountServiceFactory,
                                                              providerAccountUtil,
                                                              eventMonitors);

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
    public void testCheckSubdomain() throws Exception {
        setUpCheckSubdomain();
        accountManagerService = new AccountManagerServiceImpl(repoMgr,
                                                              userService,
                                                              accountServiceFactory,
                                                              providerAccountUtil,
                                                              eventMonitors);

        // success case
        String subdomain = "random-subdomain";
        Assert.assertTrue(accountManagerService.subdomainAvailable(subdomain));

        // failure case
        int acctId = 1;
        subdomain = "subdomain-" + acctId;
        Assert.assertFalse(accountManagerService.subdomainAvailable(subdomain));
    }

    private void setUpCheckSubdomain() throws Exception {
        int acctId = 1;
        EasyMock.expect(accountRepo.findById(EasyMock.anyInt())).andReturn(
            newAccountInfo(acctId)).anyTimes();

        replayMocks();
    }

}