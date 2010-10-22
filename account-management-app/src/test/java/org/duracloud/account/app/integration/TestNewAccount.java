/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.integration;

import org.junit.Test;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class TestNewAccount extends AbstractIntegrationTest{
	
	@Test
	public void createAccount(){
		sc.open(getAppRoot()+"/accounts/new");
		loginAdmin();
		String id = AccountTestHelper.createAccount(sc);
		isTextPresent(id);
		logout();
	}
}
