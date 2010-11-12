/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
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
