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
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class UserBot extends BaseBot {
    private String username;
    private String password;
    public UserBot(Selenium sc, String username, String password) {
        super(sc);
        this.username = username;
        this.password = password;
    }

    public void login(){
        LoginHelper.login(sc, username, password);
    }
    public void openProfilePage() {
        UserHelper.openUserProfile(sc,username);

    }
}
