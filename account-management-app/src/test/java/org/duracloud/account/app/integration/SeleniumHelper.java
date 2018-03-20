/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.integration;

import com.thoughtworks.selenium.Selenium;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class SeleniumHelper {
    private static Logger log = LoggerFactory.getLogger(SeleniumHelper.class);
    public static String DEFAULT_PAGE_LOAD_WAIT_IN_MS = "120000";

    private SeleniumHelper() {
        // Ensures no instances are made of this class, as there are only static members.
    }

    public static boolean isTextPresent(Selenium s, String text) {
        return s.isTextPresent(text);
    }

    public static String getAppRoot() {
        return "/ama";
    }

    /**
     * @param sc
     */
    public static void waitForPage(Selenium sc) {
        log.debug("waiting for page to load...");
        sc.waitForPageToLoad(SeleniumHelper.DEFAULT_PAGE_LOAD_WAIT_IN_MS);
        log.debug("body=" + sc.getBodyText());
    }

    public static void clickAndWait(Selenium sc, String locator) {
        sc.click(locator);
        log.debug("clicked " + locator);
        waitForPage(sc);
    }
}
