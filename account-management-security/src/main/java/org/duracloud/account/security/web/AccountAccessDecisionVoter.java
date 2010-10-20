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
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

/**
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class AccountAccessDecisionVoter extends AbstractAccessDecisionVoter implements AccessDecisionVoter {
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
	protected int voteImpl(Authentication authentication, MethodInvocation rmi,
			Collection<ConfigAttribute> attributes) {
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
