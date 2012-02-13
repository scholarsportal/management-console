/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.util.HashSet;
import java.util.Set;

import org.duracloud.account.common.domain.AccountCreationInfo;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.util.DuracloudInstanceManagerService;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class AccountControllerTest extends AmaControllerTestBase {
    private AccountController accountController;
    private DuracloudInstanceManagerService instanceManagerService;
    private DuracloudInstanceService instanceService;
    private Model model;
    @Before
    public void before() throws Exception {
        super.before();
        
        setupGenericAccountAndUserServiceMocks(TEST_ACCOUNT_ID);
        
        this.instanceManagerService =
            EasyMock.createMock("DuracloudInstanceManagerService",
                                DuracloudInstanceManagerService.class);
        mocks.add(instanceManagerService);
        this.instanceService =
            EasyMock.createMock("DuracloudInstanceService",
                                DuracloudInstanceService.class);
        mocks.add(instanceService);
        
        accountController = new AccountController();
        accountController.setAccountManagerService(this.accountManagerService);
        accountController.setInstanceManagerService(this.instanceManagerService);
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
    public void testGetHome() throws AccountNotFoundException {
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
    public void testGetInstance() throws Exception {
        initializeMockInstanceManagerService();
        accountController.setInstanceManagerService(this.instanceManagerService);
        replayMocks();
        accountController.getInstance(TEST_ACCOUNT_ID, model);
        Assert.assertTrue(model.containsAttribute(AccountController.ACCOUNT_INFO_KEY));
    }


    @Test
    public void testRestartInstance() throws Exception {
        initializeMockInstanceManagerService();
        initRestart(TEST_ACCOUNT_ID);
        accountController.setInstanceManagerService(this.instanceManagerService);
        
        replayMocks();
        accountController.restartInstance(TEST_ACCOUNT_ID,
                                          TEST_INSTANCE_ID,
                                          model);
        verifyResult(model);
    }

    private void verifyResult(Model model) {
        Assert.assertTrue(model.containsAttribute(AccountController.ACCOUNT_INFO_KEY));
        Assert.assertTrue(model.containsAttribute(AccountController.ACTION_STATUS));
    }

    @Test
    public void testStartInstance() throws Exception {
        String version = "1.0";
        initStart(TEST_ACCOUNT_ID, version);
        replayMocks();
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        AccountInstanceForm instanceForm = new AccountInstanceForm();
        instanceForm.setVersion(version);
        accountController.startInstance(TEST_ACCOUNT_ID,
                                        instanceForm,
                                        redirectAttributes);
    }

    @Test
    public void testInstanceAvailable() throws Exception {
        initializeMockInstanceAvailable();
        replayMocks();
        
        accountController.instanceAvailable(TEST_ACCOUNT_ID,
                                            model);
    }

    @Test
    public void testReInitUsers() throws Exception {
        boolean initUsers = true;
        createReInitInstanceMocks(initUsers);
        replayMocks();
        
        accountController.reInitializeUserRoles(TEST_ACCOUNT_ID,
                                                TEST_INSTANCE_ID,
                                                model);
        verifyResult(model);
    }

    @Test
    public void testReInitInstance() throws Exception {
        boolean initUsers = false;
        createReInitInstanceMocks(initUsers);
        
        replayMocks();
        accountController.reInitialize(TEST_ACCOUNT_ID, TEST_INSTANCE_ID, model);
        verifyResult(model);
    }

    private void createReInitInstanceMocks(boolean initUsers) throws Exception {
        Set<DuracloudInstanceService> instanceServices = new HashSet<DuracloudInstanceService>();
        instanceServices.add(instanceService);

        EasyMock.expect(instanceManagerService.getInstanceServices(EasyMock.anyInt()))
            .andReturn(instanceServices);

        EasyMock.expect(instanceManagerService.getInstanceService(EasyMock.anyInt()))
            .andReturn(instanceService);

        if (initUsers) {
            instanceService.reInitializeUserRoles();
        } else {
            instanceService.reInitialize();
        }
        EasyMock.expectLastCall();

        EasyMock.expect(instanceService.getStatus()).andReturn("status");

        DuracloudInstance instance = new DuracloudInstance(0,
                                                           0,
                                                           0,
                                                           "host",
                                                           "providerInstanceId",
                                                           false);
        EasyMock.expect(instanceService.getInstanceInfo()).andReturn(instance);
    }

    @Test
    public void testStopInstance() throws Exception {
        initializeMockInstanceManagerService();
        initStop(TEST_ACCOUNT_ID);
        
        replayMocks();
        accountController.stopInstance(TEST_ACCOUNT_ID,
                                       TEST_INSTANCE_ID,
                                       model);
        verifyResult(model);
    }

    @Test
    public void testUpgradeInstance() throws Exception {
        initializeMockInstanceManagerService();
        initStop(TEST_ACCOUNT_ID);
        String version = "1.0";
        EasyMock.expect(instanceManagerService.getLatestVersion())
            .andReturn(version);
        initStart(TEST_ACCOUNT_ID, version);
        replayMocks();
        accountController.upgradeInstance(TEST_ACCOUNT_ID,
                                          TEST_INSTANCE_ID,
                                          model);
        verifyResult(model);
    }

    @Test
    public void testGetStatement() throws AccountNotFoundException {
        replayMocks();
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
        replayMocks();
        String view = accountController.openAddForm(model);
        Assert.assertEquals(AccountController.NEW_ACCOUNT_VIEW, view);
        Assert.assertNotNull(model.asMap()
            .get(AccountController.NEW_ACCOUNT_FORM_KEY));
    }

    @Test
    public void testAdd() throws Exception {
        EasyMock.expect(result.hasErrors()).andReturn(true);
        EasyMock.expect(result.hasErrors()).andReturn(false);

        EasyMock.expect(this.accountManagerService.createAccount(EasyMock.isA(AccountCreationInfo.class),
                                          EasyMock.isA(DuracloudUser.class)))
            .andReturn(accountService);

        NewAccountForm newAccountForm = new NewAccountForm();
        newAccountForm.setSubdomain("testdomain");

        replayMocks();
        // first time around has errors
        ModelAndView mav = accountController.add(newAccountForm, result, model);
        Assert.assertEquals(AccountController.NEW_ACCOUNT_VIEW, mav.getViewName());

        // second time okay
        mav = accountController.add(newAccountForm, result, model);
        Assert.assertNotNull(mav.getView());
    }

    private void initializeMockInstanceManagerService() throws Exception {
        initializeMockInstanceManagerService(1);
    }

    private void initializeMockInstanceManagerService(int times) throws Exception {
        Set<DuracloudInstanceService> instanceServices =
            new HashSet<DuracloudInstanceService>();
        instanceServices.add(instanceService);

        EasyMock.expect(
            instanceManagerService.getInstanceServices(EasyMock.anyInt()))
            .andReturn(instanceServices)
            .times(times);

        EasyMock.expect(instanceService.getStatus())
            .andReturn("status")
            .times(times);

        DuracloudInstance instance =
            new DuracloudInstance(0, 0, 0, "host", "providerInstanceId", false);
        EasyMock.expect(instanceService.getInstanceInfo())
            .andReturn(instance)
            .times(times);
    }

    private void initializeMockInstanceAvailable() throws Exception {
        DuracloudInstance instance =
            new DuracloudInstance(0, 0, 0, "host", "providerInstanceId", true);

        EasyMock.expect(instanceService.getInstanceInfo())
            .andReturn(instance)
            .times(1);
        Set<DuracloudInstanceService> instanceServices =
            new HashSet<DuracloudInstanceService>();
        instanceServices.add(instanceService);

        EasyMock.expect(
            instanceManagerService.getInstanceServices(EasyMock.anyInt()))
            .andReturn(instanceServices)
            .times(1);
    }

    private void initRestart(int accountId) throws Exception {
        EasyMock.expect(instanceManagerService.
            getInstanceService(accountId))
            .andReturn(instanceService)
            .anyTimes();

        instanceService.restart();
        EasyMock.expectLastCall()
            .times(1);
    }

    private void initStart(int accountId, String version) throws Exception {
        EasyMock.expect(instanceManagerService.
            createInstance(accountId, version))
            .andReturn(instanceService)
            .anyTimes();
    }

    private void initStop(int accountId) throws Exception {
        EasyMock.expect(instanceManagerService.
            getInstanceService(accountId))
            .andReturn(instanceService)
            .anyTimes();

        instanceService.stop();
        EasyMock.expectLastCall()
            .times(1);
    }
}
