/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.IdUtil;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.DuracloudInstanceManagerService;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.sys.EventMonitor;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.HashSet;
import java.util.Set;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class AccountControllerTest extends AmaControllerTestBase {
    private AccountController accountController;
    private DuracloudInstanceManagerService instanceManagerService;
    private DuracloudInstanceService instanceService;

    @Before
    public void before() throws Exception {
        super.before();
        accountController = new AccountController();
        accountController.setAccountManagerService(this.accountManagerService);
    }

    /* FIXME: Properly set up mock account controller to pass verification - bb
    @After
    public void after() throws Exception {
        EasyMock.verify(accountController);
    }
    */

    /**
     * Test method for org.duracloud.account.app.controller.AccountController
     * 
     * @throws AccountNotFoundException
     */
    @Test
    public void testGetHome() throws AccountNotFoundException {
        Model model = new ExtendedModelMap();
        String view = accountController.getHome(TEST_ACCOUNT_ID, model);
        Assert.assertEquals(AccountController.ACCOUNT_HOME, view);
        Assert.assertTrue(model.containsAttribute(AccountController.ACCOUNT_INFO_KEY));
    }

    @Test
    public void testGetInstance() throws Exception {
        initializeMockInstanceManagerService(false);
        accountController.setInstanceManagerService(this.instanceManagerService);

        Model model = new ExtendedModelMap();
        accountController.getInstance(TEST_ACCOUNT_ID, model);
        Assert.assertTrue(model.containsAttribute(AccountController.ACCOUNT_INFO_KEY));

        EasyMock.verify(instanceManagerService, instanceService);
    }

    @Test
    public void testRestartInstance() throws Exception {
        initializeMockInstanceManagerService(true);
        accountController.setInstanceManagerService(this.instanceManagerService);

        Model model = new ExtendedModelMap();
        accountController.restartInstance(TEST_ACCOUNT_ID,
                                          TEST_INSTANCE_ID,
                                          model);
        Assert.assertTrue(model.containsAttribute(AccountController.ACCOUNT_INFO_KEY));
        Assert.assertTrue(model.containsAttribute(AccountController.ACTION_STATUS));

        EasyMock.verify(instanceManagerService, instanceService);
    }

    @Test
    public void testGetStatement() throws AccountNotFoundException {
        Model model = new ExtendedModelMap();
        accountController.getStatement(TEST_ACCOUNT_ID, model);
        Assert.assertTrue(model.containsAttribute(AccountController.ACCOUNT_INFO_KEY));
    }

    /**
     * Test method for
     * {@link org.duracloud.account.app.controller.AccountController #getNewForm()}
     * .
     */
    @Test
    public void testOpenAddForm() throws Exception {
        intializeAuthManager();
        initializeMockUserService();

        Model model = new ExtendedModelMap();
        String view = accountController.openAddForm(model);
        Assert.assertEquals(AccountController.NEW_ACCOUNT_VIEW, view);
        Assert.assertNotNull(model.asMap()
            .get(AccountController.NEW_ACCOUNT_FORM_KEY));
    }

    @Test
    public void testAdd() throws Exception {
        intializeAuthManager();
        initializeMockUserService();

        BindingResult result = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(result.hasErrors()).andReturn(true);
        EasyMock.expect(result.hasErrors()).andReturn(false);
        EasyMock.replay(result);

        IdUtil idUtil = EasyMock.createNiceMock(IdUtil.class);

        AccountService as = EasyMock.createMock("AccountService",
                                                AccountService.class);
        AccountInfo acctInfo = createAccountInfo();
        EasyMock.expect(as.retrieveAccountInfo())
            .andReturn(acctInfo)
            .anyTimes();

        AccountManagerService ams = EasyMock.createMock("AccountManagerService",
                                                        AccountManagerService.class);
        EasyMock.expect(ams.createAccount(EasyMock.isA(AccountInfo.class),
                                          EasyMock.isA(DuracloudUser.class)))
            .andReturn(as);

        EasyMock.replay(ams, as, idUtil);

        accountController.setAccountManagerService(ams);
        accountController.setIdUtil(idUtil);

        NewAccountForm newAccountForm = new NewAccountForm();
        newAccountForm.setSubdomain("testdomain");
        Model model = new ExtendedModelMap();

        // first time around has errors
        String view = accountController.add(newAccountForm, result, model);
        Assert.assertEquals(AccountController.NEW_ACCOUNT_VIEW, view);

        // second time okay
        view = accountController.add(newAccountForm, result, model);
        Assert.assertTrue(view.startsWith("redirect"));

    }

    private void initializeMockUserService() throws DBNotFoundException {
        DuracloudUserService userService =
            EasyMock.createMock(DuracloudUserService.class);
        EasyMock.expect(userService.loadDuracloudUserByUsername(TEST_USERNAME))
            .andReturn(createUser())
            .anyTimes();
        EasyMock.replay(userService);
        accountController.setUserService(userService);
    }

    private void intializeAuthManager() {
        SecurityContext ctx = new SecurityContextImpl();
        Authentication auth = EasyMock.createMock(Authentication.class);
        EasyMock.expect(auth.getName()).andReturn(TEST_USERNAME).anyTimes();
        AuthenticationManager authManager =
            EasyMock.createNiceMock(AuthenticationManager.class);
        EasyMock.replay(auth, authManager);
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
        accountController.setAuthenticationManager(authManager);
    }

    private void initializeMockInstanceManagerService(boolean restart) throws Exception {
        instanceManagerService =
            EasyMock.createMock(DuracloudInstanceManagerService.class);

        instanceService = EasyMock.createMock(DuracloudInstanceService.class);
        Set<DuracloudInstanceService> instanceServices =
            new HashSet<DuracloudInstanceService>();
        instanceServices.add(instanceService);

        if(!restart) {
            EasyMock.expect(
                instanceManagerService.getInstanceServices(EasyMock.anyInt()))
                .andReturn(instanceServices)
                .times(1);
        }

        EasyMock.expect(instanceService.getStatus())
            .andReturn("status")
            .times(1);

        DuracloudInstance instance =
            new DuracloudInstance(0, 0, "host", "providerInstanceId", 0, 0,
                                  null, null, "username", "password");
        EasyMock.expect(instanceService.getInstanceInfo())
            .andReturn(instance)
            .times(1);

        if(restart) {
            EasyMock.expect(instanceManagerService.
                getInstanceService(EasyMock.anyInt(), EasyMock.anyInt()))
                .andReturn(instanceService)
                .anyTimes();

            instanceService.restart();
            EasyMock.expectLastCall()
                .times(1);
        }

        EasyMock.replay(instanceManagerService, instanceService);
    }

}
