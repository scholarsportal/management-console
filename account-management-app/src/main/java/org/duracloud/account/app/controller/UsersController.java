/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.AccountRights;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.model.Role;
import org.duracloud.account.db.util.AccountService;
import org.duracloud.account.db.util.DuracloudUserService;
import org.duracloud.account.db.util.error.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.*;

/**
 * 
 * @author Daniel Bernstein
 *         Date: Feb 17, 2012
 */
@Controller
@RequestMapping(UsersController.BASE_MAPPING)
public class UsersController extends AbstractRootController{
    public static final String BASE_MAPPING = RootConsoleHomeController.BASE_MAPPING + "/users";
    private static final String BASE_VIEW = BASE_MAPPING;
    public static final String EDIT_ACCOUNT_USERS_FORM_KEY = "accountUsersEditForm";
    
    @Autowired
    private DuracloudUserService userService;
    
    @RequestMapping("")
    public ModelAndView get() {
        List<User> u = new ArrayList<User>();
        Set<DuracloudUser> users = getRootAccountManagerService().listAllUsers(null);
        for(DuracloudUser user: users) {
            // Do not include root users in this view
            boolean rootUser = false;
            // User allowed to be removed from the system?
            boolean removable = true;
            Set<Account> accounts = new HashSet<Account>();
            if(user.getAccountRights() != null) {
                for(AccountRights account : user.getAccountRights()) {
                    if(user.isRootForAcct(account.getAccount().getId())) {
                        rootUser = true;
                        break;
                    }

                    try {

                    // User account relationship able to be deleted?
                    boolean deletable = true;

                    AccountInfo accountInfo = account.getAccount();
                    AccountService accountService = getAccountManagerService().getAccount(account.getAccount().getId());
//                    AccountService accountService = getAccountManagerService().getAccount(account.getAccountId());
//                    AccountInfo accountInfo = accountService.retrieveAccountInfo();

                    if(user.isOwnerForAcct(account.getAccount().getId())) {
                        if(!accountHasMoreThanOneOwner(accountService.getUsers(),
                                                       account.getAccount().getId())) {
                            deletable = false;
                            removable = false;
                        }
                    }

                    accounts.add(new Account(account.getAccount().getId(),
                                             accountInfo.getAcctName(),
                                             accountInfo.getSubdomain(),
                                             user.getRoleByAcct(account.getAccount().getId()),
                                             deletable));
                    
                    } catch (AccountNotFoundException e) {
                        log.error(e.getMessage(), e);
                    }

                }
            }
            if(!rootUser) {
                u.add(new User(user.getId(),
                               user.getUsername(),
                               user.getFirstName(),
                               user.getLastName(),
                               user.getEmail(),
                               removable,
                               accounts));
            }
        }
        Collections.sort(u);

        ModelAndView mav = new ModelAndView(BASE_VIEW);
        mav.addObject("users", u);
        mav.addObject(EDIT_ACCOUNT_USERS_FORM_KEY, new AccountUserEditForm());

        return mav;
    }
    
    @RequestMapping(value = { BY_ID_MAPPING + "/reset" }, method = RequestMethod.POST)
    public ModelAndView resetUsersPassword(
        @PathVariable Long id, RedirectAttributes redirectAttributes)
        throws Exception {
        log.debug("resetting user {}'s password.", id);    
        getRootAccountManagerService().resetUsersPassword(id);
            
        setSuccessFeedback("The username has been reset.", redirectAttributes);
        return createRedirectMav(BASE_VIEW);
    }

    
    @RequestMapping(value = BY_ID_DELETE_MAPPING , method = RequestMethod.POST)
    public ModelAndView deleteUser(
        @PathVariable Long id, RedirectAttributes redirectAttributes)
        throws Exception {
        log.info("delete user {}", id);

        //delete user
        getRootAccountManagerService().deleteUser(id);
        setSuccessFeedback("Successfully deleted user.", redirectAttributes);
        return createRedirectMav(BASE_VIEW);
    }
    
    
    @RequestMapping(value = BY_ID_MAPPING + "/revoke", method = RequestMethod.POST)
    public ModelAndView revokeUserRightsFromAccount(@PathVariable("id") Long userId,
                                                    @RequestParam(required=true) Long accountId,
                                                    RedirectAttributes redirectAttributes) 
                                                        throws Exception {

        log.info("revoking user {}'s rights from account {}", userId, accountId);
        String username = getUserService().loadDuracloudUserByIdInternal(userId).getUsername();
        String accountName = getAccountManagerService().getAccount(accountId)
                                                       .retrieveAccountInfo()
                                                       .getAcctName();

        getUserService().revokeUserRights(accountId, userId);
        String message = MessageFormat.format("Removed {0} from {1}", username, accountName);
        setSuccessFeedback(message, redirectAttributes);
        return createRedirectMav(BASE_MAPPING);
    }

    
    @RequestMapping(value = BY_ID_MAPPING + "/changerole", method = RequestMethod.POST)
    public ModelAndView changeUserRole(@PathVariable("id") Long userId,
                 @ModelAttribute @Valid AccountUserEditForm accountUserEditForm,
                 BindingResult result,
                 RedirectAttributes redirectAttributes) throws Exception {
        Long accountId = accountUserEditForm.getAccountId();
        log.debug("editUser account {}", accountId);

        boolean hasErrors = result.hasErrors();
        if (!hasErrors) {
            Role role = Role.valueOf(accountUserEditForm.getRole());
            log.info("New role: {}", role);
            setUserRights(userService, accountId, userId, role);
            setSuccessFeedback("Successfully changed user role.", redirectAttributes);                
        } else{
            setFailureFeedback("Unable to change the user role.",redirectAttributes);
        }

        return createRedirectMav(BASE_VIEW);
    }

    
    protected DuracloudUserService getUserService() {
        return userService;
    }

    public void setUserService(DuracloudUserService userService) {
        this.userService = userService;
    }

    public class User implements Comparable<User> {
        public User(
            Long id, String username, String firstName, String lastName,
            String email, boolean deletable, Set<Account> accounts) {
            super();
            this.id = id;
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.deletable = deletable;
            this.accounts = accounts;
        }

        private Long id;
        private String username;
        private String firstName;
        private String lastName;
        private String email;
        private boolean deletable;
        private Set<Account> accounts;

        public Long getId() {
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

        public boolean isDeletable() {
            return this.deletable;
        }

        public Set<Account> getAccounts() {
            return accounts;
        }

        @Override
        public int compareTo(User o) {
            return this.getUsername().compareTo(o.getUsername());
        }
    }

    public class Account implements Comparable<Account> {
        public Account(
            Long id, String accountName, String subdomain, Role role, boolean deletable) {
            super();
            this.id = id;
            this.accountName = accountName;
            this.subdomain = subdomain;
            this.role = role;
            this.deletable = deletable;
        }

        private Long id;
        private String accountName;
        private String subdomain;
        private Role role;
        private boolean deletable;

        public Long getId() {
            return id;
        }

        public String getAccountName() {
            return accountName;
        }

        public String getSubdomain() {
            return subdomain;
        }

        public Role getRole() {
            return role;
        }

        public boolean isDeletable() {
            return this.deletable;
        }

        @Override
        public int compareTo(Account a) {
            return this.getAccountName().compareTo(a.getAccountName());
        }
    }
}
