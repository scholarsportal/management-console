/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.error.SubdomainAlreadyExistsException;
import org.duracloud.storage.domain.StorageProviderType;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/**
 * 
 * @author "Daniel Bernstein (dbernstein@duracloud.org)"
 *
 */
public class AccountManagerServiceImplTest {
	
	private AccountManagerService ams;
	@Before
	public void before(){
		ams = new AccountManagerServiceImpl(
				AccountUtilTestData.createMockUserRepo(),
				AccountUtilTestData.createMockAccountRepo(),
                AccountUtilTestData.createMockRightsRepo());
	}

	@Test
	public void testCreateAccount() {

		String subdomain = "testdomain";
        int userId = 0;
		DuracloudUser user =
				new DuracloudUser(userId,
					"testuser",
					"password", 
					"Primo",
					"Ultimo", 
					"primo@ultimo.org");

        int acctId = 0;
        int paymentId = 0;
		AccountInfo info = new AccountInfo(acctId,
                                           subdomain,
                                           "primo's account",
                                           "primo site",
                                           "Dept. Big Data",
                                           paymentId,
                                           new HashSet<Integer>(),
                                           new HashSet<StorageProviderType>(
                                               Arrays.asList(StorageProviderType.values())));
		
		try {
			DuracloudAccountRepo accountRepo = EasyMock.createNiceMock(DuracloudAccountRepo.class);
			DuracloudUserRepo userRepo = EasyMock.createNiceMock(DuracloudUserRepo.class);
			EasyMock.expect(userRepo.findById(userId)).andReturn(user).anyTimes();
			EasyMock.expect(accountRepo.getIds()).andReturn(new HashSet<Integer>()).times(2);
			EasyMock.expect(accountRepo.getIds()).andReturn(new HashSet<Integer>(Arrays.asList(new Integer[]{userId})));

			EasyMock.expect(accountRepo.findById(userId)).andReturn(info);
			EasyMock.replay(accountRepo, userRepo);
			ams = new AccountManagerServiceImpl(userRepo,accountRepo);
		} catch (DBNotFoundException e1) {
			e1.printStackTrace();
		}
		

		try {
			AccountService as = this.ams.createAccount(info);
			Assert.assertNotNull(as);
			AccountInfo ai = as.retrieveAccountInfo();
			Assert.assertNotNull(ai);
			Assert.assertNotNull(ai.getId());
			Assert.assertEquals(subdomain, ai.getSubdomain());
		} catch (SubdomainAlreadyExistsException e) {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void testLookupAccountsByUsername() {
		List<AccountInfo> infos = 
			this.ams.lookupAccountsByUsername(
					AccountUtilTestData.USERNAMES[0]);
		Assert.assertNotNull(infos);
		Assert.assertTrue(infos.size() > 0);
	}

	@Test
	public void testGetAccount() {
		try {
			AccountService service = this.ams.getAccount(AccountUtilTestData.ACCOUNT_IDS[0]);
			Assert.assertEquals(
					AccountUtilTestData.ACCOUNT_IDS[0], 
					service.retrieveAccountInfo().getId());
		} catch (AccountNotFoundException e) {
			Assert.assertTrue(false);
		}

		try {
			this.ams.getAccount(AccountUtilTestData.NOT_AN_ACCOUNT_ID);
			Assert.assertTrue(false);
		} catch (AccountNotFoundException e) {
			Assert.assertTrue(true);
		}

	}

	@Test
	public void testCheckSubdomain() throws AccountNotFoundException {
		Assert.assertTrue(this.ams.checkSubdomain("randomsubdomain"));
		Assert.assertFalse(
				this.ams.checkSubdomain(
						this.ams.getAccount(AccountUtilTestData.ACCOUNT_IDS[0])
													.retrieveAccountInfo()
													.getSubdomain()));

	}

}
