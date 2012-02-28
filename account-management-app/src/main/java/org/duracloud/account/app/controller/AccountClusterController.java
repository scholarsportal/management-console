/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.common.domain.AccountCluster;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.util.AccountClusterService;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.error.AccountClusterNotFoundException;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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
        try {
            getRootAccountManagerService().deleteAccountCluster(id);
        } catch (DBConcurrentUpdateException e) {
            log.error(e.getMessage(), e);
            throw new DuraCloudRuntimeException(e);
        }
    }

    private AccountManagerService getAccountManagerService() {
        return accountManagerService;
    }

    public void
        setAccountManagerService(AccountManagerService accountManagerService) {
        this.accountManagerService = accountManagerService;
    }

}
