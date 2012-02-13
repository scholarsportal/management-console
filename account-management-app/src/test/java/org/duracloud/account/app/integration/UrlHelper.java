/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.Selenium;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class UrlHelper {
    private static Logger log = LoggerFactory.getLogger(UrlHelper.class);
    
    
    public static void open(Selenium sc, String location) {
        sc.open(location);
        SeleniumHelper.waitForPage(sc);
        log.debug("opened " + location);
        log.debug("after opening " + location + ": " + sc.getHtmlSource());
    }
    
    public static String formatURL(String path) {
        return SeleniumHelper.getAppRoot()+path;
    }

    public static void openRelative(Selenium sc, String location) {
        open(sc, SeleniumHelper.getAppRoot()+location);
    }
}
