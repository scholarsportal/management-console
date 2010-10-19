/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.integration;

import com.thoughtworks.selenium.Selenium;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class LoginHelper {
	public static void login(Selenium sc, String username, String password){
		sc.type("id=username", username);
		sc.type("id=password", password);
		sc.click("id=login-button");
		sc.waitForPageToLoad("5000");
	}

	public static void logout(Selenium sc){
		sc.open(SeleniumHelper.getAppRoot()+"/j_spring_security_logout");
		sc.waitForPageToLoad("5000");
	}

}
