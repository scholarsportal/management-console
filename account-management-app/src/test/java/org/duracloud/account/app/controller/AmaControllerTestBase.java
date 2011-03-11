/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.easymock.EasyMock;
import org.junit.Before;

import java.util.HashSet;
import java.util.Set;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class AmaControllerTestBase {
   protected static final String TEST_USERNAME = "testuser";
    protected static final Integer TEST_ACCOUNT_ID = 1;
    protected static final Integer TEST_INSTANCE_ID = 1;

    protected AccountManagerService accountManagerService;
    
    @Before
    public void before() throws Exception {
        setupSimpleAccountManagerService();
    }

    protected void setupSimpleAccountManagerService()
        throws AccountNotFoundException {
        this.accountManagerService =
            EasyMock.createMock(AccountManagerService.class);
        AccountService as = EasyMock.createMock(AccountService.class);
        EasyMock
            .expect(as.retrieveAccountInfo()).andReturn(createAccountInfo())
            .times(1);

        EasyMock.expect(accountManagerService.getAccount(TEST_ACCOUNT_ID)).andReturn(as);
        EasyMock.replay(accountManagerService, as);
    }

    /**
     * @return
     */
    protected AccountInfo createAccountInfo() {
        return new AccountInfo(
            TEST_ACCOUNT_ID, "testdomain", "test", "test", "test", 0, null,
            null);
    }

    protected DuracloudUser createUser() {
        DuracloudUser user =
            new DuracloudUser(0, TEST_USERNAME, "test", "test", "test", "test");
        Set<AccountRights> rights = new HashSet<AccountRights>();
        Set<Role> roles = new HashSet<Role>();
        roles.add(Role.ROLE_OWNER);
        rights.add(new AccountRights(1, TEST_ACCOUNT_ID.intValue(), 0, roles));
        user.setAccountRights(rights);
        return user;
    }
}
