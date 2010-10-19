/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */

package org.duracloud.account.security.web;

import java.util.Collection;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

/**
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class AccountAccessDecisionVoter implements AccessDecisionVoter {
	private Logger log = LoggerFactory.getLogger(getClass());
	@Override
	public boolean supports(ConfigAttribute attribute) {
		log.debug("supports attribute{}", attribute.getAttribute());
		
		return true;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		log.debug("supports {}", clazz.getName());
		return MethodInvocation.class.isAssignableFrom(clazz);
	}

	@Override
	public int vote(Authentication authentication, Object object,
			Collection<ConfigAttribute> attributes) {
		log.debug("voting on {} for {} using attributes {}", new Object[] {
				authentication, object, attributes });
		for(ConfigAttribute ca : attributes){
			log.debug("attribute: {}", ca.getAttribute());
		}
		
		MethodInvocation rmi = (MethodInvocation)object;
		if(rmi.getMethod().getName().equals("getAccount")){
			String accountId = (String)rmi.getArguments()[0];
			log.debug("intercepted getAccount({})", accountId);
			DuracloudUser user = (DuracloudUser)authentication.getPrincipal();
			if(user.getRolesByAcct(accountId).contains(Role.ROLE_USER.name())){
				return ACCESS_GRANTED;
			}else{
				return ACCESS_DENIED;
			}
		}
		
		return ACCESS_ABSTAIN;
	}

}
