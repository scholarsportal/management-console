/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.web;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.AccountRights;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.model.Role;
import org.easymock.EasyMock;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Method;
import java.util.*;

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
        return createMockAuthentication(createUser(authorities, TEST_USERNAME, 0L,0L));

    }
    
    protected DuracloudUser createUser(Set<Role> authorities, String username, Long accountId, Long userId) {
        DuracloudUser user = new DuracloudUser();
        user.setId(userId);
        user.setUsername(username);
        user.setPassword("test");
        user.setFirstName("test");
        user.setLastName("test");
        user.setEmail("test@test");
        user.setSecurityQuestion("test");
        user.setSecurityAnswer("test");
        Set<AccountRights> rights = new HashSet<AccountRights>();

        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setId(accountId);
        AccountRights accountRights = new AccountRights();
        accountRights.setId(0L);
        accountRights.setAccount(accountInfo);
        accountRights.setUser(user);
        accountRights.setRoles(authorities);
        rights.add(accountRights);
        user.setAccountRights(rights);

        return user;
    }

    protected Authentication createRootAuthentication() {
        UserDetails r = EasyMock.createMock(UserDetails.class);
        Collection<? extends GrantedAuthority> authorities =
            Arrays.asList(new GrantedAuthority[] { Role.ROLE_ROOT.authority() });

        EasyMock.expect(r.getAuthorities())
            .andReturn((Collection)authorities);
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