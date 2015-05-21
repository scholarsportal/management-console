/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.model.StorageProviderAccount;
import org.duracloud.account.db.util.AccountService;
import org.duracloud.account.db.util.error.AccountClusterNotFoundException;
import org.duracloud.account.db.util.error.AccountNotFoundException;
import org.duracloud.account.db.util.error.DBNotFoundException;
import org.duracloud.account.util.UserFeedbackUtil;
import org.duracloud.storage.domain.StorageProviderType;
import org.springframework.binding.message.Severity;
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
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Set;

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
    public String get(@PathVariable Long accountId, Model model)
        throws AccountNotFoundException, DBNotFoundException, AccountClusterNotFoundException {
        AccountInfo accountInfo = loadAccountInfo(accountId, model);
        loadBillingInfo(accountId, model);
        loadProviderInfo(accountId, model);

        DuracloudUser user = getUser();
        model.addAttribute(UserController.USER_KEY, user);
        model.addAttribute("userRole", user.getRoleByAcct(accountId));
        return ACCOUNT_DETAILS_VIEW_ID;
    }

    @RequestMapping(value = ACCOUNT_DETAILS_MAPPING + "/providers/add", method = RequestMethod.POST)
    @Transactional
    public ModelAndView addProvider(@PathVariable Long accountId,
                           @ModelAttribute("providerForm") @Valid ProviderForm providerForm,
					   BindingResult result,
					   Model model) throws AccountNotFoundException {
        log.info("addProvider account {}", accountId);

        AccountService accountService =
            accountManagerService.getAccount(accountId);
        accountService.addStorageProvider(StorageProviderType.fromString(
            providerForm.getProvider()));
        return createAccountRedirectModelAndView(accountId, ACCOUNT_DETAILS_PATH);
    }

    
    @RequestMapping(value = ACCOUNT_DETAILS_MAPPING + "/providers/{providerType}/remove", method = RequestMethod.POST)
    @Transactional
    public View removeProvider(@PathVariable Long accountId,
                                       @PathVariable String providerType,
                                       RedirectAttributes redirectAttributes)
        throws AccountNotFoundException {
        log.debug("attempting to remove provider {} from  account {}",
                  providerType,
                  accountId);
        AccountService accountService =
            accountManagerService.getAccount(accountId);

        StorageProviderType spType =
            StorageProviderType.fromString(providerType);

        Set<StorageProviderAccount> ssps =
            accountService.getSecondaryStorageProviders();
        boolean removed = false;
        for (StorageProviderAccount spa : ssps) {
            if (spa.getProviderType().equals(spType)) {
                accountService.removeStorageProvider(spa.getId());
                String message =
                    "Successfully removed provider (" + providerType + ")!";
                log.info(message + " from account " + accountId);
                UserFeedbackUtil.addFlash(message,
                                          Severity.INFO,
                                          redirectAttributes);
                removed = true;
                break;
            }
        }

        if (!removed) {
            String message =
                "Unable to remove provider ("
                    + providerType
                    + ").  A provider of that type is not a secondary provider associated with this account.";
            log.info(message + " from account " + accountId);
            UserFeedbackUtil.addFlash(message,
                                      Severity.ERROR,
                                      redirectAttributes);
        }

        return createAccountRedirectView(accountId,
                                                 ACCOUNT_DETAILS_PATH);
    }


    @RequestMapping(value = ACCOUNT_DETAILS_MAPPING + "/providers/rrs/enable", method = RequestMethod.POST)
    @Transactional
    public ModelAndView
        enableProviderRrs(@PathVariable Long accountId)
            throws AccountNotFoundException {
        log.info("enableProviderRrs account {}", accountId);

        setProviderRrs(accountId, true);

        return createAccountRedirectModelAndView(accountId, ACCOUNT_DETAILS_PATH);
    }

    @RequestMapping(value = ACCOUNT_DETAILS_MAPPING + "/providers/rrs/disable", method = RequestMethod.POST)
    @Transactional
    public ModelAndView disableProviderRrs(@PathVariable Long accountId,
					   Model model) throws AccountNotFoundException {
        log.info("disableProviderRrs account {}", accountId);

        setProviderRrs(accountId, false);

        return createAccountRedirectModelAndView(accountId, ACCOUNT_DETAILS_PATH);
    }


}
