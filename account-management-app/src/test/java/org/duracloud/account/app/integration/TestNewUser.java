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
        sc.waitForPageToLoad("5000");
        confirmNewUserFormIsLoaded();
        
    }

	/**
	 * 
	 */
	private void confirmNewUserFormIsLoaded() {
		assertTrue(sc.isElementPresent("id=new-user-form"));
	}
}
