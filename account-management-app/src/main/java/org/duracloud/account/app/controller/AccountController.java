/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.common.domain.AccountCreationInfo;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountType;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.ServicePlan;
import org.duracloud.account.compute.error.DuracloudInstanceNotAvailableException;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.error.SubdomainAlreadyExistsException;
import org.duracloud.account.util.notification.NotificationMgr;
import org.duracloud.storage.domain.StorageProviderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
@Controller
@Lazy
public class AccountController extends AbstractAccountController {

    public static final String NEW_ACCOUNT_FORM_KEY = "newAccountForm";
    public static final String NEW_INSTANCE_FORM = "instanceForm";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private NotificationMgr notificationMgr;
   
    @RequestMapping(value = { ACCOUNT_PATH }, method = RequestMethod.GET)
    public String getHome(@PathVariable int accountId, Model model)
        throws AccountNotFoundException {
        loadAccountInfo(accountId, model);
        return ACCOUNT_HOME;
    }

    @RequestMapping(value = { STATEMENT_PATH }, method = RequestMethod.GET)
    public String getStatement(@PathVariable int accountId, Model model)
        throws AccountNotFoundException {
        loadAccountInfo(accountId, model);
        return "account-statement";
    }

    @RequestMapping(value = { INSTANCE_PATH }, method = RequestMethod.GET)
    public String getInstance(@PathVariable int accountId, Model model)
        throws AccountNotFoundException, DBNotFoundException, DuracloudInstanceNotAvailableException {
        populateAccountInModel(accountId, model);
        addUserToModel(model);
        return "account-instance";
    }

    private void populateAccountInModel(int accountId, Model model)
        throws AccountNotFoundException, DuracloudInstanceNotAvailableException {
        AccountInfo acctInfo = loadAccountInfo(accountId, model);
        loadAccountInstances(acctInfo, model);
    }

    private void loadAccountInstances(AccountInfo accountInfo, Model model)
        throws DuracloudInstanceNotAvailableException {
        Set<DuracloudInstanceService> instanceServices =
            instanceManagerService.getInstanceServices(accountInfo.getId());
        if(instanceServices.size() > 0) {
            // Handle only a single instance for the time being
            DuracloudInstanceService instanceService =
                instanceServices.iterator().next();
            model.addAttribute(INSTANCE_INFO_KEY,
                               instanceService.getInstanceInfo());
            model.addAttribute(INSTANCE_STATUS_KEY,
                               instanceService.getStatus());
        } else {
            if(accountInfo.getStatus().equals(AccountInfo.AccountStatus.ACTIVE) ||
               accountInfo.getStatus().equals(AccountInfo.AccountStatus.INACTIVE) ) {
                Set<String> versions = instanceManagerService.getVersions();
                model.addAttribute(DC_VERSIONS_KEY, versions);
                model.addAttribute(NEW_INSTANCE_FORM,
                                   new AccountInstanceForm());
            }
        }
    }

    @RequestMapping(value = { INSTANCE_START_PATH }, method = RequestMethod.POST)
    public ModelAndView startInstance(@PathVariable int accountId,
                                @ModelAttribute(NEW_INSTANCE_FORM) @Valid AccountInstanceForm instanceForm,
                                RedirectAttributes redirectAttributes)
        throws AccountNotFoundException, DuracloudInstanceNotAvailableException {
        if(instanceForm.getVersion() == null)
            instanceForm.setVersion(instanceManagerService.getLatestVersion());
        try{
            startInstance(accountId, instanceForm.getVersion());
            redirectAttributes.addFlashAttribute(ACTION_STATUS,
                               "Instance STARTED successfully, it will be " +
                               "available for use in 5 minutes.");
        }catch(RuntimeException ex){
            setError(ex,redirectAttributes);
        }

        String username =
            SecurityContextHolder.getContext().getAuthentication().getName();
        
        return createUserRedirectModelAndView(username);
    }



    @RequestMapping(value = { INSTANCE_AVAILABLE_PATH }, method = RequestMethod.POST)
    public ModelAndView instanceAvailable(@PathVariable int accountId,
                                    Model model)
        throws AccountNotFoundException, DuracloudInstanceNotAvailableException {
        DuracloudInstanceService instanceService = null;
        long start = System.currentTimeMillis();

        do {
            if(instanceService != null) {
                long now = System.currentTimeMillis();
                if(now - start > 300000) {
                    return null;
                } else {
                    sleep(10000);
                }
            }

            Set<DuracloudInstanceService> instanceServices =
                instanceManagerService.getInstanceServices(accountId);
            if(instanceServices.size() > 0) {
                instanceService =
                    instanceServices.iterator().next();
            }
        } while(!instanceService.getInstanceInfo().isInitialized());

        String username =
            SecurityContextHolder.getContext().getAuthentication().getName();
        return createUserRedirectModelAndView(username);
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch(InterruptedException e) {
        }
    }

