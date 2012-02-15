/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class TestRootConsole extends AbstractIntegrationTest {

    private RootBot rootBot;
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.duracloud.account.app.integration.AbstractIntegrationTest#before()
     */
    @Override
    @Before
    public void before() throws Exception {
        super.before();
        this.rootBot = new RootBot(sc);
        openHome();
        LoginHelper.loginRoot(sc);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.duracloud.account.app.integration.AbstractIntegrationTest#after()
     */
    @Override
    @After
    public void after() {
        logout();
        super.after();
    }

    @Test
    public void testClickRootConsoleLink() {
        this.rootBot.openProfilePage();
        this.rootBot.clickRootConsoleLink();
        this.rootBot.confirmManageUsersPageIsLoaded();
    }
    
    @Test
    public void testDeleteUser() {
        logout();
        String username = createNewUser();
        rootBot.deleteUser(username);
        rootBot.deleteAllTestUsers();
    }
    
    /*
    @Test
    public void testAddUserToAccount() {
        //TODO implement test
    }
    

    @Test
    public void testRestPassword() {
        //TODO implement test
    }

    */

}
