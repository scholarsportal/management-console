/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import org.junit.Assert;

import com.thoughtworks.selenium.Selenium;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class AccountTestHelper {

	public static String createAccount(Selenium sc, String orgName, String department, String subdomain, String accountName){
        sc.open(SeleniumHelper.getAppRoot()+"/accounts/new");
        sc.type("org-name-text", orgName);
        sc.type("department-text", department);
        sc.type("subdomain-text", subdomain);
        sc.type("acct-name-text", accountName);
        sc.click("id=create-account-button");
		SeleniumHelper.waitForPage(sc);
        String accountId =  sc.getText("id=account-id");
        Assert.assertTrue(accountId != null && !accountId.trim().equals(""));
        return accountId;
	}

	/**
	 * @return
	 */
	public static String createAccount(Selenium sc) {
		return createAccount(	sc, 
								"test", 
								"test", 
								System.currentTimeMillis()
								+ "", 
								"test");
    }
}
