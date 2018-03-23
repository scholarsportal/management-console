/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.util.error.AccountNotFoundException;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class AccountControllerTest extends AmaControllerTestBase {
    private AccountController accountController;
    private Model model;

    @Before
    public void before() throws Exception {
        super.before();

        setupGenericAccountAndUserServiceMocks(TEST_ACCOUNT_ID);
        accountController = new AccountController();
        accountController.setAccountManagerService(this.accountManagerService);
        accountController.setUserService(this.userService);
        accountController.setAuthenticationManager(authenticationManager);
        model = new ExtendedModelMap();
    }

    /**
     * Test method for org.duracloud.account.app.controller.AccountController
     *
     * @throws AccountNotFoundException
     */
    @Test
    public void testGetHome()
        throws AccountNotFoundException {
        replayMocks();
        String view = accountController.getHome(TEST_ACCOUNT_ID, model);
        Assert.assertEquals(AccountController.ACCOUNT_HOME, view);
        Assert.assertTrue(model.containsAttribute(AccountController.ACCOUNT_INFO_KEY));
    }

    @Test
    public void testActivate() throws Exception {
        this.accountService.storeAccountStatus(AccountInfo.AccountStatus.ACTIVE);
        EasyMock.expectLastCall();
        accountController.setAccountManagerService(accountManagerService);
        replayMocks();
        accountController.activate(TEST_ACCOUNT_ID);
    }

    @Test
    public void testDeactivate() throws Exception {
        this.accountService.storeAccountStatus(AccountInfo.AccountStatus.INACTIVE);
        EasyMock.expectLastCall();
        accountController.setAccountManagerService(accountManagerService);
        replayMocks();
        accountController.deactivate(TEST_ACCOUNT_ID, model);
    }

    @Test
    public void testGetStatement()
        throws AccountNotFoundException {
        replayMocks();
        accountController.getStatement(TEST_ACCOUNT_ID, model);
        Assert.assertTrue(model.containsAttribute(AccountController.ACCOUNT_INFO_KEY));
    }

}
