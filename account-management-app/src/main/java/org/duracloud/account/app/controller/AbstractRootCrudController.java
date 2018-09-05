/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.db.util.RootAccountManagerService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Daniel Bernstein
 * Date: Feb 27, 2012
 */
public abstract class AbstractRootCrudController<T> extends AbstractCrudController<T> {

    public AbstractRootCrudController(Class<T> clazz) {
        super(clazz);
    }

    @Autowired(required = true)
    private RootAccountManagerService rootAccountManagerService;

    public RootAccountManagerService getRootAccountManagerService() {
        return rootAccountManagerService;
    }

    protected void setRootAccountManagerService(RootAccountManagerService rootAccountManagerService) {
        this.rootAccountManagerService = rootAccountManagerService;
    }

    public String getBaseView() {
        return RootConsoleHomeController.BASE_MAPPING;
    }

}
