/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.web;

import java.util.Collection;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.common.domain.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public abstract class AbstractAccessDecisionVoter implements AccessDecisionVoter {

	protected Logger log = LoggerFactory.getLogger(getClass());

	public AbstractAccessDecisionVoter() {
		super();
	}

	public boolean supports(ConfigAttribute attribute) {
		log.debug("supports attribute{}", attribute.getAttribute());
		return true;
	}

	public boolean supports(Class<?> clazz) {
		log.debug("supports {}", clazz.getName());
		return MethodInvocation.class.isAssignableFrom(clazz);
	}
	
	public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
		log.debug("voting on {} for {} using attributes {}", new Object[] {
				authentication, object, attributes });
		for(ConfigAttribute ca : attributes){
			log.debug("attribute: {}", ca.getAttribute());
		}

		if(isRoot(authentication)){
			return ACCESS_GRANTED;
		}
		
		MethodInvocation rmi = (MethodInvocation)object;
		return voteImpl(authentication, rmi, attributes);
	}

	protected abstract int voteImpl(Authentication authentication, MethodInvocation rmi,
			Collection<ConfigAttribute> attributes);

	protected boolean isRoot(Authentication authentication) {
		if(!(authentication.getPrincipal() instanceof UserDetails)){
			return false;
		}

		return ((UserDetails)authentication.getPrincipal())
					.getAuthorities()
					.contains(Role.ROLE_ROOT.authority());
	}

}