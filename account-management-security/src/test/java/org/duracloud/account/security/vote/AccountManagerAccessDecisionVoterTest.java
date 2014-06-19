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
import org.duracloud.account.db.util.error.DBNotFoundException;
import org.duracloud.account.db.util.impl.AccountManagerServiceImpl;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: 4/1/11
 */
public class AccountManagerAccessDecisionVoterTest {

    private AccountManagerAccessDecisionVoter voter;

    private DuracloudRepoMgr repoMgr;

    private Authentication authentication;
    private MethodInvocation invocation;
    private Collection<ConfigAttribute> securityConfig;

    private Role accessRole = Role.ROLE_USER;
    private Role badRole = Role.ROLE_ADMIN;

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
    public void testVoteScopeAny() throws Exception {
        Role userRole = Role.ROLE_USER;
        int expectedDecision = AccessDecisionVoter.ACCESS_GRANTED;
        doTestScopeAny(userRole, expectedDecision);
    }

    @Test
    public void testVoteScopeAnyFail() {
        Role userRole = Role.ROLE_ADMIN;
        int expectedDecision = AccessDecisionVoter.ACCESS_DENIED;
        doTestScopeAny(userRole, expectedDecision);
    }

    private void doTestScopeAny(Role userRole, int expectedDecision) {
        Long userId = 5L;
        authentication = createAuthentication(userId, userRole);
        invocation = createInvocation(null);
        securityConfig = createSecurityConfig(SecuredRule.Scope.ANY);

        doTest(expectedDecision);
    }

    private void doTest(int expectedDecision) {
        replayMocks();
        voter = new AccountManagerAccessDecisionVoter(repoMgr);

        int decision = voter.vote(authentication, invocation, securityConfig);
        Assert.assertEquals(expectedDecision, decision);
    }

    @Test
    public void testScopeSelfAcct() throws DBNotFoundException {
        Role userRole = Role.ROLE_USER;
        int expectedDecision = AccessDecisionVoter.ACCESS_GRANTED;
        doTestScopeSelfAcct(userRole, expectedDecision);
    }

    @Test
    public void testScopeSelfAcctFail() throws DBNotFoundException {
        Role userRole = Role.ROLE_ADMIN;
        int expectedDecision = AccessDecisionVoter.ACCESS_DENIED;
        doTestScopeSelfAcct(userRole, expectedDecision);
    }

    private void doTestScopeSelfAcct(Role userRole, int expectedDecision)
        throws DBNotFoundException {
        Long userId = 5L;
        Long acctId = 9L;
        authentication = createAuthentication(userId, userRole);
        invocation = createInvocation(acctId);
        securityConfig = createSecurityConfig(SecuredRule.Scope.SELF_ACCT);
        repoMgr = createRepoMgr(createRights(userRole));

        doTest(expectedDecision);
    }

    @Test
    public void testScopeSelf() throws DBNotFoundException {
        Role userRole = accessRole;
        int expectedDecision = AccessDecisionVoter.ACCESS_GRANTED;
        doTestScopeSelf(userRole, expectedDecision);
    }

    @Test
    public void testScopeSelfFailRole() throws DBNotFoundException {
        Role userRole = badRole;
        int expectedDecision = AccessDecisionVoter.ACCESS_DENIED;
        doTestScopeSelf(userRole, expectedDecision);
    }

    @Test
    public void testScopeSelfFailId() throws DBNotFoundException {
        Role userRole = accessRole;
        int expectedDecision = AccessDecisionVoter.ACCESS_DENIED;
        int targetUserId = 99;
        doTestScopeSelf(userRole, expectedDecision, targetUserId);
    }

    private void doTestScopeSelf(Role userRole, int expectedDecision) {
        doTestScopeSelf(userRole, expectedDecision, -1);
    }

    private void doTestScopeSelf(Role userRole,
                                 int expectedDecision,
                                 int targetId) {
        Long userId = 5L;
        Long targetUserId = targetId > -1 ? targetId : userId;
        authentication = createAuthentication(userId, userRole);
        invocation = createInvocation(userRole.equals(accessRole) ? targetUserId : null);
        securityConfig = createSecurityConfig(SecuredRule.Scope.SELF_ID);

        doTest(expectedDecision);
    }

    /**
     * Mocks created below.
     */

    private AccountRights createRights(Role role) {
        Set<Role> roles = new HashSet<Role>();
        roles.add(role);
        AccountRights accountRights = new AccountRights();
        accountRights.setRoles(roles);
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

    private Authentication createAuthentication(Long userId, Role role) {
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

        Collection<GrantedAuthority> userRoles = new HashSet<GrantedAuthority>();
        userRoles.add(new SimpleGrantedAuthority(role.name()));
        EasyMock.expect(auth.getAuthorities()).andReturn((Collection)userRoles);

        return auth;
    }

    private MethodInvocation createInvocation(Long id) {
        MethodInvocation inv = EasyMock.createMock("MethodInvocation",
                                                   MethodInvocation.class);

        EasyMock.expect(inv.getMethod()).andReturn(this.getClass()
                                                       .getMethods()[0]);

        EasyMock.expect(inv.getArguments()).andReturn(new Object[0]);

        
        AccountManagerServiceImpl serviceImpl = new AccountManagerServiceImpl(
            null,
            null,
            null,
            null,
            null,
            null);

        EasyMock.expect(inv.getThis()).andReturn(serviceImpl).times(2);

        if (null != id) {
            EasyMock.expect(inv.getArguments()).andReturn(new Object[]{id});
        }
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
