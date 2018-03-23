/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.db.util.AccountManagerService;
import org.duracloud.account.db.util.RootAccountManagerService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Daniel Bernstein
 * Date: Feb 17, 2012
 */
public abstract class AbstractRootController extends AbstractController {

    @Autowired(required = true)
    private RootAccountManagerService rootAccountManagerService;

    @Autowired
    private AccountManagerService accountManagerService;

    public RootAccountManagerService getRootAccountManagerService() {
        return rootAccountManagerService;
    }

    protected void setRootAccountManagerService(RootAccountManagerService rootAccountManagerService) {
        this.rootAccountManagerService = rootAccountManagerService;
    }

    public String getBaseView() {
        return RootConsoleHomeController.BASE_MAPPING;
    }

    public AccountManagerService getAccountManagerService() {
        return accountManagerService;
    }

    public void setAccountManagerService(AccountManagerService accountManagerService) {
        this.accountManagerService = accountManagerService;
    }
}
