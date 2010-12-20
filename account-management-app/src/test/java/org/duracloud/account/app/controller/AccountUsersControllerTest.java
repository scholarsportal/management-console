/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.duracloud.account.app.controller.AccountUsersController.AccountUser;
import org.duracloud.account.app.controller.AccountUsersController.PendingAccountUser;
import org.duracloud.account.common.domain.DuracloudUser;
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

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class AccountUsersControllerTest extends AmaControllerTestBase {
    private AccountUsersController accountUsersController;
    private AccountService as;

    @Before
    public void before() throws Exception {
        accountUsersController = new AccountUsersController();
    }

    @Test
    public void testSendInvitations() throws Exception {
        this.accountManagerService =
            EasyMock.createMock(AccountManagerService.class);
        as = EasyMock.createMock(AccountService.class);

        
        EasyMock
            .expect(as.retrieveAccountInfo()).andReturn(createAccountInfo())
            .times(1);

        UserInvitation ui = createUserInvitation();
        
        EasyMock.expect(as.createUserInvitation(ui.getUserEmail())).andReturn(ui);
        
        EasyMock
        .expect(as.getPendingInvitations()).andReturn(new HashSet<UserInvitation>(Arrays.asList(new UserInvitation[]{ui})))
        .times(1);

        EasyMock.expect(as.getUsers()).andReturn(
            new HashSet<DuracloudUser>(Arrays
                .asList(new DuracloudUser[] { createUser() }))).times(1);

        EasyMock
            .expect(accountManagerService.getAccount(TEST_ACCOUNT_ID))
            .andReturn(as).times(1);

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
       EasyMock.verify(this.accountManagerService, this.as, result);
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
        as = EasyMock.createMock(AccountService.class);
        EasyMock.expect(as.retrieveAccountInfo())
            .andReturn(createAccountInfo())
            .times(1);

        EasyMock.expect(as.getPendingInvitations())
            .andReturn(new HashSet<UserInvitation>())
            .times(1);

        EasyMock.expect(as.getUsers())
            .andReturn(new HashSet<DuracloudUser>(Arrays.asList(new DuracloudUser[] { createUser() })))
            .times(1);

        EasyMock.expect(accountManagerService.getAccount(TEST_ACCOUNT_ID))
            .andReturn(as);
        this.accountUsersController.setAccountManagerService(accountManagerService);
    }
    
    @Test
    public void testDeleteUserInvitation() throws Exception {
        this.accountManagerService =
            EasyMock.createMock(AccountManagerService.class);
        as = EasyMock.createMock(AccountService.class);

        EasyMock.expect(accountManagerService.getAccount(TEST_ACCOUNT_ID))
            .andReturn(as);
        this.accountUsersController.setAccountManagerService(accountManagerService);
        this.as.deleteUserInvitation(1);
        EasyMock.expectLastCall();
        replayMocks();
        
        Model model = new ExtendedModelMap();
        this.accountUsersController.deleteUserInvitation(TEST_ACCOUNT_ID, 1, model);
        EasyMock.verify(accountManagerService, as);
   }

    @Test
    public void testDeleteUser() throws Exception {
        DuracloudUserService userService = EasyMock.createMock(DuracloudUserService.class);
        userService.revokeOwnerRights(TEST_ACCOUNT_ID, 1);
        EasyMock.expectLastCall();
        userService.revokeAdminRights(TEST_ACCOUNT_ID, 1);
        EasyMock.expectLastCall();
        userService.revokeUserRights(TEST_ACCOUNT_ID, 1);
        EasyMock.expectLastCall();
        
        this.accountUsersController.setUserService(userService);
        EasyMock.replay(userService);
        Model model = new ExtendedModelMap();
        this.accountUsersController.deleteUserFromAccount(TEST_ACCOUNT_ID, 1, model);
        EasyMock.verify(userService);
    }

    private void replayMocks() {
        EasyMock.replay(accountManagerService, as);
    }

}
