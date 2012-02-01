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
import org.duracloud.account.security.domain.SecuredRule;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.impl.DuracloudInstanceServiceSecuredImpl;
import org.duracloud.account.util.security.AnnotationParser;
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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: 4/11/11
 */
public class InstanceAccessDecisionVoterTest {

    private InstanceAccessDecisionVoter voter;

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
    public void testVoteScopeAny() throws Exception {
        Role userRole = Role.ROLE_ADMIN;
        int expectedDecision = AccessDecisionVoter.ACCESS_GRANTED;
        doTestScopeAny(userRole, expectedDecision);
    }

    @Test
    public void testVoteScopeAnyFail() {
        Role userRole = Role.ROLE_USER;
        int expectedDecision = AccessDecisionVoter.ACCESS_DENIED;
        doTestScopeAny(userRole, expectedDecision);
    }

    private void doTestScopeAny(Role userRole, int expectedDecision) {
        int userId = 5;
        int acctId = -1;
        SecuredRule.Scope scope = SecuredRule.Scope.ANY;

        authentication = createAuthentication(userId, userRole, scope);
        securityConfig = createSecurityConfig(scope);
        invocation = createInvocation(acctId, scope);

        doTest(expectedDecision);
    }

    private void doTest(int expectedDecision) {
        replayMocks();
        voter = new InstanceAccessDecisionVoter(repoMgr);

        int decision = voter.vote(authentication, invocation, securityConfig);
        Assert.assertEquals(expectedDecision, decision);
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
        int userId = 5;
        int acctId = 9;
        SecuredRule.Scope scope = SecuredRule.Scope.SELF_ACCT;
        AccountRights userRight = createRights(userRole);
        Set<AccountRights> rights = new HashSet<AccountRights>();
        rights.add(userRight);

        authentication = createAuthentication(userId, userRole, scope);
        securityConfig = createSecurityConfig(scope);
        invocation = createInvocation(acctId, scope);
        repoMgr = createRepoMgr(userRight, rights, scope);

        doTest(expectedDecision);
    }

    @Test
    public void testScopeSelfAcctPeerUpdate() throws DBNotFoundException {
        Role otherUserRole = Role.ROLE_USER;
        int expectedDecision = AccessDecisionVoter.ACCESS_GRANTED;
        doTestScopeSelfAcctPeerUpdate(otherUserRole, expectedDecision);
    }

    @Test
    public void testScopeSelfAcctPeerUpdateFail() throws DBNotFoundException {
        Role otherUserRole = Role.ROLE_USER;
        int otherAcctId = 6;
        int expectedDecision = AccessDecisionVoter.ACCESS_DENIED;
        doTestScopeSelfAcctPeerUpdate(otherUserRole,
                                      null,
                                      otherAcctId,
                                      expectedDecision);
    }

    @Test
    public void testScopeSelfAcctPeerUpdateFailRole()
        throws DBNotFoundException {
        Role otherUserRole = Role.ROLE_OWNER;
        int expectedDecision = AccessDecisionVoter.ACCESS_DENIED;
        doTestScopeSelfAcctPeerUpdate(otherUserRole, expectedDecision);
    }

    @Test
    public void testScopeSelfAcctPeerUpdateFailNewRole()
        throws DBNotFoundException {
        Role otherUserRole = Role.ROLE_ADMIN;
        Set<Role> argRoles = Role.ROLE_OWNER.getRoleHierarchy();
        int expectedDecision = AccessDecisionVoter.ACCESS_DENIED;
        doTestScopeSelfAcctPeerUpdate(otherUserRole,
                                      argRoles,
                                      -1,
                                      expectedDecision);
    }

    private void doTestScopeSelfAcctPeerUpdate(Role otherUserRole,
                                               int expectedDecision)
        throws DBNotFoundException {
        doTestScopeSelfAcctPeerUpdate(otherUserRole,
                                      null,
                                      -1,
                                      expectedDecision);
    }

    private void doTestScopeSelfAcctPeerUpdate(Role otherUserRole,
                                               Set<Role> argRoles,
                                               int argAcctId,
                                               int expectedDecision)
        throws DBNotFoundException {
        Role userRole = Role.ROLE_ADMIN;

        int userId = 3;
        int acctId = 5;
        int otherUserId = 6;
        int otherAcctId = argAcctId >= 0 ? argAcctId : acctId;
        SecuredRule.Scope scope = SecuredRule.Scope.SELF_ACCT_PEER_UPDATE;
        Set<Role> newArgRoles =
            null == argRoles ? otherUserRole.getRoleHierarchy() : argRoles;

        AccountRights userRight = new AccountRights(-1,
                                                    acctId,
                                                    userId,
                                                    userRole.getRoleHierarchy());
        AccountRights otherRight = new AccountRights(-1,
                                                     otherAcctId,
                                                     otherUserId,
                                                     newArgRoles);


        Set<AccountRights> rightsList = new HashSet<AccountRights>();
        rightsList.add(userRight);
        rightsList.add(otherRight);

        authentication = createAuthentication(userId, userRole, scope);
        securityConfig = createSecurityConfig(scope);
        invocation = createInvocation(acctId, rightsList, scope);
        repoMgr = createRepoMgr(userRight, rightsList, scope);

        doTest(expectedDecision);
    }


