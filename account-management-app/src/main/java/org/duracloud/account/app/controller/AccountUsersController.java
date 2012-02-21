/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.validation.Valid;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudAccount;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.EmailAddressesParser;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.error.UnsentEmailException;
import org.duracloud.account.util.notification.NotificationMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 */
@Controller
@Lazy
public class AccountUsersController extends AbstractAccountController {
    public static final String ACCOUNT_USERS_VIEW_ID = "account-users";
    public static final String USERS_INVITE_VIEW_ID = "account-users-invite";
    public static final String ACCOUNT_USERS_EDIT_ID = "account-users-edit";

    public static final String EDIT_ACCOUNT_USERS_FORM_KEY = "accountUsersEditForm";
    private static final String USERNAME_FORM_KEY = "usernameForm";
    private static final String INVITATION_FORM_KEY = "invitationForm";

    public static final String ACCOUNT_USERS_PATH = "/users";
    public static final String ACCOUNT_USERS_MAPPING =
        ACCOUNT_PATH + ACCOUNT_USERS_PATH;
    public static final String USERS_INVITE_MAPPING =
        ACCOUNT_USERS_MAPPING + "/invite";
    public static final String USERS_INVITATIONS_DELETE_MAPPING =
        ACCOUNT_USERS_MAPPING + "/invitations/byid/{invitationId}/delete";
    public static final String USERS_DELETE_MAPPING =
        ACCOUNT_USERS_MAPPING + "/byid/{userId}/delete";
    public static final String USERS_EDIT_MAPPING =
        ACCOUNT_USERS_MAPPING + "/byid/{userId}/edit";

    public static final String USERS_KEY = "users";

    @Autowired
    private NotificationMgr notificationMgr;

    
    @ModelAttribute(INVITATION_FORM_KEY)
    public InvitationForm invitationForm(){
        return new InvitationForm();
    }

    @ModelAttribute(USERNAME_FORM_KEY)
    public UsernameForm usernameForm(){
        return new UsernameForm();
    }
    
    @ModelAttribute(EDIT_ACCOUNT_USERS_FORM_KEY)
    public AccountUserEditForm accountUserEditForm(){
        return new AccountUserEditForm();
    }

    /**
     * 
     * @param accountId
     * @param model
     * @return
     * @throws AccountNotFoundException
     */
    @RequestMapping(value = ACCOUNT_USERS_MAPPING, method = RequestMethod.GET)
    public String get(@PathVariable int accountId, Model model)
        throws Exception {
        addUserToModel(model);
        return get(getAccountService(accountId), model);
    }

    @RequestMapping(value = ACCOUNT_USERS_MAPPING+"/adduser", method = RequestMethod.POST)
    public ModelAndView
        addUser(@PathVariable int accountId,
                @ModelAttribute(USERNAME_FORM_KEY) @Valid UsernameForm usernameForm,
                BindingResult result,
                Model model,
                RedirectAttributes redirectAttributes) throws Exception {
        String username = usernameForm.getUsername();
        log.debug("entering addUser: adding {} to account {}",
                  username,
                  accountId);
        if (result.hasErrors()) {
            addUserToModel(model);
            get(getAccountService(accountId), model);
            return new ModelAndView(ACCOUNT_USERS_VIEW_ID, model.asMap());
        }

        
        DuracloudUser user =  userService.loadDuracloudUserByUsernameInternal(username);
        if(userService.addUserToAccount(accountId, user.getId())){
            log.info("added user {} to account {}",
                     new Object[] { username, accountId });

            String message =  MessageFormat.format("Successfully added {0} to account.",
                                                  username);
            setSuccessFeedback(message, redirectAttributes);

        }

        
        return createAccountRedirectModelAndView(accountId,
                                                 ACCOUNT_USERS_PATH);
    }

    protected String get(AccountService accountService, Model model)
        throws Exception {
        loadAccountUsers(accountService, model);
        return ACCOUNT_USERS_VIEW_ID;
    }

