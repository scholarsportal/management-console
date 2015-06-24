/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
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
