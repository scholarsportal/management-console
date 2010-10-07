/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * The default view for this application
 *
 * @contributor dbernstein
 */
@Controller
public class HomeController {
    private Logger log = LoggerFactory.getLogger(HomeController.class);

    @RequestMapping(value = {"/index.html",
                             "/",
                             "/home.html",
                             "/index",
                             "/home"})
    public ModelAndView home() {
        log.debug("serving up the home page at {}", System.currentTimeMillis());
        ModelAndView mav = new ModelAndView();
        mav.setViewName("home.page");
        mav.addObject("message", "Hello World");
        mav.addObject("time", new Date());
        return mav;
    }
}
