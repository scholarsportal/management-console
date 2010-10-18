/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.integration;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 *
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
        sc.waitForPageToLoad("1000");
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
        sc.open(getAppRoot()+"/users/new");
        sc.type("first-name-txt", "Ira");
        sc.type("last-name-txt", "Glass");
        sc.type("email-txt", "ira@thisamericanlife.org");
        sc.type("username-txt", newusername);
        sc.type("password-txt", newpassword);
        sc.type("password-confirm-txt", newpassword);
        sc.click("id=create-user-btn");
        sc.waitForPageToLoad("10000");
        
        this.isTextPresent("Profile");
        this.isTextPresent(newusername);

	}
}
