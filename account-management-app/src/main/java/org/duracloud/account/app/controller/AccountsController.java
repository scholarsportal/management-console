/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudAccount;
import org.duracloud.account.common.domain.ServerDetails;
import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.compute.error.DuracloudInstanceNotAvailableException;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.DuracloudInstanceManagerService;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
/**
 * 
 * @author Daniel Bernstein
 *         Date: Feb 27, 2012
 *
 */
@Controller
@RequestMapping(AccountsController.BASE_MAPPING)
public class AccountsController extends AbstractRootController{
    public static final String BASE_MAPPING = RootConsoleHomeController.BASE_MAPPING + "/accounts";
    private static final String BASE_VIEW = BASE_MAPPING;
    private static final String ACCOUNT_SETUP_VIEW = BASE_VIEW+"/setup";
    private static final String ACCOUNT_SETUP_MAPPING = BY_ID_MAPPING + "/setup";
    private static final String SETUP_ACCOUNT_FORM_KEY = "setupAccountForm";

    @Autowired
    private DuracloudInstanceManagerService instanceManagerService;

    @RequestMapping("")
    public ModelAndView get() {
        List<DuracloudAccount> accounts = new ArrayList<DuracloudAccount>();
        for(AccountInfo accountInfo : getRootAccountManagerService().listAllAccounts(null)) {
            DuracloudAccount duracloudAccount = new DuracloudAccount();
            duracloudAccount.setAccountInfo(accountInfo);

            Set<DuracloudInstanceService> instanceServices =
                instanceManagerService.getInstanceServices(accountInfo.getId());
            if (instanceServices.size() > 0) {
                // Handle only a single instance for the time being
                DuracloudInstanceService instanceService = instanceServices.iterator()
                                                                           .next();
                try {
                    duracloudAccount.setInstanceStatus(instanceService.getStatus());
                } catch (DuracloudInstanceNotAvailableException e) {
                    log.warn(e.getMessage(), e);
                    duracloudAccount.setInstanceStatus("Unavailable");
                }
            }

            accounts.add(duracloudAccount);
        }
        
        ModelAndView mav = new ModelAndView(BASE_VIEW, "accounts", accounts);
        
        return mav;
    }

    @RequestMapping(value = { BY_ID_DELETE_MAPPING}, method = RequestMethod.POST)
    public ModelAndView delete(@PathVariable int id, RedirectAttributes redirectAttributes)
        throws AccountNotFoundException, DBConcurrentUpdateException {
        AccountService accountService = getAccountManagerService().getAccount(id);
        String accountName = accountService.retrieveAccountInfo().getAcctName();
        getRootAccountManagerService().deleteAccount(id);
        String message = MessageFormat.format("Successfully deleted account ({0}).", accountName);
        setSuccessFeedback(message, redirectAttributes);
        return createRedirectMav(BASE_MAPPING);
    }

    
    @RequestMapping(value = { BY_ID_MAPPING +"/activate"}, method = RequestMethod.POST)
    public ModelAndView activate(@PathVariable int id, RedirectAttributes redirectAttributes)
        throws AccountNotFoundException, DBConcurrentUpdateException {
        AccountService accountService = getAccountManagerService().getAccount(id);
        accountService.storeAccountStatus(AccountInfo.AccountStatus.ACTIVE);
        String accountName = accountService.retrieveAccountInfo().getAcctName();
        String message = MessageFormat.format("Successfully activated account ({0}).", accountName);
        setSuccessFeedback(message, redirectAttributes);
        return createRedirectMav(BASE_MAPPING);
    }

    @RequestMapping(value = { BY_ID_MAPPING +"/deactivate"}, method = RequestMethod.POST)
    public ModelAndView deactivate(@PathVariable int id, RedirectAttributes redirectAttributes)
        throws AccountNotFoundException, DBConcurrentUpdateException {
        AccountService accountService = getAccountManagerService().getAccount(id);
        accountService.storeAccountStatus(AccountInfo.AccountStatus.INACTIVE);
        String accountName = accountService.retrieveAccountInfo().getAcctName();
        String message = MessageFormat.format("Successfully deactivated account ({0}).", accountName);
        setSuccessFeedback(message, redirectAttributes);
        return createRedirectMav(BASE_MAPPING);

    }

    @RequestMapping(value = ACCOUNT_SETUP_MAPPING, method = RequestMethod.GET)
    public String getSetupAccount(
        @PathVariable int id, Model model)
        throws Exception {
        log.info("setup account {}", id);

        List<StorageProviderAccount> providers =
            getRootAccountManagerService().getSecondaryStorageProviders(id);

        model.addAttribute(SETUP_ACCOUNT_FORM_KEY, new AccountSetupForm());

        model.addAttribute("secProviders", providers);

        return ACCOUNT_SETUP_VIEW;
    }

