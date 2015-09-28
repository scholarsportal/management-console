/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.db.util.AccountService;
import org.duracloud.account.db.util.error.AccountNotFoundException;
import org.duracloud.account.db.util.error.DBNotFoundException;
import org.duracloud.storage.domain.StorageProviderType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;


@Controller
@Lazy
public class ProviderController extends AbstractAccountController {
    public static final String PROVIDER_PATH = ACCOUNT_PATH + "/providers";

    @RequestMapping(value = { PROVIDER_PATH }, method = RequestMethod.GET)
    public String getProviders(@PathVariable Long accountId, Model model)
        throws AccountNotFoundException, DBNotFoundException {
        loadAccountInfo(accountId, model);
        loadProviderInfo(accountId, model);
        addUserToModel(model);
        return "account-providers";
    }

    @Transactional
    @RequestMapping(value = ACCOUNT_PATH + "/providers/add", method = RequestMethod.POST)
    public ModelAndView addProvider(@PathVariable Long accountId,
                           @ModelAttribute("providerForm") @Valid ProviderForm providerForm,
					   BindingResult result,
					   Model model) throws AccountNotFoundException {
        log.info("addProvider account {}", accountId);

        AccountService accountService =
            accountManagerService.getAccount(accountId);
        accountService.addStorageProvider(
            StorageProviderType.fromString(providerForm.getProvider()));
        return createAccountRedirectModelAndView(accountId, "/providers");
    }

    @Transactional
    @RequestMapping(value = ACCOUNT_PATH + "/providers/byid/{providerId}/delete", method = RequestMethod.POST)
    public ModelAndView deleteProviderFromAccount(
        @PathVariable Long accountId, @PathVariable Long providerId, Model model)
        throws AccountNotFoundException {
        log.info("delete provider {} from account {}", providerId, accountId);

        AccountService accountService =
            accountManagerService.getAccount(accountId);
        accountService.removeStorageProvider(providerId);
        return createAccountRedirectModelAndView(accountId, "/providers");
    }

}
