/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudAccount;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.common.domain.UserInvitation;
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

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
        model.addAttribute(EDIT_ACCOUNT_USERS_FORM_KEY, new AccountUserEditForm());

        model.addAttribute("invitationForm", new InvitationForm());
        return get(getAccountService(accountId), model);
    }

    @RequestMapping(value = ACCOUNT_USERS_MAPPING, method = RequestMethod.POST)
    public String sendInvitation(
        @PathVariable int accountId,
        @ModelAttribute("invitationForm") @Valid InvitationForm invitationForm,
        BindingResult result, Model model) throws Exception {
        log.info("sending invitation from account {}", accountId);
        if (result.hasErrors()) {
            model.addAttribute(EDIT_ACCOUNT_USERS_FORM_KEY, new AccountUserEditForm());
            addUserToModel(model);
            get(getAccountService(accountId), model);
            return ACCOUNT_USERS_VIEW_ID;
        }
        String emailAddress = invitationForm.getEmailAddresses();
        AccountService service = getAccountService(accountId);

            try {
                UserInvitation ui = service.inviteUser(emailAddress,
                                                       getUser().getUsername(),
                                                       notificationMgr.getEmailer());
                log.debug(
                "created user invitation on account {} for {} expiring on {}",
                new Object[]{ui.getAccountId(),
                             ui.getUserEmail(),
                             ui.getExpirationDate()});
            } catch(UnsentEmailException e) {
                result.addError(new ObjectError("emailAddresses",
                    "Unable to send an email to the following recipient, but the user has been added: " + emailAddress));
            }


        addUserToModel(model);
        model.addAttribute(EDIT_ACCOUNT_USERS_FORM_KEY, new AccountUserEditForm());
        model.addAttribute("invitationForm", new InvitationForm());
        return get(service, model);
    }

    protected String get(AccountService accountService, Model model)
        throws Exception {
        loadAccountUsers(accountService, model);
        return ACCOUNT_USERS_VIEW_ID;
    }

    @RequestMapping(value = USERS_INVITE_MAPPING, method = RequestMethod.GET)
    public String newInivation(@PathVariable int accountId, Model model)
        throws AccountNotFoundException {
        log.info("serving up new user invitation form");
        loadAccountInfo(accountId, model);
        model.addAttribute("invitationForm", new InvitationForm());
        return USERS_INVITE_VIEW_ID;
    }

    @RequestMapping(value = USERS_INVITE_MAPPING, method = RequestMethod.POST)
    public String sendInvitations(
        @PathVariable int accountId,
        @ModelAttribute("invitationForm") @Valid InvitationForm invitationForm,
        BindingResult result, Model model) throws Exception {
        log.info("sending invitations from account {}", accountId);
        if (result.hasErrors()) {
            return USERS_INVITE_VIEW_ID;
        }
        List<String> emailAddresses =
            EmailAddressesParser.parse(invitationForm.getEmailAddresses());
        AccountService service = getAccountService(accountId);

        List<String> failedEmailAddresses =  new ArrayList<String>();
        String adminUsername = getUser().getUsername();

        for (String emailAddress : emailAddresses) {
            try {
                UserInvitation ui = service.inviteUser(emailAddress,
                                                       adminUsername,
                                                       notificationMgr.getEmailer());
                log.debug(
                "created user invitation on account {} for {} expiring on {}",
                new Object[]{ui.getAccountId(),
                             ui.getUserEmail(),
                             ui.getExpirationDate()});
            } catch(UnsentEmailException e) {
                failedEmailAddresses.add(emailAddress);
            }
        }

        if(!failedEmailAddresses.isEmpty()) {
            result.addError(new ObjectError("emailAddresses",
                    "Unable to send an email to the following recipients, but the user has been added: " + failedEmailAddresses));
            return USERS_INVITE_VIEW_ID;
        }

        return get(service, model);
    }

    @RequestMapping(value = USERS_INVITATIONS_DELETE_MAPPING, method = RequestMethod.POST)
    public String deleteUserInvitation(
        @PathVariable int accountId, @PathVariable int invitationId, Model model)
        throws Exception {
        log.info("remove invitation {} from account {}",
            invitationId,
            accountId);
        AccountService service = getAccountService(accountId);
        service.deleteUserInvitation(invitationId);

        return formatAccountRedirect(String.valueOf(accountId), ACCOUNT_USERS_PATH);
    }

    @RequestMapping(value = USERS_DELETE_MAPPING, method = RequestMethod.POST)
    public String deleteUserFromAccount(
        @PathVariable int accountId, @PathVariable int userId, Model model)
        throws Exception {
        log.info("delete user {} from account {}", userId, accountId);
        userService.revokeUserRights(accountId, userId);
        DuracloudUser user = getUser();

        if(user.getId() == userId)
            return "redirect:/users/byid/" + user.getUsername();
        return formatAccountRedirect(String.valueOf(accountId), ACCOUNT_USERS_PATH);
    }

    @RequestMapping(value = USERS_EDIT_MAPPING, method = RequestMethod.GET)
    public String getEditUserForm(@PathVariable int accountId, @PathVariable int userId, Model model)
        throws Exception {
        log.info("getEditUserForm user {} account {}", userId, accountId);
        AccountUserEditForm editForm = new AccountUserEditForm();

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

        model.addAttribute(EDIT_ACCOUNT_USERS_FORM_KEY, editForm);

        return ACCOUNT_USERS_EDIT_ID;
    }

    @ModelAttribute("ownerRole")
    public Role getOwnerRole() {
        return Role.ROLE_OWNER;
    }

    @ModelAttribute("adminRole")
    public Role getAdminRole() {
        return Role.ROLE_ADMIN;
    }

    @ModelAttribute("userRole")
    public Role getUserRole() {
        return Role.ROLE_USER;
    }

    @RequestMapping(value = USERS_EDIT_MAPPING, method = RequestMethod.POST)
    public String editUser(@PathVariable int accountId, @PathVariable int userId,
                           @ModelAttribute(EDIT_ACCOUNT_USERS_FORM_KEY) @Valid AccountUserEditForm accountUserEditForm,
					   BindingResult result,
					   Model model) throws Exception {
        log.debug("editUser account {}", accountId);

        Role role = Role.valueOf(accountUserEditForm.getRole());
        log.info("New role: {}", role);

        boolean exception = false;
        Set<Role> roles = role.getRoleHierarchy();
        try {
            userService.setUserRights(accountId,
                                      userId,
                                      roles.toArray(new Role[roles.size()]));
        } catch(AccessDeniedException e) {
            result.addError(new ObjectError("role",
                                "You are unauthorized to set the role for this user"));
            exception = true;
        }



        addUserToModel(model);
        if(!exception)
            model.addAttribute(EDIT_ACCOUNT_USERS_FORM_KEY, new AccountUserEditForm());
        model.addAttribute("invitationForm", new InvitationForm());
        get(getAccountService(accountId), model);
        return formatAccountRedirect(String.valueOf(accountId), ACCOUNT_USERS_PATH);
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
     * @param ui
     * @return
     */
    private InvitationStatus resolveStatus(UserInvitation ui) {
        return ui.getExpirationDate().getTime() > System.currentTimeMillis()
            ? InvitationStatus.PENDING : InvitationStatus.EXPIRED;
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
     * @param users
     * @return
     */
    private boolean accountHasMoreThanOneOwner(
        Set<DuracloudUser> users, int accountId) {
        int ownerCount = 0;
        for (DuracloudUser u : users) {
            if (u.isOwnerForAcct(accountId) && !u.isRootForAcct(accountId)) {
                ownerCount++;
                if (ownerCount > 1) {
                    return true;
                }
            }
        }

        return false;
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
    }

    // This method is only used for test. The actual member is autowired.
    protected void setNotificationMgr(NotificationMgr notificationMgr) {
        this.notificationMgr = notificationMgr;
    }
}
