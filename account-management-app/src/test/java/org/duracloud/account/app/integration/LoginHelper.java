/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.integration;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.Selenium;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class LoginHelper {
    private static Logger log = LoggerFactory.getLogger(LoginHelper.class);

    public static final RootUserCredential ROOT_USER =
        new RootUserCredential();
 
    public static void login(Selenium sc, String username, String password) {
        boolean success = loginQuietly(sc, username, password);
        //check for success
        Assert.assertTrue(success);
    }

    
    public static boolean loginQuietly(Selenium sc, String username, String password) {
        sc.type("username", username);
        sc.type("password", password);
        SeleniumHelper.clickAndWait(sc,"login-button");
        return isLoggedIn(sc);
    }
    
 
    public static void logout(Selenium sc) {
        UrlHelper.openRelative(sc, "/j_spring_security_logout");
        
    }

    /**
     * @param sc
     * @return
     */
    public static boolean isLoggedIn(Selenium sc) {
        return sc.isElementPresent("id=logout-link");
    }
    
    public static void loginRoot(Selenium sc) {
        login(sc, ROOT_USER.getUsername(), ROOT_USER.getPassword());
    }

}
