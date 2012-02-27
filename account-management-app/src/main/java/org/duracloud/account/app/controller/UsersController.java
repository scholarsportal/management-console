/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Daniel Bernstein
 *         Date: Feb 17, 2012
 */
@Controller
@RequestMapping(UsersController.BASE_MAPPING)
public class UsersController extends AbstractRootController{
    public static final String BASE_MAPPING = RootConsoleHomeController.BASE_MAPPING + "/users";
    private static final String BASE_VIEW = BASE_MAPPING;
    
    @RequestMapping("")
    public ModelAndView get() {
        ModelAndView mav = new ModelAndView(BASE_VIEW);
        return mav;
    }

}
