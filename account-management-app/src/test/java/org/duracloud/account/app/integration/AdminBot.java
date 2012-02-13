/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import com.thoughtworks.selenium.Selenium;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class AdminBot extends UserBot{

    public AdminBot(Selenium sc, String username, String password) {
        super(sc, username, password);
    }

}
