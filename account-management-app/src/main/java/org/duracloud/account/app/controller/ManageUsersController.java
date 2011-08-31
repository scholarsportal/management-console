/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudAccount;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.DuracloudInstanceManagerService;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.RootAccountManagerService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@Lazy
public class ManageUsersController extends AbstractController {

    public static final String EDIT_ACCOUNT_USERS_FORM_KEY = "accountUsersEditForm";
    public static final String ADD_ACCOUNT_USER_FORM_KEY = "addAccountUserForm";
    public static final String SETUP_ACCOUNT_FORM_KEY = "setupAccountForm";

    public static final String USERS_MANAGE = "/users/manage";
    public static final String REDIRECT_USERS_MANAGE = "redirect:" + USERS_MANAGE;

    public static final String USER_ADD_MAPPING =
        USERS_MANAGE + "/add";
    public static final String ACCOUNT_SETUP_MAPPING =
        USERS_MANAGE + "/accounts/byid/{accountId}/setup";
    public static final String ACCOUNT_DELETE_MAPPING =
        USERS_MANAGE + "/accounts/byid/{accountId}/delete";
    public static final String ACCOUNT_ACTIVATE_MAPPING =
        USERS_MANAGE + "/accounts/byid/{accountId}/activate";
    public static final String ACCOUNT_DEACTIVATE_MAPPING =
        USERS_MANAGE + "/accounts/byid/{accountId}/deactivate";
    public static final String USER_DELETE_ACCOUNT_MAPPING =
        USERS_MANAGE + "/accounts/byid/{accountId}/users/byid/{userId}/delete";
    public static final String USER_EDIT_MAPPING =
        USERS_MANAGE + "/accounts/byid/{accountId}/users/byid/{userId}/edit";
    public static final String USER_DELETE_MAPPING =
        USERS_MANAGE + "/byid/{userId}/delete";
    public static final String RESET_USER_MAPPING =
        USERS_MANAGE + "/byid/{userId}/reset";

    @Autowired(required = true)
    protected RootAccountManagerService rootAccountManagerService;

    @Autowired(required = true)
    protected DuracloudUserService userService;

    @Autowired(required = true)
    protected AccountManagerService accountManagerService;

    @Autowired(required = true)
    protected DuracloudInstanceManagerService instanceManagerService;

    public void setRootAccountManagerService(
        RootAccountManagerService rootAccountManagerService) {
        this.rootAccountManagerService = rootAccountManagerService;
    }

    public void setUserService(DuracloudUserService userService) {
        this.userService = userService;
    }

    public void setAccountManagerService(
        AccountManagerService accountManagerService) {
        this.accountManagerService = accountManagerService;
    }

