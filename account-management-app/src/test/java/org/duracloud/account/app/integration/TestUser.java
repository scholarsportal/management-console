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
		openUserProfilePage(TEST_USER_1);
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
		this.isTextPresent(TEST_USER_1);
	}

	@Test
	public void testUnauthorizedAccessToAnotherUser(){
		try {
			openUserProfilePage(TEST_USER_2);
		} catch (SeleniumException e) {
			Assert.assertTrue(e.getMessage().contains("403"));
		}
	}



}
