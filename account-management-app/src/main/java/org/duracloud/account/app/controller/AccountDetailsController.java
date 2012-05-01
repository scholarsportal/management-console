/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountType;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.error.AccountClusterNotFoundException;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.storage.domain.StorageProviderType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

/**
 * 
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
@Controller
@Lazy
public class AccountDetailsController extends AbstractAccountController {
    public static final String ACCOUNT_DETAILS_VIEW_ID = "account-details";
    public static final String ACCOUNT_DETAILS_PATH = "/details";
    public static final String ACCOUNT_DETAILS_MAPPING =
        ACCOUNT_PATH + ACCOUNT_DETAILS_PATH;

    @RequestMapping(value = ACCOUNT_DETAILS_MAPPING, method = RequestMethod.GET)
    public String get(@PathVariable int accountId, Model model)
        throws AccountNotFoundException, DBNotFoundException, AccountClusterNotFoundException {
        AccountInfo accountInfo = loadAccountInfo(accountId, model);
        loadBillingInfo(accountId, model);

        if(AccountType.FULL.equals(accountInfo.getType())) {
            loadProviderInfo(accountId, model);
        }

        DuracloudUser user = getUser();
        model.addAttribute(UserController.USER_KEY, user);
        model.addAttribute("userRole", user.getRoleByAcct(accountId));
        return ACCOUNT_DETAILS_VIEW_ID;
    }

    @RequestMapping(value = ACCOUNT_DETAILS_MAPPING + "/providers/add", method = RequestMethod.POST)
    public ModelAndView addProvider(@PathVariable int accountId,
                           @ModelAttribute("providerForm") @Valid ProviderForm providerForm,
					   BindingResult result,
					   Model model) throws AccountNotFoundException, DBConcurrentUpdateException {
        log.info("addProvider account {}", accountId);

        AccountService accountService =
            accountManagerService.getAccount(accountId);
        accountService.addStorageProvider(StorageProviderType.fromString(
            providerForm.getProvider()));
        return createAccountRedirectModelAndView(accountId, ACCOUNT_DETAILS_PATH);
    }

    @RequestMapping(value = ACCOUNT_DETAILS_MAPPING + "/providers/rrs/enable", method = RequestMethod.POST)
    public ModelAndView
        enableProviderRrs(@PathVariable int accountId)
            throws AccountNotFoundException,
                DBConcurrentUpdateException {
        log.info("enableProviderRrs account {}", accountId);

        setProviderRrs(accountId, true);

        return createAccountRedirectModelAndView(accountId, ACCOUNT_DETAILS_PATH);
    }

    @RequestMapping(value = ACCOUNT_DETAILS_MAPPING + "/providers/rrs/disable", method = RequestMethod.POST)
    public ModelAndView disableProviderRrs(@PathVariable int accountId,
					   Model model) throws AccountNotFoundException, DBConcurrentUpdateException {
        log.info("disableProviderRrs account {}", accountId);

        setProviderRrs(accountId, false);

        return createAccountRedirectModelAndView(accountId, ACCOUNT_DETAILS_PATH);
    }


}
