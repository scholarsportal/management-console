/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.util.Arrays;
import java.util.HashSet;

import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.AccountService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class AccountUsersControllerTest extends AmaControllerTestBase {
    private AccountUsersController accountUsersController;

    @Before
    public void before() throws Exception {
        accountUsersController = new AccountUsersController();
    }

    @Test
    public void testGet() throws Exception {
        this.accountManagerService =
            EasyMock.createMock(AccountManagerService.class);
        AccountService as = EasyMock.createMock(AccountService.class);
        EasyMock
            .expect(as.retrieveAccountInfo()).andReturn(createAccountInfo())
            .times(1);

        EasyMock
        .expect(as.getPendingInvitations()).andReturn(new HashSet<UserInvitation>())
        .times(1);

        EasyMock.expect(as.getUsers()).andReturn(
            new HashSet<DuracloudUser>(Arrays
                .asList(new DuracloudUser[] { createUser() }))).times(1);

        EasyMock
            .expect(accountManagerService.getAccount(TEST_ACCOUNT_ID))
            .andReturn(as);
        EasyMock.replay(accountManagerService, as);

        this.accountUsersController
            .setAccountManagerService(accountManagerService);
        Model model = new ExtendedModelMap();
        accountUsersController.get(TEST_ACCOUNT_ID, model);
        Assert.assertTrue(model
            .containsAttribute(AccountController.ACCOUNT_INFO_KEY));
    }

}
