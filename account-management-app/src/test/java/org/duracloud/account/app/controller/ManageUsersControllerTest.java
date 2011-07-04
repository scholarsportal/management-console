/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.RootAccountManagerService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.notification.NotificationMgr;
import org.duracloud.notification.Emailer;
import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ManageUsersControllerTest extends AmaControllerTestBase {
    private ManageUsersController manageUsersController;
    private RootAccountManagerService rootAccountManagerService;
    private AccountService accountService;
    private DuracloudUserService userService;
    private NotificationMgr notificationMgr;

    @Before
    public void before() throws Exception {
        super.before();

        manageUsersController = new ManageUsersController();
        rootAccountManagerService = EasyMock.createMock("RootAccountManagerService",
                                                        RootAccountManagerService.class);
        accountManagerService = EasyMock.createMock("AccountManagerService",
                                                    AccountManagerService.class);
        accountService = EasyMock.createMock("AccountService",
                                             AccountService.class);
        userService = EasyMock.createMock("DuracloudUserService",
                                          DuracloudUserService.class);
        notificationMgr = EasyMock.createMock("NotificationMgr",
                                              NotificationMgr.class);
    }

    @Test
    public void testGetUsers() throws Exception {
        Set<DuracloudUser> users = new HashSet<DuracloudUser>();
        users.add(createUser());

        EasyMock.expect(rootAccountManagerService.listAllUsers(null))
            .andReturn(users);
        EasyMock.expect(rootAccountManagerService.listAllAccounts(null))
            .andReturn(null);

        EasyMock.expect(accountService.retrieveAccountInfo())
            .andReturn(createAccountInfo())
            .times(1);

        EasyMock.expect(accountService.getUsers())
            .andReturn(new HashSet<DuracloudUser>(Arrays.asList(new DuracloudUser[]{
                createUser()})))
            .times(1);

        EasyMock.expect(accountManagerService.getAccount(EasyMock.anyInt()))
            .andReturn(accountService);

        EasyMock.expect(userService.loadDuracloudUserByUsername(EasyMock.<String>anyObject()))
            .andReturn(createUser())
            .anyTimes();

        this.manageUsersController.setUserService(userService);

        this.manageUsersController.setAccountManagerService(
            accountManagerService);
        this.manageUsersController.setRootAccountManagerService(rootAccountManagerService);
        EasyMock.replay(rootAccountManagerService, accountService, accountManagerService);

        Model model = new ExtendedModelMap();
        this.manageUsersController.getUsers(model);
        EasyMock.verify(rootAccountManagerService, accountService, accountManagerService);
    }

    @Test
    public void testDeleteUserFromAccount() throws Exception {
        DuracloudUserService userService = EasyMock.createMock(DuracloudUserService.class);
        userService.revokeUserRights(TEST_ACCOUNT_ID, 1);
        EasyMock.expectLastCall();

        EasyMock.expect(userService.loadDuracloudUserByUsername(TEST_USERNAME))
            .andReturn(createUser())
            .anyTimes();

        this.manageUsersController.setUserService(userService);
        EasyMock.replay(userService);
        Model model = new ExtendedModelMap();
        this.manageUsersController.deleteUserFromAccount(TEST_ACCOUNT_ID,
                                                          1,
                                                          model);
        EasyMock.verify(userService);
    }

    @Test
    public void testDeleteUser() throws Exception {
        rootAccountManagerService.deleteUser(1);
        EasyMock.expectLastCall();

        this.manageUsersController.setRootAccountManagerService(
            rootAccountManagerService);
        EasyMock.replay(rootAccountManagerService);

        Model model = new ExtendedModelMap();
        this.manageUsersController.deleteUser(1, model);
        EasyMock.verify(rootAccountManagerService);
    }

    @Test
    public void testResetUsersPassword() throws Exception {
        rootAccountManagerService.resetUsersPassword(1);
        EasyMock.expectLastCall();

        this.manageUsersController.setRootAccountManagerService(
            rootAccountManagerService);
        EasyMock.replay(rootAccountManagerService);

        Model model = new ExtendedModelMap();
        this.manageUsersController.resetUsersPassword(1, model);
        EasyMock.verify(rootAccountManagerService);
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

        BindingResult bindingResult = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(bindingResult.hasErrors()).andReturn(false);
        EasyMock.replay(bindingResult);
        Model model = new ExtendedModelMap();

        setupSetRights(acctId);

        EasyMock.expect(userService.setUserRights(EasyMock.eq(acctId),
                                                  EasyMock.eq(userId),
                                                  EasyMock.isA(Role.class),
                                                  EasyMock.isA(Role.class),
                                                  EasyMock.isA(Role.class)))
            .andReturn(true);

        EasyMock.replay(accountManagerService, accountService, userService);

        // method under test
        manageUsersController.setUserService(userService);
        manageUsersController.addUser(acctUserAddForm, bindingResult, model);

        EasyMock.verify(userService);
    }

    @Test
    public void testEditUser() throws Exception {
        // set up mocks, and args
        int acctId = 7;
        int userId = 9;

        AccountUserEditForm acctUserEditForm = new AccountUserEditForm();
        acctUserEditForm.setRole(Role.ROLE_ADMIN.name());

        BindingResult bindingResult = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(bindingResult.hasErrors()).andReturn(false);
        EasyMock.replay(bindingResult);
        Model model = new ExtendedModelMap();

        setupSetRights(acctId);

        EasyMock.expect(userService.setUserRights(EasyMock.eq(acctId),
                                                  EasyMock.eq(userId),
                                                  EasyMock.isA(Role.class),
                                                  EasyMock.isA(Role.class),
                                                  EasyMock.isA(Role.class)))
            .andReturn(true);

        EasyMock.replay(accountManagerService, accountService, userService);

        // method under test
        manageUsersController.setUserService(userService);
        manageUsersController.editUser(acctId,
                                        userId,
                                        acctUserEditForm,
                                        bindingResult,
                                        model);

        EasyMock.verify(userService);
    }

    private void setupSetRights(int accountId)
        throws DBConcurrentUpdateException, AccountNotFoundException, DBNotFoundException {
        EasyMock.expect(accountService.retrieveAccountInfo())
            .andReturn(createAccountInfo())
            .times(1);

        EasyMock.expect(accountService.getPendingInvitations())
            .andReturn(new HashSet<UserInvitation>())
            .times(1);

        EasyMock.expect(accountService.getUsers())
            .andReturn(new HashSet<DuracloudUser>(Arrays.asList(new DuracloudUser[] { createUser() })))
            .times(1);

        EasyMock.expect(accountManagerService.getAccount(accountId))
            .andReturn(accountService);
        this.manageUsersController.setAccountManagerService(
            accountManagerService);

        EasyMock.expect(userService.loadDuracloudUserByUsername(TEST_USERNAME))
            .andReturn(createUser())
            .anyTimes();
        manageUsersController.setUserService(userService);
    }

}