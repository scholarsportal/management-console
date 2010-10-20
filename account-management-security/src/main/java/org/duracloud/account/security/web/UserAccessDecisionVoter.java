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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

/**
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class UserAccessDecisionVoter extends AbstractAccessDecisionVoter {
	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	protected int voteImpl(Authentication authentication, MethodInvocation rmi,
			Collection<ConfigAttribute> attributes) {
		if(rmi.getMethod().getName().matches(".*UserByUsername.*")){
			String username = (String)rmi.getArguments()[0];
			log.debug("intercepted ({})", username);
			DuracloudUser user = (DuracloudUser)authentication.getPrincipal();
			
			if(username.equals(user.getUsername())){
				return ACCESS_GRANTED;
			}else{
				return ACCESS_DENIED;
			}
		}
		
		return ACCESS_ABSTAIN;
	}

}
