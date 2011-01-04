/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.util.Set;

import javax.validation.Valid;

import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.util.AccountService;
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


@Controller
@Lazy
public class ProviderController extends AbstractAccountController {
    public static final String PROVIDER_PATH = ACCOUNT_PATH + "/providers";

    @RequestMapping(value = { PROVIDER_PATH }, method = RequestMethod.GET)
    public String getProviders(@PathVariable int accountId, Model model)
        throws AccountNotFoundException {
        loadAccountInfo(accountId, model);
        loadProviderInfo(accountId, model);

        return "account-providers";
    }

    @RequestMapping(value = ACCOUNT_PATH + "/providers/add", method = RequestMethod.POST)
    public String addProvider(@PathVariable int accountId,
                           @ModelAttribute("providerForm") @Valid ProviderForm providerForm,
					   BindingResult result,
					   Model model) throws AccountNotFoundException, DBConcurrentUpdateException {
        log.info("addProvider account {}", accountId);

        AccountService accountService =
            accountManagerService.getAccount(accountId);
        Set<StorageProviderType> providers = accountService.getStorageProviders();
        log.info("Providers before add: " + providers);
        providers.add(StorageProviderType.fromString(providerForm.getProvider()));
        log.info("Providers after add: " + providers);
        accountService.setStorageProviders(providers);

        loadAccountInfo(accountId, model);
        loadProviderInfo(accountId, model);
        return "account-providers";
    }

    @RequestMapping(value = ACCOUNT_PATH + "/providers/byid/{provider}/delete", method = RequestMethod.POST)
    public String deleteProviderFromAccount(
        @PathVariable int accountId, @PathVariable String provider, Model model)
        throws AccountNotFoundException, DBConcurrentUpdateException {
        log.info("delete provider {} from account {}", provider, accountId);

        AccountService accountService =
            accountManagerService.getAccount(accountId);
        Set<StorageProviderType> providers = accountService.getStorageProviders();
        log.info("Providers before delete: " + providers);
        providers.remove(StorageProviderType.fromString(provider));
        log.info("Providers after delete: " + providers);
        accountService.setStorageProviders(providers);

        loadAccountInfo(accountId, model);
        loadProviderInfo(accountId, model);
        return "account-providers";
    }
}
