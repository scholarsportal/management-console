/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.duracloud.storage.domain.StorageProviderType;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.CreditCardPaymentInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.InvoicePaymentInfo;
import org.duracloud.account.common.domain.PaymentInfo;
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

    protected void addAccountOwnersToModel(List<DuracloudUser> owners, Model model)
        throws AccountNotFoundException {
        model.addAttribute("accountOwners", owners);
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
        	
	protected void loadBillingInfo(int accountId, Model model) {
        //TODO LoadBillingInfo
        model.addAttribute("billingInfo", new CreditCardPaymentInfo());
    }


    protected void loadProviderInfo(int accountId, Model model)
        throws AccountNotFoundException {
        AccountService accountService =
            accountManagerService.getAccount(accountId);
        Set<StorageProviderType> providers = accountService.getStorageProviders();
        log.info("Providers: " + providers);
        
        model.addAttribute("providers", providers);

        ProviderForm providerForm = new ProviderForm();
        //TODO get available providers for account
        List<StorageProviderType> providerList = new ArrayList<StorageProviderType>();
        if(!providers.contains(StorageProviderType.AMAZON_S3))
            providerList.add(StorageProviderType.AMAZON_S3);
        if(!providers.contains(StorageProviderType.RACKSPACE))        
            providerList.add(StorageProviderType.RACKSPACE);

        if(providerList.size() > 0)
            providerForm.setStorageProviders(providerList);
        else
            providerForm.setStorageProviders(null);

        model.addAttribute("providerForm", providerForm);
    }
}
