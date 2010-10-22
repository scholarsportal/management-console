/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */

package org.duracloud.account.security.web;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.easymock.EasyMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	protected Logger log = LoggerFactory.getLogger(getClass());
	protected Collection<ConfigAttribute> attributes = new LinkedList<ConfigAttribute>();
	protected static List<Role> USER_AUTHORITIES;

	static {
		USER_AUTHORITIES= new ArrayList<Role>();
		USER_AUTHORITIES.add(Role.ROLE_USER);
		
	}
	
	public AccessDecisionVoterTestBase() {
		super();
	}

	protected Authentication createUserAuthentication(List<Role> authorities) {
		DuracloudUser user = new DuracloudUser("test", "test", "test","test", "test@test");
		Map<String,List<String>> map = new HashMap<String,List<String>>();
		user.setAcctToRoles(map);
		List<String> list = new LinkedList<String>();
		for(Role a : authorities){
			list.add(a.name());
		}
		map.put("0", list);
		
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