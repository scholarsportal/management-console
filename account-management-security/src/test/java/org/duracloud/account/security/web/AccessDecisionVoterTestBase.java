/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.web;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.easymock.EasyMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
/**
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class AccessDecisionVoterTestBase {

	protected Logger log = LoggerFactory.getLogger(getClass());
	protected Collection<ConfigAttribute> attributes = new LinkedList<ConfigAttribute>();
	protected static Set<Role> USER_AUTHORITIES;

	static {
		USER_AUTHORITIES = new HashSet<Role>();
		USER_AUTHORITIES.add(Role.ROLE_USER);		
	}
	
	public AccessDecisionVoterTestBase() {
		super();
	}

	protected Authentication createUserAuthentication(Set<Role> authorities) {
		DuracloudUser user = new DuracloudUser(0, "test", "test", "test","test", "test@test");
        Set<AccountRights> rights = new HashSet<AccountRights>();
        rights.add(new AccountRights(0, 0, 0, authorities));
		user.setAccountRights(rights);

		return createMockAuthentication(user);
	}

	protected Authentication createRootAuthentication() {
		UserDetails r = EasyMock.createMock(UserDetails.class);
		EasyMock.expect(r.getAuthorities()).andReturn((Collection<GrantedAuthority>)Arrays.asList(new GrantedAuthority[]{Role.ROLE_ROOT.authority()}));
		EasyMock.replay(r);
		return createMockAuthentication(r);
	}

	private Authentication createMockAuthentication(UserDetails user) {
		Authentication auth = EasyMock.createMock(Authentication.class);
		EasyMock.expect(auth.getPrincipal()).andReturn(user).anyTimes();
		EasyMock.replay(auth);
		return auth;
	}

	protected MethodInvocation createMockMethodInvoker(Method method, Object[] arguments)
			throws Exception {
				MethodInvocation mi = EasyMock.createMock(MethodInvocation.class);
				EasyMock.expect(mi.getArguments()).andReturn(arguments);
				EasyMock.expect(mi.getMethod()).andReturn(method);
				EasyMock.replay(mi);
				return mi;
			}
	

}