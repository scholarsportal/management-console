/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.web;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.db.util.AccountManagerService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDecisionVoter;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class AccountAccessDecisionVoterTest extends AccessDecisionVoterTestBase {

    private AbstractAccessDecisionVoter decisionVoter;
    private AccountManagerService accountManagerService;

    @Before
    public void setUp() {
        decisionVoter = new AccountAccessDecisionVoter();
        accountManagerService = EasyMock.createMock(AccountManagerService.class);
        EasyMock.replay(accountManagerService);
    }

    @Test
    public void testAbstain() throws Exception {
        MethodInvocation invocation = createMockMethodInvoker(
            accountManagerService.getClass().getMethod("toString"),
            new Object[]{"0"});

        int decision = decisionVoter.vote(createUserAuthentication(
            USER_AUTHORITIES), invocation, attributes);

        Assert.assertEquals(AccessDecisionVoter.ACCESS_ABSTAIN, decision);
    }

    @Test
    public void testDeny() throws Exception {
        MethodInvocation invocation = createMockMethodInvoker(
            AccountManagerService.class.getMethod("getAccount", Long.class),
            new Object[]{1L});

        int decision = decisionVoter.vote(createUserAuthentication(
            USER_AUTHORITIES), invocation, attributes);
        Assert.assertEquals(AccessDecisionVoter.ACCESS_DENIED, decision);
    }

    @Test
    public void testRootAccess() throws Exception {
        MethodInvocation invocation = createMockMethodInvoker(
            accountManagerService.getClass().getMethod("getAccount", Long.class),
            new Object[]{1L});

        int decision = decisionVoter.vote(createRootAuthentication(),
                                          invocation,
                                          attributes);
        Assert.assertEquals(AccessDecisionVoter.ACCESS_GRANTED, decision);
    }

    @Test
    public void testGranted() throws Exception {
        MethodInvocation invocation = createMockMethodInvoker(
            accountManagerService.getClass().getMethod("getAccount", Long.class),
            new Object[]{0L});

        int decision = decisionVoter.vote(createUserAuthentication(
            USER_AUTHORITIES), invocation, attributes);
        Assert.assertEquals(AccessDecisionVoter.ACCESS_GRANTED, decision);
    }

}
