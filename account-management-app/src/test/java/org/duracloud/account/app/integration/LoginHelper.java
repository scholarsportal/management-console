/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.Selenium;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class LoginHelper {
    private static Logger log = LoggerFactory.getLogger(LoginHelper.class);

    public static void login(Selenium sc, String username, String password) {
        loginWithoutCheckingForSuccess(sc, username, password);
        //check for success
        Assert.assertTrue(isLoggedIn(sc));
    }

    public static void loginWithoutCheckingForSuccess(Selenium sc, String username, String password) {
        sc.type("id=username", username);
        sc.type("id=password", password);
        sc.click("id=login-button");
        SeleniumHelper.waitForPage(sc);
        log.debug("after login: " + sc.getHtmlSource());
    }

    public static void logout(Selenium sc) {
        sc.open(SeleniumHelper.getAppRoot() + "/j_spring_security_logout");
        log.debug("after logout: " + sc.getHtmlSource());
    }

    /**
     * @param sc
     * @return
     */
    public static boolean isLoggedIn(Selenium sc) {
        return sc.getHtmlSource().contains("logout-link");
    }

}
