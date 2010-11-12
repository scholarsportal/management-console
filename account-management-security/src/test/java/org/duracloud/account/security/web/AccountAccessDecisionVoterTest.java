/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.web;

import org.duracloud.account.util.AccountManagerService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.access.AccessDecisionVoter;

/**
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class AccountAccessDecisionVoterTest extends AccessDecisionVoterTestBase {
	private AbstractAccessDecisionVoter adv = new AccountAccessDecisionVoter();

	private AccountManagerService ams;
	
	public AccountAccessDecisionVoterTest(){
		 ams = EasyMock.createMock(AccountManagerService.class);
		 EasyMock.replay(ams);
	}
	
	@Test
	public void testAbstain() throws Exception {
		Assert.assertEquals(AccessDecisionVoter.ACCESS_ABSTAIN, adv.vote(
				createUserAuthentication(USER_AUTHORITIES),
				createMockMethodInvoker(ams.getClass().getMethod("toString"), 
										new Object[]{"0"}), 
				attributes));
	}

	@Test
	public void testDeny() throws Exception {
		Assert.assertEquals(AccessDecisionVoter.ACCESS_DENIED, adv.vote(
				createUserAuthentication(USER_AUTHORITIES),
				createMockMethodInvoker(ams.getClass().getMethod("getAccount", String.class), 
										new Object[]{"1"}), 
				attributes));
	}

	@Test
	public void testRootAccess() throws Exception {
		Assert.assertEquals(AccessDecisionVoter.ACCESS_GRANTED, adv.vote(
				createRootAuthentication(),
				createMockMethodInvoker(ams.getClass().getMethod("getAccount", String.class), 
										new Object[]{"1"}), 
				attributes));
	}

	@Test
	public void testGranted() throws Exception {
		Assert.assertEquals(AccessDecisionVoter.ACCESS_GRANTED, adv.vote(
				createUserAuthentication(USER_AUTHORITIES),
				createMockMethodInvoker(ams.getClass().getMethod("getAccount", String.class), 
										new Object[]{"0"}), 
				attributes));
	}

}