    protected DuracloudInstanceService startInstance(int accountId, String version) {
        DuracloudInstanceService instanceService =
            instanceManagerService.createInstance(accountId, version);
        return instanceService;
    }

    @RequestMapping(value = { INSTANCE_UPGRADE_PATH }, method = RequestMethod.POST)
    public ModelAndView upgradeInstance(@PathVariable int accountId,
                                  @PathVariable int instanceId,
                                  Model model)
        throws AccountNotFoundException, DuracloudInstanceNotAvailableException {
        stopInstance(instanceId);
        startInstance(accountId, instanceManagerService.getLatestVersion());

        populateAccountInModel(accountId, model);
        model.addAttribute(ACTION_STATUS,
                           "Instance UPGRADED successfully, it will be " +
                           "available for use in 5 minutes.");

        String username =
            SecurityContextHolder.getContext().getAuthentication().getName();
        return createUserRedirectModelAndView(username);
    }

    @RequestMapping(value = { INSTANCE_RESTART_PATH }, method = RequestMethod.POST)
    public ModelAndView restartInstance(@PathVariable int accountId,
                                  @PathVariable int instanceId,
                                  Model model)
        throws AccountNotFoundException, DuracloudInstanceNotAvailableException {
        restartInstance(instanceId);
        populateAccountInModel(accountId, model);
        model.addAttribute(ACTION_STATUS,
                           "Instance RESTARTED successfully, it will be " +
                           "available for use in 5 minutes.");

        String username =
            SecurityContextHolder.getContext().getAuthentication().getName();
        return createUserRedirectModelAndView(username);
    }

    protected void restartInstance(int instanceId)
        throws DuracloudInstanceNotAvailableException {
        DuracloudInstanceService instanceService =
            instanceManagerService.getInstanceService(instanceId);
        instanceService.restart();
    }

    @RequestMapping(value = {INSTANCE_REINIT_USERS_PATH},
                    method = RequestMethod.POST)
    public ModelAndView reInitializeUserRoles(@PathVariable int accountId,
                                        @PathVariable int instanceId,
                                        Model model)
        throws AccountNotFoundException, DuracloudInstanceNotAvailableException {
        log.info("ReInit UserRoles for acct: {}, instance: {}",
                  accountId,
                  instanceId);

        DuracloudInstanceService instanceService = instanceManagerService.getInstanceService(
            instanceId);
        instanceService.reInitializeUserRoles();

        String status = "Instance User Roles ReInitialized successfully.";
        return reInitResult(accountId, model, status);
    }

    @RequestMapping(value = {INSTANCE_REINIT_PATH},
                    method = RequestMethod.POST)
    public ModelAndView reInitialize(@PathVariable int accountId,
                               @PathVariable int instanceId,
                               Model model)
        throws AccountNotFoundException, DuracloudInstanceNotAvailableException {
        log.info("ReInit Instance for acct: {}, instance: {}",
                  accountId,
                  instanceId);

        DuracloudInstanceService instanceService = instanceManagerService.getInstanceService(
            instanceId);
        instanceService.reInitialize();

        String status = "Instance ReInitialized successfully.";
        return reInitResult(accountId, model, status);
    }

    private ModelAndView reInitResult(int accountId, Model model, String status)
        throws AccountNotFoundException, DuracloudInstanceNotAvailableException {
        populateAccountInModel(accountId, model);
        model.addAttribute(ACTION_STATUS, status);

        String username = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();
        return createUserRedirectModelAndView(username);
    }

    @RequestMapping(value = { INSTANCE_STOP_PATH }, method = RequestMethod.POST)
    public ModelAndView stopInstance(@PathVariable int accountId,
                               @PathVariable int instanceId,
                               Model model)
        throws AccountNotFoundException, DuracloudInstanceNotAvailableException {
        stopInstance(instanceId);
        populateAccountInModel(accountId, model);
        model.addAttribute(ACTION_STATUS, "Instance STOPPED successfully.");

        String username =
            SecurityContextHolder.getContext().getAuthentication().getName();
        return createUserRedirectModelAndView(username);

    }

    protected void stopInstance(int instanceId)
        throws DuracloudInstanceNotAvailableException {
        DuracloudInstanceService instanceService =
            instanceManagerService.getInstanceService(instanceId);
        instanceService.stop();
    }

    @RequestMapping(value = { NEW_MAPPING }, method = RequestMethod.GET)
    public String openAddForm(Model model) throws DBNotFoundException {
        log.info("serving up new AccountForm");
        NewAccountForm newAccountForm = new NewAccountForm();
        newAccountForm.setServicePlan(ServicePlan.PROFESSIONAL.toString());
        newAccountForm.setAccountType(AccountType.FULL.toString());
        model.addAttribute(NEW_ACCOUNT_FORM_KEY, newAccountForm);
        addUserToModel(model);
        return NEW_ACCOUNT_VIEW;
    }