	@RequestMapping(value = { "/users/manage" }, method = RequestMethod.GET)
	public String getUsers(Model model)
        throws Exception {
        List<User> u = new ArrayList<User>();
        Set<DuracloudUser> users = rootAccountManagerService.listAllUsers(null);
        for(DuracloudUser user: users) {
            // Do not include root users in this view
            boolean rootUser = false;
            // User allowed to be removed from the system?
            boolean removable = true;
            Set<Account> accounts = new HashSet<Account>();
            if(user.getAccountRights() != null) {
                for(AccountRights account : user.getAccountRights()) {
                    if(user.isRootForAcct(account.getAccountId())) {
                        rootUser = true;
                        break;
                    }

                    // User account relationship able to be deleted?
                    boolean deletable = true;

                    AccountService accountService =
                        accountManagerService.getAccount(account.getAccountId());
                    AccountInfo accountInfo = accountService.retrieveAccountInfo();

                    if(user.isOwnerForAcct(account.getAccountId())) {
                        if(!accountHasMoreThanOneOwner(accountService.getUsers(),
                                                       account.getAccountId())) {
                            deletable = false;
                            removable = false;
                        }
                    }

                    accounts.add(new Account(account.getAccountId(),
                                             accountInfo.getAcctName(),
                                             accountInfo.getSubdomain(),
                                             user.getRoleByAcct(account.getAccountId()),
                                             deletable));
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

		model.addAttribute("users", u);

        List<DuracloudAccount> accounts = new ArrayList<DuracloudAccount>();
        for(AccountInfo accountInfo : rootAccountManagerService.listAllAccounts(null)) {
            DuracloudAccount duracloudAccount = new DuracloudAccount();
            duracloudAccount.setAccountInfo(accountInfo);

            Set<DuracloudInstanceService> instanceServices =
                instanceManagerService.getInstanceServices(accountInfo.getId());
            if (instanceServices.size() > 0) {
                // Handle only a single instance for the time being
                DuracloudInstanceService instanceService = instanceServices.iterator()
                                                                           .next();
                duracloudAccount.setInstanceStatus(instanceService.getStatus());
            }

            accounts.add(duracloudAccount);
        }
		model.addAttribute("accounts", accounts);

        addUserToModel(model);
        model.addAttribute(EDIT_ACCOUNT_USERS_FORM_KEY, new AccountUserEditForm());
        model.addAttribute(ADD_ACCOUNT_USER_FORM_KEY, new AccountUserAddForm());
		return "manage-users";
	}

    @RequestMapping(value = USER_ADD_MAPPING, method = RequestMethod.POST)
    public String addUser(@ModelAttribute("addAccountUserForm") @Valid AccountUserAddForm accountUserAddForm,
					   BindingResult result,
					   Model model) throws Exception {
        int accountId = accountUserAddForm.getAccountId();
        int userId = accountUserAddForm.getUserId();
        log.debug("addUser account {} user {}", accountId, userId);

        Role role = Role.valueOf(accountUserAddForm.getRole());
        try {
            setUserRights(userService,
                          accountId,
                          userId,
                          role);
        } catch(AccessDeniedException e) {
            result.addError(new ObjectError("role",
                                "You are unauthorized to set the role for this user"));
            return "manage-users";
        }

        return REDIRECT_USERS_MANAGE;
    }

    @RequestMapping(value = USER_EDIT_MAPPING, method = RequestMethod.POST)
    public String editUser(@PathVariable int accountId, @PathVariable int userId,
                           @ModelAttribute("accountUsersEditForm") @Valid AccountUserEditForm accountUserEditForm,
					   BindingResult result,
					   Model model) throws Exception {
        log.debug("editUser account {}", accountId);

        Role role = Role.valueOf(accountUserEditForm.getRole());
        log.info("New role: {}", role);

        try {
            setUserRights(userService, accountId, userId, role);
        } catch(AccessDeniedException e) {
            result.addError(new ObjectError("role",
                                "You are unauthorized to set the role for this user"));
            return "manage-users";
        }

        return REDIRECT_USERS_MANAGE;
    }

    @RequestMapping(value = USER_DELETE_ACCOUNT_MAPPING, method = RequestMethod.POST)
    public String deleteUserFromAccount(
        @PathVariable int accountId, @PathVariable int userId, Model model)
        throws Exception {
        log.info("delete user {} from account {}", userId, accountId);
        
        userService.revokeUserRights(accountId, userId);

        return REDIRECT_USERS_MANAGE;
    }

    @RequestMapping(value = USER_DELETE_MAPPING, method = RequestMethod.POST)
    public String deleteUser(
        @PathVariable int userId, Model model)
        throws Exception {
        log.info("delete user {}", userId);

        //delete user
        rootAccountManagerService.deleteUser(userId);

        return REDIRECT_USERS_MANAGE;
    }

    @RequestMapping(value = ACCOUNT_DELETE_MAPPING, method = RequestMethod.POST)
    public String deleteAccount(
        @PathVariable int accountId, Model model)
        throws Exception {
        log.info("delete account {}", accountId);

        //delete account
        rootAccountManagerService.deleteAccount(accountId);

        return REDIRECT_USERS_MANAGE;
    }

    @RequestMapping(value = ACCOUNT_SETUP_MAPPING, method = RequestMethod.GET)
    public String getSetupAccount(
        @PathVariable int accountId, Model model)
        throws Exception {
        log.info("setup account {}", accountId);

        List<StorageProviderAccount> providers =
            rootAccountManagerService.getSecondaryStorageProviders(accountId);

        model.addAttribute(SETUP_ACCOUNT_FORM_KEY, new AccountSetupForm());

        model.addAttribute("secProviders", providers);

        return "account-setup";
    }

    @RequestMapping(value = ACCOUNT_SETUP_MAPPING, method = RequestMethod.POST)
    public String setupAccount(
        @PathVariable int accountId,
        @ModelAttribute(SETUP_ACCOUNT_FORM_KEY) @Valid AccountSetupForm accountSetupForm,
					   BindingResult result, Model model)
        throws Exception {
        log.info("setup account {}", accountId);
        boolean hasErrors = result.hasErrors();

        if(!hasErrors) {
            if(accountSetupForm.getSecondaryId0() > 0) {
                if(accountSetupForm.getSecondaryUsername0() == null ||
                   accountSetupForm.getSecondaryUsername0().equals("")) {
                    result.addError(new FieldError(SETUP_ACCOUNT_FORM_KEY,
                        "secondaryUsername0",
                        "Secondary Storage account's username is required"));
                    hasErrors = true;
                }
                if(accountSetupForm.getSecondaryPassword0() == null ||
                   accountSetupForm.getSecondaryPassword0().equals("")) {
                    result.addError(new FieldError(SETUP_ACCOUNT_FORM_KEY,
                        "secondaryPassword0",
                        "Secondary Storage account's password is required"));
                    hasErrors = true;
                }
            }
            if(accountSetupForm.getSecondaryId1() > 0) {
                if(accountSetupForm.getSecondaryUsername1() == null ||
                   accountSetupForm.getSecondaryUsername1().equals("")) {
                    result.addError(new FieldError(SETUP_ACCOUNT_FORM_KEY,
                        "secondaryUsername1",
                        "Secondary Storage account's username is required"));
                    hasErrors = true;
                }
                if(accountSetupForm.getSecondaryPassword1() == null ||
                   accountSetupForm.getSecondaryPassword1().equals("")) {
                    result.addError(new FieldError(SETUP_ACCOUNT_FORM_KEY,
                        "secondaryPassword1",
                        "Secondary Storage account's password is required"));
                    hasErrors = true;
                }
            }
            if(accountSetupForm.getSecondaryId2() > 0) {
                if(accountSetupForm.getSecondaryUsername2() == null ||
                   accountSetupForm.getSecondaryUsername2().equals("")) {
                    result.addError(new FieldError(SETUP_ACCOUNT_FORM_KEY,
                        "secondaryUsername2",
                        "Secondary Storage account's username is required"));
                    hasErrors = true;
                }
                if(accountSetupForm.getSecondaryPassword2() == null ||
                   accountSetupForm.getSecondaryPassword2().equals("")) {
                    result.addError(new FieldError(SETUP_ACCOUNT_FORM_KEY,
                        "secondaryPassword2",
                        "Secondary Storage account's password is required"));
                    hasErrors = true;
                }
            }
        }

        if (hasErrors) {
            List<StorageProviderAccount> providers =
                rootAccountManagerService.getSecondaryStorageProviders(accountId);

            model.addAttribute("secProviders", providers);

            return "account-setup";
        }

        AccountInfo accountInfo = rootAccountManagerService.getAccount(accountId);

        //setup account
        rootAccountManagerService.setupStorageProvider(
            accountInfo.getPrimaryStorageProviderAccountId(),
            accountSetupForm.getPrimaryStorageUsername(),
            accountSetupForm.getPrimaryStoragePassword());

        if(accountSetupForm.getSecondaryUsername0() != null) {
            //setup account
            rootAccountManagerService.setupStorageProvider(
                accountSetupForm.getSecondaryId0(),
                accountSetupForm.getSecondaryUsername0(),
                accountSetupForm.getSecondaryPassword0());
        }

        if(accountSetupForm.getSecondaryUsername1() != null) {
            //setup account
            rootAccountManagerService.setupStorageProvider(
                accountSetupForm.getSecondaryId1(),
                accountSetupForm.getSecondaryUsername1(),
                accountSetupForm.getSecondaryPassword1());
        }

        if(accountSetupForm.getSecondaryUsername2() != null) {
            //setup account
            rootAccountManagerService.setupStorageProvider(
                accountSetupForm.getSecondaryId2(),
                accountSetupForm.getSecondaryUsername2(),
                accountSetupForm.getSecondaryPassword2());
        }

        if(accountSetupForm.isComputeCredentialsSame()) {
            accountSetupForm.setComputeUsername(
                accountSetupForm.getPrimaryStorageUsername());
            accountSetupForm.setComputePassword(
                accountSetupForm.getPrimaryStoragePassword());
        }

        rootAccountManagerService.setupComputeProvider(
            accountInfo.getComputeProviderAccountId(),
            accountSetupForm.getComputeUsername(),
            accountSetupForm.getComputePassword(),
            accountSetupForm.getComputeElasticIP(),
            accountSetupForm.getComputeKeypair(),
            accountSetupForm.getComputeSecurityGroup());

        rootAccountManagerService.activateAccount(accountId);

        return REDIRECT_USERS_MANAGE;
    }

    @RequestMapping(value = { RESET_USER_MAPPING }, method = RequestMethod.POST)
    public String resetUsersPassword(
        @PathVariable int userId, Model model)
        throws Exception {
            rootAccountManagerService.resetUsersPassword(userId);

        return REDIRECT_USERS_MANAGE;            
    }

    @RequestMapping(value = { ACCOUNT_ACTIVATE_MAPPING }, method = RequestMethod.POST)
    public String activate(@PathVariable int accountId,
                           Model model)
        throws AccountNotFoundException, DBConcurrentUpdateException {
        AccountService accountService = accountManagerService.getAccount(accountId);
        accountService.storeAccountStatus(AccountInfo.AccountStatus.ACTIVE);

        return REDIRECT_USERS_MANAGE;
    }

    @RequestMapping(value = { ACCOUNT_DEACTIVATE_MAPPING }, method = RequestMethod.POST)
    public String deactivate(@PathVariable int accountId,
                           Model model)
        throws AccountNotFoundException, DBConcurrentUpdateException {
        AccountService accountService = accountManagerService.getAccount(accountId);
        accountService.storeAccountStatus(AccountInfo.AccountStatus.INACTIVE);

        return REDIRECT_USERS_MANAGE;
    }


    protected void addUserToModel(Model model) throws DBNotFoundException {
        model.addAttribute(UserController.USER_KEY, getUser());
    }

    /**
     * @return
     */
    protected DuracloudUser getUser() throws DBNotFoundException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String username = authentication.getName();
        return this.userService.loadDuracloudUserByUsername(username);
    }

    public class User implements Comparable<User> {
        public User(
            int id, String username, String firstName, String lastName,
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

        private int id;
        private String username;
        private String firstName;
        private String lastName;
        private String email;
        private boolean deletable;
        private Set<Account> accounts;

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
            int id, String accountName, String subdomain, Role role, boolean deletable) {
            super();
            this.id = id;
            this.accountName = accountName;
            this.subdomain = subdomain;
            this.role = role;
            this.deletable = deletable;
        }

        private int id;
        private String accountName;
        private String subdomain;
        private Role role;
        private boolean deletable;

        public int getId() {
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
