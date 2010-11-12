/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import com.thoughtworks.selenium.Selenium;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class UserTestHelper {
	public static void createUser(Selenium sc, String username, String password, String first, String last, String email){
        sc.open(SeleniumHelper.getAppRoot()+"/users/new");
        sc.type("first-name-text", first);
        sc.type("last-name-text", last);
        sc.type("email-text", email);
        sc.type("username-text", username);
        sc.type("password-text", password);
        sc.type("password-confirm-text", password);
        sc.click("id=create-user-button");
		SeleniumHelper.waitForPage(sc);
	}
}
