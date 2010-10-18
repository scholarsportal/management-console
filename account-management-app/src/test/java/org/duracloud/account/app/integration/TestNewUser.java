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

import com.thoughtworks.selenium.Selenium;

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
		UserTestHelper.createUser(sc,newusername, newpassword, "Walter", "Berglund", "walter@cerulean-mtr.org");
        SeleniumHelper.isTextPresent(sc,"Profile");
        SeleniumHelper.isTextPresent(sc,newusername);
	}
	
	
}
