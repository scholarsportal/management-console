/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.vote;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.db.model.AccountRights;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.model.Role;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.repo.DuracloudRightsRepo;
import org.duracloud.account.db.util.AccountService;
import org.duracloud.account.db.util.error.DBNotFoundException;
import org.duracloud.account.db.util.impl.AccountServiceSecuredImpl;
import org.duracloud.account.db.util.security.AnnotationParser;
import org.duracloud.account.security.domain.SecuredRule;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Andrew Woods
 *         Date: 4/9/11
 */
public class AccountAccessDecisionVoterTest {

    private AccountAccessDecisionVoter voter;

    private DuracloudRepoMgr repoMgr;

    private Authentication authentication;
    private MethodInvocation invocation;
    private Collection<ConfigAttribute> securityConfig;

    private final Role accessRole = Role.ROLE_ADMIN;

    @Before
    public void setUp() throws Exception {
        repoMgr = EasyMock.createMock("DuracloudRepoMgr",
                                      DuracloudRepoMgr.class);
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(authentication, repoMgr, invocation);
    }

    @Test
    public void testScopeSelfAcct() throws DBNotFoundException {
        Role userRole = Role.ROLE_ADMIN;
        int expectedDecision = AccessDecisionVoter.ACCESS_GRANTED;
        doTestScopeSelfAcct(userRole, expectedDecision);
    }

    @Test
    public void testScopeSelfAcctFail() throws DBNotFoundException {
        Role userRole = Role.ROLE_USER;
        int expectedDecision = AccessDecisionVoter.ACCESS_DENIED;
        doTestScopeSelfAcct(userRole, expectedDecision);
    }

    private void doTestScopeSelfAcct(Role userRole, int expectedDecision)
        throws DBNotFoundException {
        Long userId = 5L;
        Long acctId = 9L;
        authentication = createAuthentication(userId);
        securityConfig = createSecurityConfig(SecuredRule.Scope.SELF_ACCT);
        invocation = createInvocation(acctId);
        repoMgr = createRepoMgr(createRights(userRole));

        doTest(expectedDecision);
    }

    private void doTest(int expectedDecision) {
        replayMocks();
        voter = new AccountAccessDecisionVoter(repoMgr);

        int decision = voter.vote(authentication, invocation, securityConfig);
        Assert.assertEquals(expectedDecision, decision);
    }

    /**
     * Mocks created below.
     */

    private AccountRights createRights(Role role) {
        AccountRights accountRights = new AccountRights();
        accountRights.setRoles(role.getRoleHierarchy());
        return accountRights;
    }

    private DuracloudRepoMgr createRepoMgr(AccountRights rights)
        throws DBNotFoundException {
        DuracloudRepoMgr mgr = EasyMock.createMock("DuracloudRepoMgr",
                                                   DuracloudRepoMgr.class);
        DuracloudRightsRepo rightsRepo = EasyMock.createMock(
            "DuracloudRightsRepo",
            DuracloudRightsRepo.class);

        EasyMock.expect(rightsRepo.findByAccountIdAndUserId(EasyMock.anyLong(),
                                                            EasyMock.anyLong()))
            .andReturn(rights);

        EasyMock.expect(mgr.getRightsRepo()).andReturn(rightsRepo);

        EasyMock.replay(rightsRepo);
        return mgr;
    }

    private Authentication createAuthentication(Long userId) {
        Authentication auth = EasyMock.createMock("Authentication",
                                                  Authentication.class);
        DuracloudUser user = new DuracloudUser();
        user.setId(userId);
        user.setUsername("username");
        user.setPassword("password");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail("email");
        user.setSecurityQuestion("question");
        user.setSecurityAnswer("answer");
        EasyMock.expect(auth.getPrincipal()).andReturn(user);

        return auth;
    }

    private MethodInvocation createInvocation(Long id) {
        MethodInvocation inv = EasyMock.createMock("MethodInvocation",
                                                   MethodInvocation.class);

        EasyMock.expect(inv.getArguments()).andReturn(new Object[0]);
        // set up acctService
        AccountService acctService = EasyMock.createMock("AccountService",
                                                         AccountService.class);
        EasyMock.expect(acctService.getAccountId()).andReturn(id);

        EasyMock.replay(acctService);

        // set up annotation parser
        Method method = this.getClass().getMethods()[0];
        EasyMock.expect(inv.getMethod()).andReturn(method);

        Map<String, Object[]> methodMap = EasyMock.createMock("Map", Map.class);
        EasyMock.expect(methodMap.get(EasyMock.isA(String.class)))
            .andReturn(new Object[]{securityConfig.iterator()
                                        .next()
                                        .getAttribute()});
        EasyMock.replay(methodMap);

        AnnotationParser annotationParser = EasyMock.createMock(
            "AnnotationParser", AnnotationParser.class);
        EasyMock.expect(annotationParser.getMethodAnnotationsForClass(EasyMock.isA(
            Class.class), EasyMock.isA(Class.class))).andReturn(methodMap);
        EasyMock.replay(annotationParser);

        // set up recursive voter
        AccessDecisionVoter<MethodInvocation> subVoter = EasyMock.createMock("AccessDecisionVoter",
                                                        AccessDecisionVoter.class);
        EasyMock.expect(subVoter.vote(EasyMock.<Authentication>anyObject(),
                                   EasyMock.<MethodInvocation>anyObject(),
                                   EasyMock.<Collection<ConfigAttribute>>anyObject()))
            .andReturn(AccessDecisionVoter.ACCESS_GRANTED);
        EasyMock.replay(subVoter);
        AccountServiceSecuredImpl serviceImpl = new AccountServiceSecuredImpl(
            acctService,
            null,
            subVoter,
            annotationParser);

        EasyMock.expect(inv.getThis()).andReturn(serviceImpl).times(3);

        return inv;
    }

    private Collection<ConfigAttribute> createSecurityConfig(SecuredRule.Scope scope) {
        Collection<ConfigAttribute> attributes = new HashSet<ConfigAttribute>();

        ConfigAttribute att = new SecurityConfig(
            "role:" + accessRole.name() + ",scope:" + scope.name());
        attributes.add(att);

        return attributes;
    }

    private void replayMocks() {
        EasyMock.replay(authentication, repoMgr, invocation);
    }

}

