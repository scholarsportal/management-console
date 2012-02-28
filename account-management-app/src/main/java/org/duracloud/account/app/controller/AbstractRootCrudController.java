/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.util.RootAccountManagerService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author Daniel Bernstein
 *         Date: Feb 27, 2012
 */
public abstract class AbstractRootCrudController<T> extends AbstractCrudController<T>{

    public AbstractRootCrudController(Class<T> clazz) {
        super(clazz);
    }

    @Autowired(required = true)
    private RootAccountManagerService rootAccountManagerService;

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

}