    @RequestMapping(value = ACCOUNT_USERS_MAPPING, method = RequestMethod.POST)
    public ModelAndView sendInvitations(
        @PathVariable int accountId,
        @ModelAttribute(INVITATION_FORM_KEY) @Valid InvitationForm invitationForm,
        BindingResult result, Model model,
        RedirectAttributes redirectAttributes) throws Exception {
        log.info("sending invitations from account {}", accountId);
        boolean hasErrors = result.hasErrors();
        AccountService service = getAccountService(accountId);

        if (!hasErrors) {

            List<String> emailAddresses =
                EmailAddressesParser.parse(invitationForm.getEmailAddresses());

            List<String> failedEmailAddresses = new ArrayList<String>();
            String adminUsername = getUser().getUsername();

            for (String emailAddress : emailAddresses) {
                try {
                    UserInvitation ui =
                        service.inviteUser(emailAddress,
                                           adminUsername,
                                           notificationMgr.getEmailer());
                    String template =
                        "Successfully created user invitation on "
                            + "account {0} for {1} expiring on {2}";
                    String message =
                        MessageFormat.format(template,
                                             ui.getAccountId(),
                                             ui.getUserEmail(),
                                             ui.getExpirationDate());
                    log.info(message);
                } catch (UnsentEmailException e) {
                    failedEmailAddresses.add(emailAddress);
                }
            }

            if (!failedEmailAddresses.isEmpty()) {
                String template =
                    "Unable to send an email to the following recipients, "
                        + "but the user has been added: {0}";
                String message =
                    MessageFormat.format(template, failedEmailAddresses);

                result.addError(new ObjectError("emailAddresses", message));
                hasErrors = true;
            }
        }

        if (hasErrors) {
            addUserToModel(model);
            get(service, model);
            return new ModelAndView(ACCOUNT_USERS_VIEW_ID);
        }
        
        setSuccessFeedback("Successfully sent invitations.", redirectAttributes);
        return createAccountRedirectModelAndView(accountId, ACCOUNT_USERS_PATH);
    }

    @RequestMapping(value = USERS_INVITATIONS_DELETE_MAPPING, method = RequestMethod.POST)
    public ModelAndView deleteUserInvitation(
        @PathVariable int accountId, @PathVariable int invitationId, Model model)
        throws Exception {
        log.info("remove invitation {} from account {}",
            invitationId,
            accountId);
        AccountService service = getAccountService(accountId);
        service.deleteUserInvitation(invitationId);

        return createAccountRedirectModelAndView(accountId, ACCOUNT_USERS_PATH);
    }

    @RequestMapping(value = USERS_DELETE_MAPPING, method = RequestMethod.POST)
    public ModelAndView deleteUserFromAccount(
        @PathVariable int accountId, @PathVariable int userId, Model model)
        throws Exception {
        log.info("delete user {} from account {}", userId, accountId);
        userService.revokeUserRights(accountId, userId);
        DuracloudUser user = getUser();

        if(user.getId() == userId){
            View redirect = UserController.formatUserRedirect(user.getUsername());
            return new ModelAndView(redirect);
        }
        return createAccountRedirectModelAndView(accountId, ACCOUNT_USERS_PATH);
    }

    @RequestMapping(value = USERS_EDIT_MAPPING, method = RequestMethod.GET)
    public String getEditUserForm(@PathVariable int accountId, @PathVariable int userId, Model model)
        throws Exception {
        log.info("getEditUserForm user {} account {}", userId, accountId);
        AccountService accountService = getAccountService(accountId);
        Set<DuracloudUser> users = accountService.getUsers();

        for (DuracloudUser u : users) {
            if(u.getId() == userId) {
                AccountUser au =
                    new AccountUser(u.getId(),
                        u.getUsername(),
                        u.getFirstName(),
                        u.getLastName(),
                        u.getEmail(),
                        InvitationStatus.ACTIVE,
                        u.getRoleByAcct(accountId),
                        false);
                //TODO set current role for select box - based on hierarchy?
                //editForm.setRole();
                model.addAttribute("user", au);
                break;
            }
        }

        loadAccountInfo(accountId, model);        
        return ACCOUNT_USERS_EDIT_ID;
    }

    @RequestMapping(value = USERS_EDIT_MAPPING, method = RequestMethod.POST)
    public ModelAndView
        editUser(@PathVariable int accountId,
                 @PathVariable int userId,
                 @ModelAttribute(EDIT_ACCOUNT_USERS_FORM_KEY) @Valid AccountUserEditForm accountUserEditForm,
                 BindingResult result,
                 Model model,
                 RedirectAttributes redirectAttributes) throws Exception {
        log.debug("editUser account {}", accountId);

        boolean hasErrors = result.hasErrors();
        if (!hasErrors) {
            Role role = Role.valueOf(accountUserEditForm.getRole());
            log.info("New role: {}", role);
            try {
                setUserRights(userService, accountId, userId, role);
                setSuccessFeedback("Successfully updated user.", redirectAttributes);                
            } catch (AccessDeniedException e) {
                result.addError(new ObjectError("role",
                                                "You are unauthorized to set the role for this user"));
                hasErrors = true;
            }
        }

        if (hasErrors) {
            addUserToModel(model);
            return new ModelAndView(ACCOUNT_USERS_VIEW_ID, model.asMap());
        }

        return createAccountRedirectModelAndView(accountId,
                                                 ACCOUNT_USERS_PATH);
    }

