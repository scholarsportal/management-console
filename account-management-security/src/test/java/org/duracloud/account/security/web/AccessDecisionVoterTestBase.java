/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.web;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.easymock.EasyMock;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class AccessDecisionVoterTestBase {

    protected static final String TEST_USERNAME = "test";
    protected Collection<ConfigAttribute> attributes =
        new LinkedList<ConfigAttribute>();
    protected static Set<Role> USER_AUTHORITIES;
    protected static Set<Role> ADMIN_AUTHORITIES;
    protected static Set<Role> OWNER_AUTHORITIES;
    protected static Set<Role> ROOT_AUTHORITIES;

    static {
        USER_AUTHORITIES = new HashSet<Role>();
        USER_AUTHORITIES.add(Role.ROLE_USER);

        ADMIN_AUTHORITIES = new HashSet<Role>();
        ADMIN_AUTHORITIES.add(Role.ROLE_ADMIN);
        ADMIN_AUTHORITIES.addAll(USER_AUTHORITIES);

        OWNER_AUTHORITIES = new HashSet<Role>();
        OWNER_AUTHORITIES.add(Role.ROLE_OWNER);
        OWNER_AUTHORITIES.addAll(ADMIN_AUTHORITIES);

        ROOT_AUTHORITIES = new HashSet<Role>();
        ROOT_AUTHORITIES.add(Role.ROLE_ROOT);
        ROOT_AUTHORITIES.addAll(OWNER_AUTHORITIES);

    }

    protected Authentication createUserAuthentication(Set<Role> authorities) {
        return createMockAuthentication(createUser(authorities, TEST_USERNAME, 0,0));

    }
    
    protected DuracloudUser createUser(Set<Role> authorities, String username, int accountId, int userId) {
        DuracloudUser user =
            new DuracloudUser(userId,
                username,
                "test",
                "test",
                "test",
                "test@test",
                "test",
                "test");
        Set<AccountRights> rights = new HashSet<AccountRights>();
        rights.add(new AccountRights(0, accountId, userId, authorities));
        user.setAccountRights(rights);
        return user;
    }

    protected Authentication createRootAuthentication() {
        UserDetails r = EasyMock.createMock(UserDetails.class);
        EasyMock.expect(r.getAuthorities())
            .andReturn((Collection<GrantedAuthority>) Arrays.asList(new GrantedAuthority[] { Role.ROLE_ROOT.authority() }));
        EasyMock.replay(r);
        return createMockAuthentication(r);
    }

    private Authentication createMockAuthentication(UserDetails user) {
        Authentication auth = EasyMock.createMock(Authentication.class);
        EasyMock.expect(auth.isAuthenticated()).andReturn(true);
        EasyMock.expect(auth.getPrincipal()).andReturn(user).anyTimes();
        EasyMock.replay(auth);
        return auth;
    }

    protected MethodInvocation createMockMethodInvoker(
        Method method, Object[] arguments) throws Exception {
        MethodInvocation mi = EasyMock.createMock(MethodInvocation.class);
        EasyMock.expect(mi.getArguments()).andReturn(arguments).anyTimes();
        EasyMock.expect(mi.getMethod()).andReturn(method).anyTimes();
        EasyMock.replay(mi);
        return mi;
    }



}