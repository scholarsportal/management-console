/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.RootAccountManagerService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author Daniel Bernstein
 *         Date: Feb 17, 2012
 */
public abstract class AbstractRootController extends AbstractController{

    @Autowired(required = true)
    private RootAccountManagerService rootAccountManagerService;

    @Autowired
    private AccountManagerService accountManagerService;
    
    public RootAccountManagerService getRootAccountManagerService() {
        return rootAccountManagerService;
    }

    protected void
        setRootAccountManagerService(RootAccountManagerService rootAccountManagerService) {
        this.rootAccountManagerService = rootAccountManagerService;
    }

    public String getBaseView() {
        return RootConsoleHomeController.BASE_MAPPING;
    }
    
    public AccountManagerService getAccountManagerService() {
        return accountManagerService;
    }

    public void
        setAccountManagerService(AccountManagerService accountManagerService) {
        this.accountManagerService = accountManagerService;
    }
}
