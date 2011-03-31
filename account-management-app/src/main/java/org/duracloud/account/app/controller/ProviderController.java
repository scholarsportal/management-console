/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.common.domain.StorageProviderAccount;
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

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
        accountService.addStorageProvider(
            StorageProviderType.fromString(providerForm.getProvider()));

        loadAccountInfo(accountId, model);
        loadProviderInfo(accountId, model);
        return "account-providers";
    }

    @RequestMapping(value = ACCOUNT_PATH + "/providers/byid/{providerId}/delete", method = RequestMethod.POST)
    public String deleteProviderFromAccount(
        @PathVariable int accountId, @PathVariable int providerId, Model model)
        throws AccountNotFoundException, DBConcurrentUpdateException {
        log.info("delete provider {} from account {}", providerId, accountId);

        AccountService accountService =
            accountManagerService.getAccount(accountId);
        accountService.removeStorageProvider(providerId);

        loadAccountInfo(accountId, model);
        loadProviderInfo(accountId, model);
        return "account-providers";
    }

    private void loadProviderInfo(int accountId, Model model)
        throws AccountNotFoundException {
        AccountService accountService =
            accountManagerService.getAccount(accountId);

        StorageProviderAccount primarySP =
            accountService.getPrimaryStorageProvider();
        model.addAttribute("primaryProvider", primarySP);

        Set<StorageProviderAccount> secondarySPs =
            accountService.getSecondaryStorageProviders();
        model.addAttribute("secondaryProviders", secondarySPs);

        // Get available providers for account
        ProviderForm providerForm = new ProviderForm();
        List<StorageProviderType> availableProviderTypes =
            new ArrayList<StorageProviderType>();

        Set<StorageProviderType> usedTypes = new HashSet<StorageProviderType>();
        for(StorageProviderAccount secondaryAcct : secondarySPs) {
            usedTypes.add(secondaryAcct.getProviderType());
        }

        if(!usedTypes.contains(StorageProviderType.RACKSPACE)) {
            availableProviderTypes.add(StorageProviderType.RACKSPACE);
        }
        if(!usedTypes.contains(StorageProviderType.MICROSOFT_AZURE)) {
            availableProviderTypes.add(StorageProviderType.MICROSOFT_AZURE);
        }

        if(availableProviderTypes.size() > 0) {
            providerForm.setStorageProviders(availableProviderTypes);
        } else {
            providerForm.setStorageProviders(null);
        }

        model.addAttribute("providerForm", providerForm);
    }
}