    /**
     * @param accountId
     * @return
     */
    private AccountService getAccountService(int accountId)
        throws AccountNotFoundException {
        return this.accountManagerService.getAccount(accountId);
    }

    /**
     * @param accountService
     * @param model
     */
    private void loadAccountUsers(AccountService accountService, Model model)
        throws Exception {
        AccountInfo accountInfo = accountService.retrieveAccountInfo();
        Set<DuracloudUser> users = accountService.getUsers();
        Set<UserInvitation> pendingUserInvitations =
            accountService.getPendingInvitations();

        DuracloudAccount duracloudAccount = new DuracloudAccount();
        duracloudAccount.setAccountInfo(accountInfo);
        duracloudAccount.setUserRole(getUser().getRoleByAcct(accountInfo.getId()));
        model.addAttribute("account", duracloudAccount);

        List<AccountUser> accountUsers =
            buildUserList(accountInfo.getId(), users);
        Collections.sort(accountUsers);
        addInvitationsToModel(pendingUserInvitations, accountService, model);
        model.addAttribute(USERS_KEY, accountUsers);
    }

    /**
     * @param model
     * @param pendingUserInvitations
     * @param accountService
     */
    private void addInvitationsToModel(Set<UserInvitation> pendingUserInvitations,
                                       AccountService accountService,
                                       Model model)
        throws Exception {
        Set<PendingAccountUser> pendingUsers = new TreeSet<PendingAccountUser>();
        for (UserInvitation ui : pendingUserInvitations) {
            pendingUsers.add(new PendingAccountUser(ui, Role.ROLE_USER));
        }
        model.addAttribute("pendingUserInvitations", pendingUsers);
    }

    /**
     * @param accountId
     * @return
     */
    private List<AccountUser> buildUserList(
        int accountId, Set<DuracloudUser> users) {
        List<AccountUser> list = new LinkedList<AccountUser>();
        boolean hasMoreThanOneOwner =
            accountHasMoreThanOneOwner(users, accountId);
        for (DuracloudUser u : users) {
            Role role = u.getRoleByAcct(accountId);
            if(!role.equals(Role.ROLE_ROOT)) {
                AccountUser au =
                    new AccountUser(u.getId(),
                        u.getUsername(),
                        u.getFirstName(),
                        u.getLastName(),
                        u.getEmail(),
                        InvitationStatus.ACTIVE,
                        role,
                        (!u.isOwnerForAcct(accountId) ||
                             (u.isOwnerForAcct(accountId) && hasMoreThanOneOwner)));
                list.add(au);
            }
        }

        return list;
    }

    /**
     * This class is a read only representation of a user from the perspective
     * of an account admin/owner.
     * 
     * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
     * 
     */
    public class AccountUser implements Comparable<AccountUser> {
        public AccountUser(
            int id, String username, String firstName, String lastName,
            String email, InvitationStatus status, Role role, boolean deletable) {
            super();
            this.id = id;
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.status = status;
            this.role = role;
            this.deletable = deletable;
        }

        private int id;
        private String username;
        private String firstName;
        private String lastName;
        private String email;
        private InvitationStatus status;
        private Role role;
        private boolean deletable;

        public int getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public InvitationStatus getStatus() {
            return status;
        }

        public Role getRole() {
            return role;
        }

        public boolean isDeletable() {
            return this.deletable;
        }

        @Override
        public int compareTo(AccountUser o) {
            return this.getUsername().compareTo(o.getUsername());
        }
    }

    public static enum InvitationStatus {
        ACTIVE, PENDING, EXPIRED
    }

    public class PendingAccountUser implements Comparable<PendingAccountUser> {

        private UserInvitation invitation;
        private Role role;

        public PendingAccountUser(UserInvitation ui, Role role) {
            this.invitation = ui;
            this.role = role;
        }

        public int getInvitationId() {
            return this.invitation.getId();
        }

        public String getRedemptionCode() {
            return this.invitation.getRedemptionCode();
        }

        public String getRedemptionURL() {
            return this.invitation.getRedemptionURL();
        }

        public String getEmail() {
            return this.invitation.getUserEmail();
        }

        public String getExpirationDate() {
            return this.invitation.getExpirationDate().toString();
        }

        @Override
        public int compareTo(PendingAccountUser o) {
            return this.getEmail().compareTo(o.getEmail());
        }

        public Role getRole() {
            return role;
        }
    }

    // This method is only used for test. The actual member is autowired.
    protected void setNotificationMgr(NotificationMgr notificationMgr) {
        this.notificationMgr = notificationMgr;
    }
}
