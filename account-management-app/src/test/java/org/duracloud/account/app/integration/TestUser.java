/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.Selenium;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class TestUser extends AbstractIntegrationTest{
    private String username;
    /* (non-Javadoc)
	 * @see org.duracloud.account.app.integration.AbstractIntegrationTest#before()
	 */
	@Override
	@Before
	public void before() throws Exception {
		super.before();
		username = createNewUser();
		openUserProfile();
	}

    /* (non-Javadoc)
	 * @see org.duracloud.account.app.integration.AbstractIntegrationTest#after()
	 */
	@Override
	@After
	public void after() {
	    deleteUserWithSeparateBrowser(username);
	    super.after();
	}

	@Test
	public void testProfile(){
        verifyUserProfilePage(username);
    }

	private void verifyUserProfilePage(String username) {
        Assert.assertTrue(isElementPresent("css=#dc-user"));
        Assert.assertTrue(isTextPresent(username));
    }

    @Test
	public void testUnauthorizedAccessToAnotherUser() throws Exception{
        openUserProfile();
        verifyUserProfilePage(username);
        logout();
	    String username2 = createNewUser();
        openUserProfile();
        verifyUserProfilePage(username2);
	    
	    logout();
        login(username, UserHelper.generatePassword(username));
        UserHelper.openUserProfile(sc, username2);
        Assert.assertTrue(isTextPresent("denied"));
        deleteUserWithSeparateBrowser(username2);
	}

}
