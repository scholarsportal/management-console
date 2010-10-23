/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.integration;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.SeleniumException;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class TestUser extends AbstractIntegrationTest{

	/* (non-Javadoc)
	 * @see org.duracloud.account.app.integration.AbstractIntegrationTest#before()
	 */
	@Override
	@Before
	public void before() throws Exception {
		super.before();
		openUserProfilePage("admin");
		loginAdmin();
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
	public void testProfile(){
		this.isTextPresent("admin");
	}

	@Test
	public void testUnauthorizedAccessToAnotherUser(){
		try {
			openUserProfilePage("user");
		} catch (SeleniumException e) {
			Assert.assertTrue(e.getMessage().contains("403"));
		}
	}



}
