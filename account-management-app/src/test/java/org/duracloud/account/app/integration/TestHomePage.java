/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */

public class TestHomePage extends AbstractIntegrationTest {
    @Test
    public void test() throws Exception {
        new RootBot(sc).loginRoot();
        openHome();
        assertTrue(isElementPresent("id=dc-logo"));
    }
}
