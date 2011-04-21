/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.common.domain.AccountCreationInfo;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.error.DuracloudInstanceNotAvailableException;
import org.duracloud.account.util.error.SubdomainAlreadyExistsException;
import org.duracloud.storage.domain.StorageProviderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.HashSet;
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
        throws AccountNotFoundException, DBNotFoundException {
        populateAccountInModel(accountId, model);
        addUserToModel(model);
        return "account-instance";
    }

    private void populateAccountInModel(int accountId, Model model)
        throws AccountNotFoundException {
        AccountInfo acctInfo = loadAccountInfo(accountId, model);
        loadAccountInstances(acctInfo, model);
    }

    private void loadAccountInstances(AccountInfo accountInfo, Model model) {
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
            if(accountInfo.getStatus().equals(AccountInfo.AccountStatus.ACTIVE)) {
                Set<String> versions = instanceManagerService.getVersions();
                model.addAttribute(DC_VERSIONS_KEY, versions);
                model.addAttribute(NEW_INSTANCE_FORM,
                                   new AccountInstanceForm());
            }
        }
    }

    @RequestMapping(value = { INSTANCE_START_PATH }, method = RequestMethod.POST)
    public String startInstance(@PathVariable int accountId,
                                @ModelAttribute(NEW_INSTANCE_FORM) @Valid AccountInstanceForm instanceForm,
                                Model model)
        throws AccountNotFoundException, DuracloudInstanceNotAvailableException {
        if(instanceForm.getVersion() == null)
            instanceForm.setVersion(instanceManagerService.getLatestVersion());
        startInstance(accountId, instanceForm.getVersion());
        populateAccountInModel(accountId, model);
        model.addAttribute(ACTION_STATUS,
                           "Instance STARTED successfully, it will be " +
                           "available for use in 5 minutes.");

        String username =
            SecurityContextHolder.getContext().getAuthentication().getName();
        return "redirect:" + PREFIX + "/users/byid/" + username;
    }

    protected void startInstance(int accountId, String version) {
        DuracloudInstanceService instanceService =
            instanceManagerService.createInstance(accountId, version);
    }

    @RequestMapping(value = { INSTANCE_RESTART_PATH }, method = RequestMethod.POST)
    public String restartInstance(@PathVariable int accountId,
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
        return "redirect:" + PREFIX + "/users/byid/" + username;
    }

    protected void restartInstance(int instanceId)
        throws DuracloudInstanceNotAvailableException {
        DuracloudInstanceService instanceService =
            instanceManagerService.getInstanceService(instanceId);
        instanceService.restart();
    }

    @RequestMapping(value = { INSTANCE_STOP_PATH }, method = RequestMethod.POST)
    public String stopInstance(@PathVariable int accountId,
                               @PathVariable int instanceId,
                               Model model)
        throws AccountNotFoundException, DuracloudInstanceNotAvailableException {
        stopInstance(instanceId);
        populateAccountInModel(accountId, model);
        model.addAttribute(ACTION_STATUS, "Instance STOPPED successfully.");

        String username =
            SecurityContextHolder.getContext().getAuthentication().getName();
        return "redirect:" + PREFIX + "/users/byid/" + username;
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
        model.addAttribute(NEW_ACCOUNT_FORM_KEY, new NewAccountForm());
        addUserToModel(model);
        return NEW_ACCOUNT_VIEW;
    }

    @RequestMapping(value = { NEW_MAPPING }, method = RequestMethod.POST)
    public String add(
        @ModelAttribute(NEW_ACCOUNT_FORM_KEY) @Valid NewAccountForm newAccountForm,
        BindingResult result, Model model) throws Exception {
        DuracloudUser user = getUser();

        if (!result.hasErrors()) {
            try {
                // TODO: This needs to be populated based on user selection of storage providers
                Set<StorageProviderType> secondaryStorageProviderTypes =
                    new HashSet<StorageProviderType>();

                AccountCreationInfo accountCreationInfo =
                    new AccountCreationInfo(newAccountForm.getSubdomain(),
                                            newAccountForm.getAcctName(),
                                            newAccountForm.getOrgName(),
                                            newAccountForm.getDepartment(),
                                            StorageProviderType.AMAZON_S3,
                                            secondaryStorageProviderTypes);

                AccountService service = this.accountManagerService.
                    createAccount(accountCreationInfo, user);

                int id = service.retrieveAccountInfo().getId();
                String idText = Integer.toString(id);
                return formatAccountRedirect(idText, "/providers");
            } catch (SubdomainAlreadyExistsException ex) {
                result.addError(new ObjectError("subdomain",
                    "The subdomain you selected is already in use. Please choose another."));
            }
        }

        addUserToModel(model);
        return NEW_ACCOUNT_VIEW;

    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(
        AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

}
