/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.web;

import org.duracloud.account.util.DuracloudUserService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.access.AccessDecisionVoter;

/**
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class UserAccessDecisionVoterTest extends AccessDecisionVoterTestBase{

	private UserAccessDecisionVoter adv = new UserAccessDecisionVoter();

	private DuracloudUserService userService;
	
	public UserAccessDecisionVoterTest(){
		 userService = EasyMock.createMock(DuracloudUserService.class);
		 EasyMock.replay(userService);
	}
	
	@Test
	public void testAbstain() throws Exception {
		Assert.assertEquals(AccessDecisionVoter.ACCESS_ABSTAIN, adv.vote(
				createUserAuthentication(USER_AUTHORITIES),
				createMockMethodInvoker(userService.getClass().getMethod("toString"), 
										new Object[]{"test"}), 
				attributes));
	}

	@Test
	public void testDeny() throws Exception {
		Assert.assertEquals(AccessDecisionVoter.ACCESS_DENIED, adv.vote(
				createUserAuthentication(USER_AUTHORITIES),
				createMockMethodInvoker(userService.getClass().getMethod("loadDuracloudUserByUsername", String.class), 
										new Object[]{"other"}), 
				attributes));
	}

	@Test
	public void testRootAccess() throws Exception {
		Assert.assertEquals(AccessDecisionVoter.ACCESS_GRANTED, adv.vote(
				createRootAuthentication(),
				createMockMethodInvoker(userService.getClass().getMethod("loadDuracloudUserByUsername", String.class), 
										new Object[]{"other"}), 
				attributes));
	}

	@Test
	public void testGranted() throws Exception {
		Assert.assertEquals(AccessDecisionVoter.ACCESS_GRANTED, adv.vote(
				createUserAuthentication(USER_AUTHORITIES),
				createMockMethodInvoker(userService.getClass().getMethod("loadDuracloudUserByUsername", String.class), 
										new Object[]{"test"}), 
				attributes));
	}
}
