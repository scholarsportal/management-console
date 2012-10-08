/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.Selenium;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class SeleniumHelper {
	private static Logger log = LoggerFactory.getLogger(SeleniumHelper.class);
	public static String DEFAULT_PAGE_LOAD_WAIT_IN_MS = "120000";
	  
	public static boolean isTextPresent(Selenium s, String text){
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
