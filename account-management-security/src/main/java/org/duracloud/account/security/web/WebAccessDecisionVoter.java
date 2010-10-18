/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */

package org.duracloud.account.security.web;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

/**
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class WebAccessDecisionVoter implements AccessDecisionVoter {
	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public boolean supports(ConfigAttribute attribute) {
		log.debug("supports attribute{}", attribute.getAttribute());
		
		return true;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		log.debug("supports {}", clazz.getName());
		return true;
	}

	@Override
	public int vote(Authentication authentication, Object object,
			Collection<ConfigAttribute> attributes) {
		log.debug("voting on {} for {} using attributes {}", new Object[] {
				authentication, object, attributes });
		for(ConfigAttribute ca : attributes){
			log.debug("attribute: {}", ca.getAttribute());
		}
		return AccessDecisionVoter.ACCESS_DENIED;
	}

}
