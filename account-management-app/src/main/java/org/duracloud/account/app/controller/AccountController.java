/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountType;
import org.duracloud.account.common.domain.ServicePlan;
import org.duracloud.account.compute.error.DuracloudInstanceNotAvailableException;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.UserFeedbackUtil;
import org.duracloud.account.util.error.AccountClusterNotFoundException;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.notification.NotificationMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

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
        throws AccountNotFoundException, AccountClusterNotFoundException {
        loadAccountInfo(accountId, model);
        return ACCOUNT_HOME;
    }

    @RequestMapping(value = { STATEMENT_PATH }, method = RequestMethod.GET)
    public String getStatement(@PathVariable int accountId, Model model)
        throws AccountNotFoundException, AccountClusterNotFoundException {
        loadAccountInfo(accountId, model);
        return "account-statement";
    }

    @RequestMapping(value = { INSTANCE_PATH }, method = RequestMethod.GET)
    public String getInstance(@PathVariable int accountId, Model model)
        throws AccountNotFoundException,
            DBNotFoundException,
            DuracloudInstanceNotAvailableException,
            AccountClusterNotFoundException {
        populateAccountInModel(accountId, model);
        addUserToModel(model);
        return "account-instance";
    }

    private void populateAccountInModel(int accountId, Model model)
        throws AccountNotFoundException,
            DuracloudInstanceNotAvailableException,
            AccountClusterNotFoundException {
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
    public ModelAndView
        upgradeInstance(@PathVariable int accountId,
                        @PathVariable int instanceId,
                        Model model)
            throws AccountNotFoundException,
                DuracloudInstanceNotAvailableException,
                AccountClusterNotFoundException {
        stopInstance(instanceId);
        startInstance(accountId, instanceManagerService.getLatestVersion());

        populateAccountInModel(accountId, model);
        model.addAttribute(ACTION_STATUS,
                           "Instance UPGRADED successfully, it will be "
                               + "available for use in 5 minutes.");

        String username =
            SecurityContextHolder.getContext().getAuthentication().getName();
        return createUserRedirectModelAndView(username);
    }

    @RequestMapping(value = { INSTANCE_RESTART_PATH }, method = RequestMethod.POST)
    public ModelAndView
        restartInstance(@PathVariable int accountId,
                        @PathVariable int instanceId,
                        Model model)
            throws AccountNotFoundException,
                DuracloudInstanceNotAvailableException,
                AccountClusterNotFoundException {
        restartInstance(instanceId);
        populateAccountInModel(accountId, model);
        model.addAttribute(ACTION_STATUS,
                           "Instance RESTARTED successfully, it will be "
                               + "available for use in 5 minutes.");

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
                                        Model model,
                                        RedirectAttributes redirectAttributes)        
                throws  AccountNotFoundException, 
                        DuracloudInstanceNotAvailableException, 
                        AccountClusterNotFoundException {

        log.info("ReInit UserRoles for acct: {}, instance: {}",
                  accountId,
                  instanceId);

        try{
            DuracloudInstanceService instanceService =
                instanceManagerService.getInstanceService(instanceId);
            instanceService.reInitializeUserRoles();
            UserFeedbackUtil.addSuccessFlash("Successfully reinitialized users!",
                                             redirectAttributes);
        }catch(Exception ex){
            log.error("failed to reinitialize service", ex);    
            UserFeedbackUtil.addFailureFlash("Unable to  reinitialize users. "+
                                             "Please try again in a few minutes.",
                                             redirectAttributes);
        }   
        return reInitResult(accountId, model, redirectAttributes);
    }

    @RequestMapping(value = {INSTANCE_REINIT_PATH},
                    method = RequestMethod.POST)
    public ModelAndView reInitialize(@PathVariable int accountId,
                               @PathVariable int instanceId,
                               Model model,
                               RedirectAttributes redirectAttributes)        
            throws  AccountNotFoundException, 
                    DuracloudInstanceNotAvailableException, 
                    AccountClusterNotFoundException {
        
        log.info("ReInit Instance for acct: {}, instance: {}",
                  accountId,
                  instanceId);


        try{
            DuracloudInstanceService instanceService =
                instanceManagerService.getInstanceService(instanceId);
            instanceService.reInitialize();
            UserFeedbackUtil.addSuccessFlash("Successfully reinitialized instance!",
                redirectAttributes);

        }catch(Exception ex){
            log.error("failed to reinitialize service", ex);
            UserFeedbackUtil.addFailureFlash("Unable to  reinitialize instance. "+
                                             "Please try again in a few minutes.",
                                             redirectAttributes);
        }

        return reInitResult(accountId, model, redirectAttributes);
    }

    private ModelAndView reInitResult(int accountId,
                                      Model model,
                                      RedirectAttributes redirectAttributes)
        throws AccountNotFoundException,
                DuracloudInstanceNotAvailableException,
                AccountClusterNotFoundException {
        populateAccountInModel(accountId, model);

        String username =
            SecurityContextHolder.getContext().getAuthentication().getName();
        return createUserRedirectModelAndView(username);
    }

    @RequestMapping(value = { INSTANCE_STOP_PATH }, method = RequestMethod.POST)
    public ModelAndView
        stopInstance(@PathVariable int accountId,
                     @PathVariable int instanceId,
                     Model model)
            throws AccountNotFoundException,
                DuracloudInstanceNotAvailableException,
                AccountClusterNotFoundException {
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

    @ModelAttribute("servicePlans")
    public List<ServicePlan> getServicePlans() {
        return Arrays.asList(ServicePlan.values());
    }

    @ModelAttribute("accountTypes")
    public List<AccountType> getAccountTypes() {
        return Arrays.asList(AccountType.values());
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
