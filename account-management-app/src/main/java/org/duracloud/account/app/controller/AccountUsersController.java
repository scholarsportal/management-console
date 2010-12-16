/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.EmailAddressesParser;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 */
@Controller
@Lazy
public class AccountUsersController extends AbstractAccountController {
    public static final String ACCOUNT_USERS_VIEW_ID = "account-users";
    public static final String USERS_INVITE_VIEW_ID = "account-users-invite";

    public static final String ACCOUNT_USERS_PATH = "/users";
    public static final String ACCOUNT_USERS_MAPPING =
        ACCOUNT_PATH + ACCOUNT_USERS_PATH;
    public static final String USERS_INVITE_MAPPING =
        ACCOUNT_USERS_MAPPING + "/invite";

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
        loadAccountUsers(accountId, model);

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
        @ModelAttribute("invitationForm") @Valid InvitationForm invitationForm,
        @PathVariable int accountId, Model model) throws Exception {
        log.info("sending invitations from account {}", accountId);
        List<String> emailAddresses =
            EmailAddressesParser.parse(invitationForm.getEmailAddresses());
        AccountService service = getAccountService(accountId);

        for (String emailAddress : emailAddresses) {
            UserInvitation ui = service.createUserInvitation(emailAddress);
            log.debug(
                "created user invitation on account {} for {} expiring on {}",
                new Object[] {
                    ui.getAccountId(), ui.getUserEmail(),
                    ui.getExpirationDate() });
        }
        
        loadAccountUsers(accountId, model);
        return ACCOUNT_USERS_VIEW_ID;
    }

    /**
     * @param accountId
     * @return
     */
    private AccountService getAccountService(int accountId)
        throws AccountNotFoundException {
        return this.getAccountService(accountId);
    }

    /**
     * @param accountId
     * @param model
     */
    private void loadAccountUsers(int accountId, Model model)
        throws Exception {
        AccountService accountService =
            accountManagerService.getAccount(accountId);
        Set<DuracloudUser> users = accountService.getUsers();
        Set<UserInvitation> pendingUserInvitations = accountService.getPendingInvitations();
        model.addAttribute("pendingUserInvitations", pendingUserInvitations);    
        addAccountInfoToModel(accountService.retrieveAccountInfo(), model);
        model.addAttribute(USERS_KEY, buildUserList(accountId, users));
    }

    /**
     * @param accountService
     * @return
     */
    private List<AccountUser> buildUserList(
        int accountId, Set<DuracloudUser> users) {
        List<AccountUser> list = new LinkedList<AccountUser>();
        for (DuracloudUser u : users) {
            AccountUser au =
                new AccountUser(u.getId(), u.getUsername(), u.getFirstName(), u
                    .getLastName(), u.getEmail(), "Active", getBroadestRole(u
                    .getRolesByAcct(accountId)));
            list.add(au);
        }

        return list;
    }

    private static class LowestToHighestComparator implements Comparator<Role> {

        @Override
        public int compare(Role o1, Role o2) {
            return o1.ordinal() > o2.ordinal() ? 1 : (o1.ordinal() < o2
                .ordinal() ? -1 : 0);
        }
    }

    private static LowestToHighestComparator LOWEST_TO_HIGHEST_COMPARATOR =
        new LowestToHighestComparator();

    /**
     * @param roles
     * @return
     */
    private Role getBroadestRole(Set<Role> roles) {
        List<Role> roleList = new ArrayList<Role>(roles);
        Collections.sort(roleList, LOWEST_TO_HIGHEST_COMPARATOR);
        return roleList.get(0);
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
            String email, String status, Role role) {
            super();
            this.id = id;
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.status = status;
            this.role = role;
        }

        private int id;
        private String username;
        private String firstName;
        private String lastName;
        private String email;
        private String status;
        private Role role;

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

        public String getStatus() {
            return status;
        }

        public Role getRole() {
            return role;
        }
    }
}
