/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
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
		Assert.assertTrue(sc.getHtmlSource().contains("Profile"));
		Assert.assertTrue(sc.getHtmlSource().contains(newusername));
	}
	
	
}
