/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.vote;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.error.DBNotFoundException;
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
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import java.lang.reflect.Method;
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
        int userId = 5;
        authentication = createAuthentication(userId, userRole);
        invocation = createInvocation(null);
        securityConfig = createSecurityConfig("any");

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
        int userId = 5;
        int acctId = 9;
        authentication = createAuthentication(userId, userRole);
        invocation = createInvocation(acctId);
        securityConfig = createSecurityConfig("self-acct");
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
        int userId = 5;
        int targetUserId = targetId > -1 ? targetId : userId;
        authentication = createAuthentication(userId, userRole);
        invocation = createInvocation(userRole.equals(accessRole) ? targetUserId : null);
        securityConfig = createSecurityConfig("self");

        doTest(expectedDecision);
    }

    /**
     * Mocks created below.
     */

    private AccountRights createRights(Role role) {
        Set<Role> roles = new HashSet<Role>();
        roles.add(role);
        return new AccountRights(-1, -1, -1, roles);
    }

    private DuracloudRepoMgr createRepoMgr(AccountRights rights)
        throws DBNotFoundException {
        DuracloudRepoMgr mgr = EasyMock.createMock("DuracloudRepoMgr",
                                                   DuracloudRepoMgr.class);
        DuracloudRightsRepo rightsRepo = EasyMock.createMock(
            "DuracloudRightsRepo",
            DuracloudRightsRepo.class);

        EasyMock.expect(rightsRepo.findByAccountIdAndUserId(EasyMock.anyInt(),
                                                            EasyMock.anyInt()))
            .andReturn(rights);

        EasyMock.expect(mgr.getRightsRepo()).andReturn(rightsRepo);

        EasyMock.replay(rightsRepo);
        return mgr;
    }

    private Authentication createAuthentication(int userId, Role role) {
        Authentication auth = EasyMock.createMock("Authentication",
                                                  Authentication.class);
        DuracloudUser user = new DuracloudUser(userId,
                                               "username",
                                               "password",
                                               "firstName",
                                               "lastName",
                                               "email");
        EasyMock.expect(auth.getPrincipal()).andReturn(user);

        Set<GrantedAuthority> userRoles = new HashSet<GrantedAuthority>();
        userRoles.add(new GrantedAuthorityImpl(role.name()));
        EasyMock.expect(auth.getAuthorities()).andReturn(userRoles);

        return auth;
    }

    private MethodInvocation createInvocation(Integer id) {
        MethodInvocation inv = EasyMock.createMock("MethodInvocation",
                                                   MethodInvocation.class);

        EasyMock.expect(inv.getMethod()).andReturn(this.getClass()
                                                       .getMethods()[0]);

        if (null != id) {
            EasyMock.expect(inv.getArguments()).andReturn(new Object[]{id});
        }
        return inv;
    }

    private Collection<ConfigAttribute> createSecurityConfig(String scope) {
        Collection<ConfigAttribute> attributes = new HashSet<ConfigAttribute>();

        ConfigAttribute att = new SecurityConfig(
            "role:" + accessRole.name() + ",scope:" + scope);
        attributes.add(att);

        return attributes;
    }

    private void replayMocks() {
        EasyMock.replay(authentication, repoMgr, invocation);
    }

}
