/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.selenium.SeleniumException;

/**
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class TestAccount extends AbstractIntegrationTest {
	private String accountId = null;
	private Logger log = LoggerFactory.getLogger(getClass());
	
	/* (non-Javadoc)
	 * @see org.duracloud.account.app.integration.AbstractIntegrationTest#before()
	 */
	@Override
	@Before
	public void before() throws Exception {
		super.before();
		openUserProfilePage("admin");
		loginAdmin();
    	accountId = AccountTestHelper.createAccount(sc);
	}

	/* (non-Javadoc)
	 * @see org.duracloud.account.app.integration.AbstractIntegrationTest#after()
	 */
	@Override
	@After
	public void after() {
		logout();
		super.after();
	}

	@Test
	public void test(){
		Assert.assertNotNull(this.accountId);
		openAccountHome(this.accountId);
		testIfAccountIdElementIsPresent();
	}

	/**
	 * 
	 */
	private void testIfAccountIdElementIsPresent() {
		Assert.assertTrue(sc.isElementPresent("id=account-id"));
	}

	@Test
	public void testBillingOpenEditSave(){
		Assert.assertNotNull(this.accountId);
		openAccountDetails(this.accountId);
		sc.click("id=billing-edit-link");
		SeleniumHelper.waitForPage(sc);
		Assert.assertTrue(sc.isElementPresent("id=billing-edit-form"));
		sc.type("id=address-line-1", "address 1");
		sc.type("id=city", "Anytown");
		sc.type("id=state", "CA");
		sc.type("id=postal-code", "99999");
		sc.type("id=country", "usa");
		sc.type("id=cc-number", "1234123412341234");
		sc.type("id=cc-name", "Joe Duracloud");
		sc.type("id=cc-expiration", "10/2015");
		sc.type("id=cc-cvc", "999");
		
		sc.click("id=billing-save-button");
		SeleniumHelper.waitForPage(sc);
		
		Assert.assertTrue(!sc.isElementPresent("id=billing-edit-form"));
	}


	@Test
	public void testAccountTabs(){
		String[] tabs = {"dashboard", "statement", "instance", "users", "details"};
		for(String t : tabs){
			openAccountHome(accountId);
			String id = "account-"+t;
			log.debug("about to click id=" + id);
			sc.click("id="+id);
			waitForPage();
			testIfAccountIdElementIsPresent();
			log.debug("clicked id=" + id);
		}
	}
	
	
	@Test
	public void testFailedAuthorization(){
		try {
			logout();
			sc.open(getAppRoot()+"/login");
			login("user", "user");

			openAccountHome(accountId);
			Assert.assertTrue(false);
		} catch (SeleniumException e) {
			Assert.assertTrue(e.getMessage().contains("403"));
		}
	}
	
}
