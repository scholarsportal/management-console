/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.app.controller.AccountUsersController.AccountUser;
import org.duracloud.account.app.controller.AccountUsersController.PendingAccountUser;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class AccountUsersControllerTest extends AmaControllerTestBase {
    private AccountUsersController accountUsersController;
    private AccountService accountService;
    private DuracloudUserService userService;

    @Before
    public void before() throws Exception {
        accountUsersController = new AccountUsersController();
        accountManagerService = EasyMock.createMock("AccountManagerService",
                                                    AccountManagerService.class);
        accountService = EasyMock.createMock("AccountService",
                                             AccountService.class);
        userService = EasyMock.createMock("DuracloudUserService",
                                          DuracloudUserService.class);
    }

    @Test
    public void testSendInvitations() throws Exception {
        accountService = EasyMock.createMock(AccountService.class);

        
        EasyMock
            .expect(accountService.retrieveAccountInfo()).andReturn(createAccountInfo())
            .times(1);

        UserInvitation ui = createUserInvitation();
        
        EasyMock.expect(accountService.createUserInvitation(ui.getUserEmail())).andReturn(ui);
        
        EasyMock
        .expect(accountService.getPendingInvitations()).andReturn(new HashSet<UserInvitation>(Arrays.asList(new UserInvitation[]{ui})))
        .times(1);

        EasyMock.expect(accountService.getUsers()).andReturn(
            new HashSet<DuracloudUser>(Arrays
                .asList(new DuracloudUser[] { createUser() }))).times(1);

        EasyMock
            .expect(accountManagerService.getAccount(TEST_ACCOUNT_ID))
            .andReturn(accountService).times(1);

       replayMocks();
       
       BindingResult result = EasyMock.createMock(BindingResult.class);
       EasyMock.expect(result.hasErrors()).andReturn(false);
       EasyMock.replay(result);
       
       this.accountUsersController.setAccountManagerService(accountManagerService);
       Model model = new ExtendedModelMap();
       InvitationForm invitationForm = new InvitationForm();
       invitationForm.setEmailAddresses("test@duracloud.org");
       
       
       this.accountUsersController.sendInvitations(TEST_ACCOUNT_ID, invitationForm, result, model);
        Collection<AccountUser> accountUsers =
            (Collection<AccountUser>) model.asMap()
                .get(AccountUsersController.USERS_KEY);
       Assert.assertNotNull(accountUsers);

       boolean foundInvitation = false;
       
       for(AccountUser au : accountUsers){
           if(au instanceof AccountUsersController.PendingAccountUser){
               Assert.assertTrue(((PendingAccountUser)au).getInvitationId() == ui.getId());
               foundInvitation = true;
           }
       }
       
       Assert.assertTrue(foundInvitation);
       EasyMock.verify(this.accountManagerService, this.accountService, result);
    }

    /**
     * @return
     */
    private UserInvitation createUserInvitation() {
        return new UserInvitation(2,
            TEST_ACCOUNT_ID.intValue(),
            "test@duracloud.org",
            14,
            "xyz",
            0);
    }

    @Test
    public void testGet() throws Exception {
        setupGet();
        replayMocks();

        Model model = new ExtendedModelMap();
        accountUsersController.get(TEST_ACCOUNT_ID, model);
        verifyGet(model);

    }

    private void verifyGet(Model model) {
        Assert.assertTrue(model.containsAttribute(AccountController.ACCOUNT_INFO_KEY));
        Assert.assertTrue(model.containsAttribute(AccountUsersController.USERS_KEY));
    }

    private void setupGet()
        throws DBConcurrentUpdateException, AccountNotFoundException {
        this.accountManagerService =
            EasyMock.createMock(AccountManagerService.class);
        accountService = EasyMock.createMock(AccountService.class);
        EasyMock.expect(accountService.retrieveAccountInfo())
            .andReturn(createAccountInfo())
            .times(1);

        EasyMock.expect(accountService.getPendingInvitations())
            .andReturn(new HashSet<UserInvitation>())
            .times(1);

        EasyMock.expect(accountService.getUsers())
            .andReturn(new HashSet<DuracloudUser>(Arrays.asList(new DuracloudUser[] { createUser() })))
            .times(1);

        EasyMock.expect(accountManagerService.getAccount(TEST_ACCOUNT_ID))
            .andReturn(accountService);
        this.accountUsersController.setAccountManagerService(accountManagerService);
    }
    
    @Test
    public void testDeleteUserInvitation() throws Exception {
        this.accountManagerService =
            EasyMock.createMock(AccountManagerService.class);
        accountService = EasyMock.createMock(AccountService.class);

        EasyMock.expect(accountManagerService.getAccount(TEST_ACCOUNT_ID))
            .andReturn(accountService);
        this.accountUsersController.setAccountManagerService(accountManagerService);
        this.accountService.deleteUserInvitation(1);
        EasyMock.expectLastCall();
        replayMocks();
        
        Model model = new ExtendedModelMap();
        this.accountUsersController.deleteUserInvitation(TEST_ACCOUNT_ID, 1, model);
        EasyMock.verify(accountManagerService, accountService);
   }

    @Test
    public void testDeleteUser() throws Exception {
        DuracloudUserService userService = EasyMock.createMock(DuracloudUserService.class);
        userService.revokeUserRights(TEST_ACCOUNT_ID, 1);
        EasyMock.expectLastCall();
        
        this.accountUsersController.setUserService(userService);
        EasyMock.replay(userService);
        Model model = new ExtendedModelMap();
        this.accountUsersController.deleteUserFromAccount(TEST_ACCOUNT_ID, 1, model);
        EasyMock.verify(userService);
    }

    private void replayMocks() {
        EasyMock.replay(accountManagerService, accountService, userService);
    }

    @Test
    public void testEditUser() throws Exception {
        // set up mocks, and args
        int acctId = 7;
        int userId = 9;
        Role role = Role.ROLE_ADMIN;

        AccountUserEditForm acctUserEditForm = new AccountUserEditForm();
        acctUserEditForm.setRole(role.name());

        BindingResult bindingResult = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(bindingResult.hasErrors()).andReturn(false);
        EasyMock.replay(bindingResult);
        Model model = new ExtendedModelMap();

        EasyMock.expect(userService.setUserRights(acctId, userId, role))
            .andReturn(true);

        replayMocks();

        // method under test
        accountUsersController.setUserService(userService);
        accountUsersController.editUser(acctId,
                                        userId,
                                        acctUserEditForm,
                                        bindingResult,
                                        model);

        EasyMock.verify(userService);
    }

}
