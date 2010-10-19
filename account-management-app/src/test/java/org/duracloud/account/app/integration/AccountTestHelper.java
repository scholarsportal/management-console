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
public class AccountTestHelper {

	public static void createAccount(Selenium sc, String orgName, String department, String subdomain, String accountName){
        sc.open(SeleniumHelper.getAppRoot()+"/accounts/new");
        sc.type("org-name-text", orgName);
        sc.type("department-text", department);
        sc.type("subdomain-text", subdomain);
        sc.type("acct-name-text", accountName);
        sc.click("id=create-account-button");
        sc.waitForPageToLoad("1000");
	}
}