    /**
     * Mocks created below.
     */

    private AccountRights createRights(Role role) {
        return new AccountRights(-1, -1, -1, role.getRoleHierarchy());
    }

    private DuracloudRepoMgr createRepoMgr(AccountRights userRight,
                                           Set<AccountRights> rights,
                                           SecuredRule.Scope scope)
        throws DBNotFoundException {
        DuracloudRepoMgr mgr = EasyMock.createMock("DuracloudRepoMgr",
                                                   DuracloudRepoMgr.class);
        DuracloudRightsRepo rightsRepo = EasyMock.createMock(
            "DuracloudRightsRepo",
            DuracloudRightsRepo.class);


        EasyMock.expect(rightsRepo.findByAccountId(EasyMock.anyInt()))
            .andReturn(rights);

        int xFind = scope.equals(SecuredRule.Scope.SELF_ACCT_PEER_UPDATE) ? 2 : 1;
        EasyMock.expect(rightsRepo.findAccountRightsForUser(EasyMock.anyInt(),
                                                            EasyMock.anyInt()))
            .andReturn(userRight)
            .times(xFind);

        int xGetRights = scope.equals(SecuredRule.Scope.SELF_ACCT_PEER_UPDATE) ? 3 : 1;
        EasyMock.expect(mgr.getRightsRepo()).andReturn(rightsRepo).times(
            xGetRights);

        EasyMock.replay(rightsRepo);
        return mgr;
    }

    private Authentication createAuthentication(int userId,
                                                Role role,
                                                SecuredRule.Scope scope) {
        Authentication auth = EasyMock.createMock("Authentication",
                                                  Authentication.class);
        DuracloudUser user = new DuracloudUser(userId,
                                               "username",
                                               "password",
                                               "firstName",
                                               "lastName",
                                               "email",
                                               "question",
                                               "answer");
        EasyMock.expect(auth.getPrincipal()).andReturn(user);

        if (scope.equals(SecuredRule.Scope.ANY)) {
            Set<GrantedAuthority> grants = new HashSet<GrantedAuthority>();
            for (Role r : role.getRoleHierarchy()) {
                grants.add(r.authority());
            }
            EasyMock.expect(auth.getAuthorities()).andReturn((Collection)grants);
        }

        return auth;
    }

    private MethodInvocation createInvocation(int acctId,
                                              SecuredRule.Scope scope) {
        return createInvocation(acctId, null, scope);
    }

    private MethodInvocation createInvocation(int acctId,
                                              Set<AccountRights> rights,
                                              SecuredRule.Scope scope) {
        MethodInvocation inv = EasyMock.createMock("MethodInvocation",
                                                   MethodInvocation.class);

        // set up instanceService
        DuracloudInstanceService instanceService = EasyMock.createMock(
            "DuracloudInstanceService",
            DuracloudInstanceService.class);
        EasyMock.expect(instanceService.getAccountId()).andReturn(acctId);

        EasyMock.replay(instanceService);

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
            "AnnotationParser",
            AnnotationParser.class);
        EasyMock.expect(annotationParser.getMethodAnnotationsForClass(EasyMock.isA(
            Class.class), EasyMock.isA(Class.class))).andReturn(methodMap);
        EasyMock.replay(annotationParser);

        // set up recursive voter
        AccessDecisionVoter subVoter = EasyMock.createMock("AccessDecisionVoter",
                                                           AccessDecisionVoter.class);
        EasyMock.expect(subVoter.vote(EasyMock.<Authentication>anyObject(),
                                      EasyMock.<Object>anyObject(),
                                      EasyMock.<Collection<ConfigAttribute>>anyObject()))
            .andReturn(AccessDecisionVoter.ACCESS_GRANTED);
        EasyMock.replay(subVoter);
        DuracloudInstanceServiceSecuredImpl serviceImpl = new DuracloudInstanceServiceSecuredImpl(
            instanceService,
            null,
            subVoter,
            annotationParser);

        int times = scope.equals(SecuredRule.Scope.ANY) ? 2 : 3;
        EasyMock.expect(inv.getThis()).andReturn(serviceImpl).times(times);

        Object arg;
        if (scope.equals(SecuredRule.Scope.SELF_ACCT_PEER_UPDATE)) {
            Set<DuracloudUser> users = new HashSet<DuracloudUser>();
            for (AccountRights r : rights) {
                users.add(createUser(r));
            }
            arg = users;

        } else {
            arg = acctId;
        }

        EasyMock.expect(inv.getArguments()).andReturn(new Object[]{arg});

        return inv;
    }

    private DuracloudUser createUser(AccountRights right) {
        DuracloudUser user = new DuracloudUser(right.getUserId(),
                                               "username" + right.getUserId(),
                                               "password",
                                               "first-name",
                                               "last-name",
                                               "email",
                                               "question",
                                               "answer");
        user.setAccountRights(right);
        return user;
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

