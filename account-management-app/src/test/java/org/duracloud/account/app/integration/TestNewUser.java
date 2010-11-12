/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class TestNewUser extends AbstractIntegrationTest {
	private String newusername = System.currentTimeMillis()+"";
	private String newpassword = "pw-" + newusername;
	
	@Test
    public void testNewUserLink() throws Exception {
        sc.open(getAppRoot()+"/");
        String newUserLink = "id=new-user-link";
        assertTrue(this.sc.isElementPresent(newUserLink));
        this.sc.click(newUserLink);
		SeleniumHelper.waitForPage(sc);
        confirmNewUserFormIsLoaded();
    }

	/**
	 * 
	 */
	private void confirmNewUserFormIsLoaded() {
		assertTrue(sc.isElementPresent("id=new-user-form"));
	}
	

	
	@Test
	public void createUser(){
		UserTestHelper.createUser(sc,newusername, newpassword, "Walter", "Berglund", "walter@cerulean-mtr.org");
        SeleniumHelper.waitForPage(sc);
		Assert.assertTrue(sc.isTextPresent("Profile"));
		Assert.assertTrue(sc.isTextPresent(newusername));
	}
	
	
}