    @RequestMapping(value = ACCOUNT_SETUP_MAPPING, method = RequestMethod.POST)
    public ModelAndView setupAccount(
        @PathVariable int id,
        @ModelAttribute(SETUP_ACCOUNT_FORM_KEY) @Valid AccountSetupForm accountSetupForm,
                       BindingResult result, Model model,
                       RedirectAttributes redirectAttributes)
        throws Exception {
        log.info("setup account {}", id);
        boolean hasErrors = result.hasErrors();

        if(!hasErrors) {
            if(accountSetupForm.getSecondaryId0() > 0) {
                if(StringUtils.isBlank(accountSetupForm.getSecondaryUsername0())) {
                    result.addError(new FieldError(SETUP_ACCOUNT_FORM_KEY,
                        "secondaryUsername0",
                        "Secondary Storage account's username is required"));
                    hasErrors = true;
                }
                if(StringUtils.isBlank(accountSetupForm.getSecondaryPassword0())) {
                    result.addError(new FieldError(SETUP_ACCOUNT_FORM_KEY,
                        "secondaryPassword0",
                        "Secondary Storage account's password is required"));
                    hasErrors = true;
                }
            }
            if(accountSetupForm.getSecondaryId1() > 0) {
                if(StringUtils.isBlank(accountSetupForm.getSecondaryUsername1())) {
                    result.addError(new FieldError(SETUP_ACCOUNT_FORM_KEY,
                        "secondaryUsername1",
                        "Secondary Storage account's username is required"));
                    hasErrors = true;
                }
                if(StringUtils.isBlank(accountSetupForm.getSecondaryPassword1())) {
                    result.addError(new FieldError(SETUP_ACCOUNT_FORM_KEY,
                        "secondaryPassword1",
                        "Secondary Storage account's password is required"));
                    hasErrors = true;
                }
            }
            if(accountSetupForm.getSecondaryId2() > 0) {
                if(StringUtils.isBlank(accountSetupForm.getSecondaryUsername2())) {
                    result.addError(new FieldError(SETUP_ACCOUNT_FORM_KEY,
                        "secondaryUsername2",
                        "Secondary Storage account's username is required"));
                    hasErrors = true;
                }
                if(StringUtils.isBlank(accountSetupForm.getSecondaryPassword2())) {
                    result.addError(new FieldError(SETUP_ACCOUNT_FORM_KEY,
                        "secondaryPassword2",
                        "Secondary Storage account's password is required"));
                    hasErrors = true;
                }
            }
        }

        if (hasErrors) {
            List<StorageProviderAccount> providers =
                getRootAccountManagerService().getSecondaryStorageProviders(id);

            model.addAttribute("secProviders", providers);

            return new ModelAndView(ACCOUNT_SETUP_VIEW);
        }

        AccountService accountService =
            getAccountManagerService().getAccount(id);
        ServerDetails serverDetails = accountService.retrieveServerDetails();

        //setup account
        getRootAccountManagerService().setupStorageProvider(
            serverDetails.getPrimaryStorageProviderAccountId(),
            accountSetupForm.getPrimaryStorageUsername(),
            accountSetupForm.getPrimaryStoragePassword());

        if(accountSetupForm.getSecondaryUsername0() != null) {
            //setup account
            getRootAccountManagerService().setupStorageProvider(
                accountSetupForm.getSecondaryId0(),
                accountSetupForm.getSecondaryUsername0(),
                accountSetupForm.getSecondaryPassword0());
        }

        if(accountSetupForm.getSecondaryUsername1() != null) {
            //setup account
            getRootAccountManagerService().setupStorageProvider(
                accountSetupForm.getSecondaryId1(),
                accountSetupForm.getSecondaryUsername1(),
                accountSetupForm.getSecondaryPassword1());
        }

        if(accountSetupForm.getSecondaryUsername2() != null) {
            //setup account
            getRootAccountManagerService().setupStorageProvider(
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

        getRootAccountManagerService().setupComputeProvider(
            serverDetails.getComputeProviderAccountId(),
            accountSetupForm.getComputeUsername(),
            accountSetupForm.getComputePassword(),
            accountSetupForm.getComputeElasticIP(),
            accountSetupForm.getComputeKeypair(),
            accountSetupForm.getComputeSecurityGroup());

        getRootAccountManagerService().activateAccount(id);
        setSuccessFeedback("Successfully setup and activated acccount", redirectAttributes);
        return createRedirectMav(BASE_MAPPING);
    }

    public DuracloudInstanceManagerService getInstanceManagerService() {
        return instanceManagerService;
    }

    public void
        setInstanceManagerService(DuracloudInstanceManagerService instanceManagerService) {
        this.instanceManagerService = instanceManagerService;
    }
}
