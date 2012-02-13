/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
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
