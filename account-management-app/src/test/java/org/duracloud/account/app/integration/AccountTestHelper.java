/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.integration;

import com.thoughtworks.selenium.Selenium;
import org.junit.Assert;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class AccountTestHelper {

    private AccountTestHelper() {
        // Ensures no instances are made of this class, as there are only static members.
    }

    public static String createAccount(Selenium sc,
                                       String orgName,
                                       String department,
                                       String subdomain,
                                       String accountName) {
        sc.open(SeleniumHelper.getAppRoot() + "/accounts/new");
        sc.type("org-name-text", orgName);
        sc.type("department-text", department);
        sc.type("subdomain-text", subdomain);
        sc.type("acct-name-text", accountName);
        sc.click("id=create-account-button");
        SeleniumHelper.waitForPage(sc);
        String accountId = sc.getText("id=account-id");
        Assert.assertTrue(accountId != null && !accountId.trim().equals(""));
        return accountId;
    }

    /**
     * @return
     */
    public static String createAccount(Selenium sc) {
        return createAccount(sc,
                             "test",
                             "test",
                             System.currentTimeMillis() + "",
                             "test");
    }
}
