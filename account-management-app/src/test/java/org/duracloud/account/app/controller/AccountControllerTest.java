/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import java.util.HashSet;
import java.util.Set;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.DuracloudInstance;
import org.duracloud.account.db.model.InstanceType;
import org.duracloud.account.db.model.ServerImage;
import org.duracloud.account.db.util.DuracloudInstanceManagerService;
import org.duracloud.account.db.util.DuracloudInstanceService;
import org.duracloud.account.db.util.error.AccountNotFoundException;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
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
            createMock(DuracloudInstanceManagerService.class);
        this.instanceService =
            createMock(DuracloudInstanceService.class);
        
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
                                                model,
                                                this.redirectAttributes);
    }

    @Test
    public void testReInitInstance() throws Exception {
        boolean initUsers = false;
        createReInitInstanceMocks(initUsers);
        
        replayMocks();
        accountController.reInitialize(TEST_ACCOUNT_ID, TEST_INSTANCE_ID, model,this.redirectAttributes);
    }

    private void createReInitInstanceMocks(boolean initUsers) throws Exception {
        Set<DuracloudInstanceService> instanceServices = new HashSet<DuracloudInstanceService>();
        instanceServices.add(instanceService);

        EasyMock.expect(instanceManagerService.getInstanceServices(EasyMock.anyLong()))
            .andReturn(instanceServices);

        EasyMock.expect(instanceManagerService.getInstanceService(EasyMock.anyLong()))
            .andReturn(instanceService);

        if (initUsers) {
            instanceService.reInitializeUserRoles();
        } else {
            instanceService.reInitialize();
        }
        EasyMock.expectLastCall();

        EasyMock.expect(instanceService.getStatus()).andReturn("status");

        DuracloudInstance instance = createInstance(false);
        EasyMock.expect(instanceService.getInstanceInfo()).andReturn(instance);
    
        addFlashAttribute();

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
        EasyMock.expect(instanceService.getInstanceType())
                .andReturn(InstanceType.MEDIUM);
        initStart(TEST_ACCOUNT_ID, version);
        replayMocks();
        accountController.upgradeInstance(TEST_ACCOUNT_ID,
                                          TEST_INSTANCE_ID,
                                          model);
        verifyResult(model);
    }

    @Test
    public void testGetStatement()
        throws AccountNotFoundException {
        replayMocks();
        accountController.getStatement(TEST_ACCOUNT_ID, model);
        Assert.assertTrue(model.containsAttribute(AccountController.ACCOUNT_INFO_KEY));
    }

    private void initializeMockInstanceManagerService() throws Exception {
        initializeMockInstanceManagerService(1);
    }

    private void initializeMockInstanceManagerService(int times) throws Exception {
        Set<DuracloudInstanceService> instanceServices =
            new HashSet<DuracloudInstanceService>();
        instanceServices.add(instanceService);

        EasyMock.expect(
            instanceManagerService.getInstanceServices(EasyMock.anyLong()))
            .andReturn(instanceServices)
            .times(times);

        EasyMock.expect(instanceService.getStatus())
            .andReturn("status")
            .times(times);

        DuracloudInstance instance = createInstance(false);
        EasyMock.expect(instanceService.getInstanceInfo())
            .andReturn(instance)
            .times(times);
    }

    private void initializeMockInstanceAvailable() throws Exception {
        DuracloudInstance instance = createInstance(true);

        EasyMock.expect(instanceService.getInstanceInfo())
            .andReturn(instance)
            .times(1);
        Set<DuracloudInstanceService> instanceServices =
            new HashSet<DuracloudInstanceService>();
        instanceServices.add(instanceService);

        EasyMock.expect(
            instanceManagerService.getInstanceServices(EasyMock.anyLong()))
            .andReturn(instanceServices)
            .times(1);
    }

    private DuracloudInstance createInstance(boolean initialized) {
        DuracloudInstance instance = new DuracloudInstance();
        instance.setId(0L);
        ServerImage image = new ServerImage();
        image.setId(0L);
        instance.setImage(image);
        instance.setAccount(createAccountInfo(0L));
        instance.setHostName("host");
        instance.setProviderInstanceId("providerInstanceId");
        instance.setInitialized(initialized);
        return instance;
    }

    private void initRestart(Long accountId) throws Exception {
        EasyMock.expect(instanceManagerService.
            getInstanceService(accountId))
            .andReturn(instanceService)
            .anyTimes();

        instanceService.restart();
        EasyMock.expectLastCall()
            .times(1);
    }

    private void initStart(Long accountId, String version) throws Exception {
        EasyMock.expect(instanceManagerService.
            createInstance(accountId, version, InstanceType.MEDIUM))
            .andReturn(instanceService)
            .anyTimes();
    }

    private void initStop(Long accountId) throws Exception {
        EasyMock.expect(instanceManagerService.
            getInstanceService(accountId))
            .andReturn(instanceService)
            .anyTimes();

        instanceService.stop();
        EasyMock.expectLastCall()
            .times(1);
    }
}
