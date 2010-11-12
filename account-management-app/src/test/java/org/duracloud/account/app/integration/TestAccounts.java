/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.integration;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.SeleniumException;

/**
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class TestAccounts extends AbstractIntegrationTest {
	
	/* (non-Javadoc)
	 * @see org.duracloud.account.app.integration.AbstractIntegrationTest#before()
	 */
	@Override
	@Before
	public void before() throws Exception {
		super.before();
		openUserProfilePage("root");
		loginRoot();
	}

	/**
	 * 
	 */
	protected void loginRoot() {
		login("root","root");
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
		openAccounts();
		Assert.assertTrue(sc.isElementPresent("id=accounts-link"));
    }
    
	/**
	 * 
	 */
	private void openAccounts() {
		sc.open(getAppRoot()+"/accounts/");		
	}

	@Test
	public void testAuthorization(){
		try{
			logout();
			sc.open(getAppRoot()+"/login");
			loginAdmin();
			openAccounts();
		}catch(SeleniumException ex){
			Assert.assertTrue(ex.getMessage().contains("403"));
		}
	}

}
