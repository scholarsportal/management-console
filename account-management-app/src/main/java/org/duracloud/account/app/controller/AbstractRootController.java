/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.util.RootAccountManagerService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author Daniel Bernstein
 *         Date: Feb 17, 2012
 */
public class AbstractRootController extends AbstractController{

    public static final String  BY_ID_EDIT_MAPPING = "/byid/{id}/edit";
    public static final String BY_ID_DELETE_MAPPING = "/byid/{id}/delete";
    
    @Autowired(required = true)
    private RootAccountManagerService rootAccountManagerService;

    public RootAccountManagerService getRootAccountManagerService() {
        return rootAccountManagerService;
    }

    protected void
        setRootAccountManagerService(RootAccountManagerService rootAccountManagerService) {
        this.rootAccountManagerService = rootAccountManagerService;
    }

}
