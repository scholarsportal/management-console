/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class HomeControllerTest {
    @Test
    public void test() throws Exception {
        HomeController c = new HomeController();
        assertNotNull(c.home());
    }

}
