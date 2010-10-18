/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @contributor "Daniel Bernstein (dbernstein@archive.org)"
 *
 */
public class TestUser extends AbstractIntegrationTest{

	/* (non-Javadoc)
	 * @see org.duracloud.account.app.integration.AbstractIntegrationTest#before()
	 */
	@Override
	@Before
	public void before() throws Exception {
		// TODO Auto-generated method stub
		super.before();
		openUserProfilePage();
		login();

	}

	protected void openUserProfilePage(){
		sc.open(getAppRoot()+"/users/byid/admin");
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

	/**
	 * 
	 */
	private void login() {
		login("admin","admin");
	}

	@Test
	public void testProfileAgain(){
		this.isTextPresent("admin");
	}

}
