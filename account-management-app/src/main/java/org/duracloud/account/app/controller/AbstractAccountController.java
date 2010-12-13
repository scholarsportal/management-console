/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.text.MessageFormat;
import java.util.Set;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The default view for this application
 * 
 * @contributor dbernstein
 */
@Lazy
@RequestMapping(AbstractAccountController.ACCOUNTS_PATH)
public abstract class AbstractAccountController extends AbstractController {
    public static final String ACCOUNTS_PATH = "/accounts";
    public static final String ACCOUNT_PATH = "/byid/{accountId}";
    public static final String EDIT_PATH = "/edit";
    public static final String ACCOUNT_INFO_KEY = "accountInfo";
    @Autowired(required = true)
    protected AccountManagerService accountManagerService;

    @Autowired(required = true)
    protected DuracloudUserService userService;
    public static final String NEW_ACCOUNT_VIEW = "account-new";
    public static final String ACCOUNT_HOME = "account-home";

    public AccountManagerService getAccountManagerService() {
        return accountManagerService;
    }

    public void setAccountManagerService(
        AccountManagerService accountManagerService) {
        this.accountManagerService = accountManagerService;
    }

    public DuracloudUserService getUserService() {
        return userService;
    }

    public void setUserService(DuracloudUserService userService) {
        this.userService = userService;
    }

    protected void addAccountInfoToModel(AccountInfo info, Model model){
        model.addAttribute("accountInfo", info);
    }

    protected void addAccountOwnerToModel(DuracloudUser owner, Model model)
        throws AccountNotFoundException {
        model.addAttribute("accountOwner", owner);
    }

    /**
     * @param accountId
     * @param model
     */
    protected void loadAccountInfo(int accountId, Model model)
        throws AccountNotFoundException {
        AccountService accountService =
            accountManagerService.getAccount(accountId);
        loadAccountInfo(accountService, model);
    }

    
    protected void loadAccountInfo(AccountService accountService, Model model){
        AccountInfo accountInfo = accountService.retrieveAccountInfo();
        addAccountInfoToModel(accountInfo, model);
    }

    /**
     * @return
     */
    protected DuracloudUser getOwner(int accountId, Set<DuracloudUser> users) {
        for (DuracloudUser user : users) {
            for (Role role : user.getRolesByAcct(accountId)) {
                if (role == Role.ROLE_OWNER) {
                    return user;
                }
            }
        }
        throw new RuntimeException(
            "No owner designated for this account - this should never happen");
    }

    /**
     * @param accountId
     * @param suffix
     * @return
     */
    protected String formatAccountRedirect(String accountId, String suffix) {
        return "redirect:"
            + MessageFormat.format(
                "{0}{1}{2}{3}", PREFIX, ACCOUNTS_PATH, ACCOUNT_PATH.replace(
                    "{accountId}", accountId), suffix);
    }
}
