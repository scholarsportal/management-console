/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
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
