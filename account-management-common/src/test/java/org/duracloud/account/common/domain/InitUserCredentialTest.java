/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

import org.duracloud.account.common.domain.InitUserCredential;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: Jan 31, 2011
 */
public class InitUserCredentialTest {

    @After
    public void tearDown() {
        System.clearProperty("init.username");
        System.clearProperty("init.password");
    }

    @Test
    public void testDefault() {
        InitUserCredential init = new InitUserCredential();
        Assert.assertEquals(init.getUsername(), "init");
        Assert.assertEquals(init.getPassword(), "ipw");
    }

    @Test
    public void testSystemProvided() {
        String username = "user-name";
        String password = "pass-word";
        System.setProperty("init.username", username);
        System.setProperty("init.password", password);

        InitUserCredential init = new InitUserCredential();
        Assert.assertEquals(init.getUsername(), username);
        Assert.assertEquals(init.getPassword(), password);
    }
}
