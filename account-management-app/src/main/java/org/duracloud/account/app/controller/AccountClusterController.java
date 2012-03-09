/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.duracloud.account.common.domain.AccountCluster;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.util.AccountClusterService;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.UrlHelper;
import org.duracloud.account.util.error.AccountClusterNotFoundException;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 
 * @author Daniel Bernstein
 *         Date: Feb 27, 2012
 */
@Controller
@RequestMapping(AccountClusterController.BASE_MAPPING)
public class AccountClusterController extends AbstractRootCrudController<AccountClusterForm>{

    private static final String RELATIVE_MAPPING = "/clusters";
    public static final String BASE_MAPPING =
        RootConsoleHomeController.BASE_MAPPING + RELATIVE_MAPPING;

    @Autowired
    private AccountManagerService accountManagerService;
    
    public AccountClusterController() {
        super(AccountClusterForm.class);
    }
   
    @Override
    protected String getBaseViewId() {
        return super.getBaseView() + RELATIVE_MAPPING;
    }
    
    @Override
    public ModelAndView get() {
        ModelAndView mav = new ModelAndView(getBaseViewId());
        mav.addObject("clusters",
                      getAccountManagerService().listAccountClusters(null));
        return mav;
    }

    @RequestMapping(BY_ID_MAPPING)
    public ModelAndView details(@PathVariable("id") int id) throws Exception{
        
        AccountManagerService ams = getAccountManagerService();
        AccountClusterService clusterService = ams.getAccountCluster(id);
        AccountCluster cluster = clusterService.retrieveAccountCluster();
        Set<Integer> accountIds = cluster.getClusterAccountIds();
        List<AccountInfo> accounts = loadAccounts(accountIds, ams);
        ModelAndView mav = new ModelAndView(getBaseViewId() + "/detail");
        mav.addObject("accounts", accounts);
        mav.addObject("cluster", cluster);
        return mav;
    }
    
    private List<AccountInfo> loadAccounts(Set<Integer> accountIds,
                                                AccountManagerService ams)
        throws AccountNotFoundException {

        List<AccountInfo> accounts =
            new ArrayList<AccountInfo>(accountIds.size());
        for (Integer accountId : accountIds) {
            AccountInfo accountInfo =
                ams.getAccount(accountId).retrieveAccountInfo();
            accounts.add(accountInfo);
        }
        return accounts;
    }

    @ModelAttribute
    public AccountSelectionForm accountSelectionForm(){
        return new AccountSelectionForm();
    }

    @RequestMapping(value = { BY_ID_MAPPING + "/remove-accounts" }, method = RequestMethod.POST)
    public ModelAndView
        removeAccounts(@PathVariable("id") int id,
                       AccountSelectionForm accountSelectionForm,
                       RedirectAttributes redirectAttributes) throws Exception {
        AccountManagerService ams = getAccountManagerService();
        AccountClusterService clusterService = ams.getAccountCluster(id);
        Integer[] accountIds = accountSelectionForm.getAccountIds();
        int removedCount = 0;
        if(accountIds != null){
            for(Integer accountId : accountIds){
                clusterService.removeAccountFromCluster(accountId);
                removedCount++;
            }
        }

        String pattern = "Removed {0} account(s) from this cluster.";
        String message = MessageFormat.format(pattern, removedCount);
        log.info(message);

        setSuccessFeedback(message, redirectAttributes);
        return createRedirectMav(UrlHelper.formatId(id, BASE_MAPPING+BY_ID_MAPPING));
    }

    @RequestMapping(value = { BY_ID_MAPPING + "/add-accounts" }, method = RequestMethod.GET)
    public ModelAndView
        getAddAccounts(@PathVariable("id") int id) throws Exception {
        
        AccountManagerService ams = getAccountManagerService();
        AccountClusterService clusterService = ams.getAccountCluster(id);
        AccountCluster cluster = clusterService.retrieveAccountCluster();
        Set<Integer> accountIds = cluster.getClusterAccountIds();
        Set<AccountInfo> allAccounts = getRootAccountManagerService().listAllAccounts(null);
        List<AccountInfo> accountsNotInCluster = new LinkedList<AccountInfo>(allAccounts);
        for(AccountInfo account : allAccounts){
            if(accountIds.contains(account.getId())){
                accountsNotInCluster.remove(account);
            }
        }

        ModelAndView mav = new ModelAndView(getBaseViewId()+"/add-accounts");
        mav.addObject("cluster", cluster);
        mav.addObject("accounts", accountsNotInCluster);
        return mav;

    }

    
    @RequestMapping(value = { BY_ID_MAPPING + "/add-accounts" }, method = RequestMethod.POST)
    public ModelAndView
        postAddAccounts(@PathVariable("id") int id,
                       AccountSelectionForm accountSelectionForm,
                       RedirectAttributes redirectAttributes) throws Exception {
        Integer[] accountIds = accountSelectionForm.getAccountIds();

        AccountManagerService ams = getAccountManagerService();
        AccountClusterService clusterService = ams.getAccountCluster(id);
        int addedCount = 0;
        if(accountIds != null){
            for(Integer accountId : accountIds){
                clusterService.addAccountToCluster(accountId);
                addedCount++;
            }
        }

        String pattern = "Add {0} account(s) to this cluster.";
        String message = MessageFormat.format(pattern, addedCount);
        log.info(message);

        setSuccessFeedback(message, redirectAttributes);
        return createRedirectMav(UrlHelper.formatId(id, BASE_MAPPING+BY_ID_MAPPING));
    }
    
    
    
    @Override
    protected void create(AccountClusterForm form) {
        getAccountManagerService().createAccountCluster(form.getName());
    }

    @Override
    protected Object getEntity(int id) {
        AccountClusterService service;
        try {
            service = getAccountManagerService().getAccountCluster(id);
            return service.retrieveAccountCluster();
        } catch (AccountClusterNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new DuraCloudRuntimeException(e);
        }
    }

    @Override
    protected AccountClusterForm loadForm(Object obj) {
        AccountCluster entity = (AccountCluster)obj;
        AccountClusterForm form = form();
        form.setName(entity.getClusterName());
        return form;
    }

    @Override
    protected void update(int id, AccountClusterForm form) {
        try {
            AccountClusterService acs = getAccountManagerService().getAccountCluster(id);
            acs.renameAccountCluster(form.getName());
        } catch (AccountClusterNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new DuraCloudRuntimeException(e);
        }
    }

    @Override
    protected void delete(int id) {
        getRootAccountManagerService().deleteAccountCluster(id);
    }

    private AccountManagerService getAccountManagerService() {
        return accountManagerService;
    }

    public void
        setAccountManagerService(AccountManagerService accountManagerService) {
        this.accountManagerService = accountManagerService;
    }

}