    @ModelAttribute("servicePlans")
    public List<String> getServicePlans() {
        List<String> plans = new ArrayList<String>();
        for (ServicePlan pType : ServicePlan.values()) {
            plans.add(pType.toString());
        }
        return plans;
    }

    @ModelAttribute("accountTypes")
    public List<String> getAccountTypes() {
        List<String> types = new ArrayList<String>();
        for (AccountType type: AccountType.values()) {
            types.add(type.toString());
        }
        return types;
    }

    @RequestMapping(value = { NEW_MAPPING }, method = RequestMethod.POST)
    public ModelAndView add(
        @ModelAttribute(NEW_ACCOUNT_FORM_KEY) @Valid NewAccountForm newAccountForm,
        BindingResult result, Model model) throws Exception {
        DuracloudUser user = getUser();

        if (!result.hasErrors()) {
            try {
                // TODO: This needs to be populated based on user selection of storage providers
                Set<StorageProviderType> secondaryStorageProviderTypes =
                    new HashSet<StorageProviderType>();

                int accountClusterId = getAccountClusterId(newAccountForm);

                AccountCreationInfo accountCreationInfo =
                    new AccountCreationInfo(newAccountForm.getSubdomain(),
                                            newAccountForm.getAcctName(),
                                            newAccountForm.getOrgName(),
                                            newAccountForm.getDepartment(),
                                            StorageProviderType.AMAZON_S3,
                                            secondaryStorageProviderTypes,
                                            ServicePlan.fromString(
                                                newAccountForm.getServicePlan()),
                                            AccountType.fromString(
                                                newAccountForm.getAccountType()),
                                            accountClusterId);

                AccountService service = this.accountManagerService.
                    createAccount(accountCreationInfo, user);

                int id = service.retrieveAccountInfo().getId();
                
                return createAccountRedirectModelAndView(id, "/providers");
            } catch (SubdomainAlreadyExistsException ex) {
                result.addError(new ObjectError("subdomain",
                    "The subdomain you selected is already in use. Please choose another."));
            }
        }

        addUserToModel(model);
        return new ModelAndView(NEW_ACCOUNT_VIEW, model.asMap());
    }

    private int getAccountClusterId(NewAccountForm newAccountForm) {
        int accountClusterId = -1;
        String formId = newAccountForm.getAccountClusterId();
        if(null != formId) {
            try {
                accountClusterId = Integer.valueOf(formId).intValue();
            } catch(NumberFormatException e) {
                log.error("NumberFormatException encountered attempting to " +
                          "convert accountClusterId value " + formId +
                          " to a number. Account with subdomain " +
                          newAccountForm.getSubdomain() +
                          " will be created without an associated cluster.");
                accountClusterId = -1;
            }
        }
        return accountClusterId;
    }

    @RequestMapping(value = { ACCOUNT_PATH + "/activate" }, method = RequestMethod.POST)
    public ModelAndView activate(@PathVariable int accountId)
        throws AccountNotFoundException, DBConcurrentUpdateException {
        AccountService accountService = accountManagerService.getAccount(
            accountId);
        accountService.storeAccountStatus(AccountInfo.AccountStatus.ACTIVE);

        String username =
            SecurityContextHolder.getContext().getAuthentication().getName();
        return createUserRedirectModelAndView(username);
    }

    @RequestMapping(value = { ACCOUNT_PATH + "/deactivate" }, method = RequestMethod.POST)
    public ModelAndView deactivate(@PathVariable int accountId,
                           Model model)
        throws AccountNotFoundException, DBConcurrentUpdateException {
        AccountService accountService = accountManagerService.getAccount(accountId);
        accountService.storeAccountStatus(AccountInfo.AccountStatus.INACTIVE);

        String username =
            SecurityContextHolder.getContext().getAuthentication().getName();
        return createUserRedirectModelAndView(username);
    }
    
    private ModelAndView createUserRedirectModelAndView(String username){
        RedirectView view = UserController.formatUserRedirect(username);
        return new ModelAndView(view);
    }

    @RequestMapping(value = { ACCOUNT_PATH + "/cancel" }, method = RequestMethod.POST)
    public ModelAndView cancel(@PathVariable int accountId,
                         Model model)
        throws AccountNotFoundException, DBConcurrentUpdateException, DuracloudInstanceNotAvailableException {

        //Verify there is not an instance running
        Set<DuracloudInstanceService> instanceServices =
            instanceManagerService.getInstanceServices(accountId);
        if(instanceServices.size() > 0) {
            throw new DuracloudInstanceNotAvailableException("An instance can not be running when trying to cancel an account.");
        }

        String username =
            SecurityContextHolder.getContext().getAuthentication().getName();

        //Cancel the account
        AccountService accountService = accountManagerService.getAccount(accountId);
        accountService.cancelAccount(username, notificationMgr.getEmailer(), notificationMgr.getConfig().getAdminAddresses());

        return createUserRedirectModelAndView(username);
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(
        AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

}
