/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/**
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class RootAccountManagerServiceImplTest {
	private RootAccountManagerServiceImpl rootService;
	@Before 
	public void before(){
		rootService = createRootService(AccountUtilTestData.createMockUserRepo(),AccountUtilTestData.createMockAccountRepo());
	}

	

	@Test
	public void testAddDuracloudImage() {
		//TODO implement test;
	}

	@Test
	public void testListAllAccounts() {
		Assert.assertTrue(rootService.listAllAccounts(null).size() > 1);
	}

	@Test
	public void testListAllAccountsWithFilter() {
		Assert.assertTrue(rootService.listAllAccounts(AccountUtilTestData.ORG_PREFIX+AccountUtilTestData.ACCOUNT_IDS[0]).size() == 1);
		
	}

	@Test
	public void testListAllUsersNoFilter() {
		Assert.assertTrue(rootService.listAllUsers(null).size() > 1);
	}

	@Test
	public void testListAllUsersFilter() {
		Assert.assertTrue(rootService.listAllUsers(AccountUtilTestData.USERNAMES[0]).size() == 1);
	}

	private RootAccountManagerServiceImpl createRootService(
			DuracloudUserRepo userRepo,
			DuracloudAccountRepo accountRepo) {
		return new RootAccountManagerServiceImpl(userRepo, accountRepo);
	}



	
}
