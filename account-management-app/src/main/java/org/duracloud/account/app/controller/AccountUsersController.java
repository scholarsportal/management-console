/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.EmailAddressesParser;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
        return get(getAccountService(accountId), model);
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

        for (String emailAddress : emailAddresses) {
            UserInvitation ui = service.createUserInvitation(emailAddress);
            log.debug("created user invitation on account {} for {} expiring on {}",
                new Object[] {
                    ui.getAccountId(), ui.getUserEmail(),
                    ui.getExpirationDate() });
        }

        // FIXME pause for a moment to let the async calls to
        // the database percolate.
        // what happens if an async call fails?
        sleepMomentarily();
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
                        u.getRolesByAcct(accountId),
                        false);
                //TODO set current role for select box - based on hierarchy?
                //editForm.setRole();
                model.addAttribute("user", au);
                break;
            }
        }

        model.addAttribute(EDIT_ACCOUNT_USERS_FORM_KEY, editForm);

        return ACCOUNT_USERS_EDIT_ID;
    }

    @ModelAttribute("roleList")
    public List<Role> getRoleList() {
        List<Role> roles = Arrays.asList(Role.values());
        return roles;
    }

    @RequestMapping(value = USERS_EDIT_MAPPING, method = RequestMethod.POST)
    public String editUser(@PathVariable int accountId, @PathVariable int userId,
                           @ModelAttribute(EDIT_ACCOUNT_USERS_FORM_KEY) @Valid AccountUserEditForm accountUserEditForm,
					   BindingResult result,
					   Model model) throws Exception {
        log.debug("editUser account {}", accountId);

        Role role = Role.valueOf(accountUserEditForm.getRole());
        log.info("New role: {}", role);

        Set<Role> roles = role.getRoleHierarchy();
        userService.setUserRights(accountId,
                                  userId, 
                                  roles.toArray(new Role[roles.size()]));

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
        model.addAttribute("pendingUserInvitations", pendingUserInvitations);
        addAccountInfoToModel(accountInfo, model);
        List<AccountUser> accountUsers =
            buildUserList(accountInfo.getId(), users);
        appendInvitationsToAccountUserList(accountUsers, pendingUserInvitations);
        model.addAttribute(USERS_KEY, accountUsers);
    }

    /**
     * @param accountUsers
     * @param pendingUserInvitations
     */
    private void appendInvitationsToAccountUserList(
        List<AccountUser> accountUsers,
        Set<UserInvitation> pendingUserInvitations) {
        for (UserInvitation ui : pendingUserInvitations) {
            accountUsers.add(new PendingAccountUser(ui.getId(),
                ui.getUserEmail(),
                resolveStatus(ui),
                Arrays.asList(new Role[]{Role.ROLE_USER}),
                ui.getRedemptionCode()));
        }
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
            AccountUser au =
                new AccountUser(u.getId(),
                    u.getUsername(),
                    u.getFirstName(),
                    u.getLastName(),
                    u.getEmail(),
                    InvitationStatus.ACTIVE,
                    u.getRolesByAcct(accountId),
                    u.isOwnerForAcct(accountId) || hasMoreThanOneOwner);
            list.add(au);
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
            if (u.isOwnerForAcct(accountId)) {
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
    public class AccountUser {
        public AccountUser(
            int id, String username, String firstName, String lastName,
            String email, InvitationStatus status, Collection<Role> roles, boolean deletable) {
            super();
            this.id = id;
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.status = status;
            this.roles = roles;
            this.deletable = deletable;
        }

        private int id;
        private String username;
        private String firstName;
        private String lastName;
        private String email;
        private InvitationStatus status;
        private Collection<Role> roles;
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

        public Collection<Role> getRoles() {
            return roles;
        }

        public boolean isDeletable() {
            return this.deletable;
        }
    }

    public static enum InvitationStatus {
        ACTIVE, PENDING, EXPIRED
    }

    public class PendingAccountUser extends AccountUser {

        /**
         * @param invitationId
         * @param email
         * @param status
         * @param role
         */

        private int invitationId;
        private String redemptionCode;

        public PendingAccountUser(
            int invitationId, String email, InvitationStatus status, Collection<Role> roles,
            String redemptionCode) {
            super(-1, "------", "------", "------", email, status, roles, true);
            this.invitationId = invitationId;
            this.redemptionCode = redemptionCode;
        }

        public int getInvitationId() {
            return this.invitationId;
        }

        public String getRedemptionCode() {
            return this.redemptionCode;
        }

    }
}
