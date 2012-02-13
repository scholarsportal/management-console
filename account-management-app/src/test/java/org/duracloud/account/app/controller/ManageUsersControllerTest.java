/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.common.domain.ServerImage;
import org.duracloud.account.common.domain.ServiceRepository;
import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.util.RootAccountManagerService;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;

public class ManageUsersControllerTest extends AmaControllerTestBase {
    private ManageUsersController manageUsersController;
    private RootAccountManagerService rootAccountManagerService;

    @Before
    public void before() throws Exception {
        super.before();

        manageUsersController = new ManageUsersController();
        rootAccountManagerService = EasyMock.createMock("RootAccountManagerService",
                                                        RootAccountManagerService.class);
        mocks.add(rootAccountManagerService);
        manageUsersController.setRootAccountManagerService(rootAccountManagerService);
        manageUsersController.setUserService(userService);
        manageUsersController.setAccountManagerService(accountManagerService);        
        
        setupGenericAccountAndUserServiceMocks(TEST_ACCOUNT_ID);
    }

    @Test
    public void testGetUsers() throws Exception {
        Set<DuracloudUser> users = new HashSet<DuracloudUser>();
        users.add(createUser());
        EasyMock.expect(rootAccountManagerService.listAllUsers(null))
            .andReturn(users);
        EasyMock.expect(rootAccountManagerService.listAllAccounts(null))
            .andReturn(new HashSet<AccountInfo>());
        EasyMock.expect(rootAccountManagerService.listAllServerImages(null))
            .andReturn(new HashSet<ServerImage>());
        EasyMock.expect(rootAccountManagerService.listAllServiceRepositories(null))
            .andReturn(new HashSet<ServiceRepository>());

        replayMocks();
        this.manageUsersController.getUsers(model);
    }

    @Test
    public void testDeleteUserFromAccount() throws Exception {
        this.userService.revokeUserRights(TEST_ACCOUNT_ID, 1);
        EasyMock.expectLastCall();
        replayMocks();
        this.manageUsersController.deleteUserFromAccount(TEST_ACCOUNT_ID,
                                                         1);
    }

    @Test
    public void testDeleteUser() throws Exception {
        rootAccountManagerService.deleteUser(1);
        EasyMock.expectLastCall();

        this.manageUsersController.setRootAccountManagerService(
            rootAccountManagerService);
        replayMocks();
        this.manageUsersController.deleteUser(1);
    }

    @Test
    public void testDeleteAccount() throws Exception {
        rootAccountManagerService.deleteAccount(1);
        EasyMock.expectLastCall();
        replayMocks();
        this.manageUsersController.deleteAccount(1);
    }

    @Test
    public void testGetSetupAccount() throws Exception {
        ExtendedModelMap model1 = EasyMock.createMock("ExtendedModelMap",
                                          ExtendedModelMap.class);
        mocks.add(model1);
        EasyMock.expect(model1.addAttribute(EasyMock.isA(String.class),
                           EasyMock.isA(AccountSetupForm.class)))
            .andReturn(null);

        EasyMock.expect(model1.addAttribute(EasyMock.isA(String.class),
                           EasyMock.isA(ArrayList.class)))
            .andReturn(null);

        EasyMock.expect(rootAccountManagerService.getSecondaryStorageProviders(1))
            .andReturn(new ArrayList<StorageProviderAccount>());
        replayMocks();
        
        this.manageUsersController.getSetupAccount(1, model1);
    }

    @Test
    public void testSetupAccount() throws Exception {

        rootAccountManagerService.setupStorageProvider(EasyMock.anyInt(),
                                                       EasyMock.isA(String.class),
                                                       EasyMock.isA(String.class));
        EasyMock.expectLastCall().anyTimes();

        rootAccountManagerService.setupComputeProvider(EasyMock.anyInt(),
                                                       EasyMock.isA(String.class),
                                                       EasyMock.isA(String.class),
                                                       EasyMock.isA(String.class),
                                                       EasyMock.isA(String.class),
                                                       EasyMock.isA(String.class));
        EasyMock.expectLastCall();

        rootAccountManagerService.activateAccount(EasyMock.anyInt());
        EasyMock.expectLastCall();



        AccountSetupForm setupForm = new AccountSetupForm();
        String test = "test";
        setupForm.setPrimaryStorageUsername(test);
        setupForm.setPrimaryStoragePassword(test);
        setupForm.setComputeCredentialsSame(true);
        setupForm.setComputeElasticIP(test);
        setupForm.setComputeKeypair(test);
        setupForm.setComputeSecurityGroup(test);
        
        setupNoBindingResultErrors();
        replayMocks();
        
        
        this.manageUsersController.setupAccount(TEST_ACCOUNT_ID,
                                                setupForm,
                                                result,
                                                model);
    }

    @Test
    public void testResetUsersPassword() throws Exception {
        rootAccountManagerService.resetUsersPassword(1);
        EasyMock.expectLastCall();
        replayMocks();
        this.manageUsersController.resetUsersPassword(1, model);
    }

    @Test
    public void testAddUser() throws Exception {
        // set up mocks, and args
        int acctId = 7;
        int userId = 9;

        AccountUserAddForm acctUserAddForm = new AccountUserAddForm();
        acctUserAddForm.setAccountId(acctId);
        acctUserAddForm.setUserId(userId);
        acctUserAddForm.setRole(Role.ROLE_ADMIN.name());

        setupNoBindingResultErrors();
        EasyMock.expect(userService.setUserRights(EasyMock.eq(acctId),
                                                  EasyMock.eq(userId),
                                                  EasyMock.isA(Role.class),
                                                  EasyMock.isA(Role.class),
                                                  EasyMock.isA(Role.class)))
            .andReturn(true);

        replayMocks();

        // method under test
        manageUsersController.addUser(acctUserAddForm, result, model);
    }

    @Test
    public void testEditUser() throws Exception {
        // set up mocks, and args
        int acctId = 7;
        int userId = 9;

        AccountUserEditForm acctUserEditForm = new AccountUserEditForm();
        acctUserEditForm.setRole(Role.ROLE_ADMIN.name());
        
        setupNoBindingResultErrors();
        
        EasyMock.expect(userService.setUserRights(EasyMock.eq(acctId),
                                                  EasyMock.eq(userId),
                                                  EasyMock.isA(Role.class),
                                                  EasyMock.isA(Role.class),
                                                  EasyMock.isA(Role.class)))
            .andReturn(true);

        replayMocks();
        
        // method under test
        manageUsersController.editUser(acctId,
                                        userId,
                                        acctUserEditForm,
                                        result,
                                        model);
    }

    @Test
    public void testActivate() throws Exception {
        accountService.storeAccountStatus(AccountInfo.AccountStatus.ACTIVE);
        EasyMock.expectLastCall();
        replayMocks();
        manageUsersController.activate(TEST_ACCOUNT_ID, model);
    }

    @Test
    public void testDeactivate() throws Exception {

        accountService.storeAccountStatus(AccountInfo.AccountStatus.INACTIVE);
        EasyMock.expectLastCall();
        replayMocks();
        manageUsersController.deactivate(TEST_ACCOUNT_ID, model);

    }

}
