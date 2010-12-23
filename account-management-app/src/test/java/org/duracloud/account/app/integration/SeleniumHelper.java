/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import com.thoughtworks.selenium.Selenium;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class SeleniumHelper {
	
	public static String DEFAULT_PAGE_LOAD_WAIT_IN_MS = "30000";
	  
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
		sc.waitForPageToLoad(SeleniumHelper.DEFAULT_PAGE_LOAD_WAIT_IN_MS);
	}
}
