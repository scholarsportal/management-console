/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.web;


import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import java.util.Collection;
import java.util.Set;

/**
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class AccountAccessDecisionVoter extends AbstractAccessDecisionVoter implements AccessDecisionVoter {

    private Logger log = LoggerFactory.getLogger(AccountAccessDecisionVoter.class);

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

        int decision = ACCESS_ABSTAIN;
		if(rmi.getMethod().getName().equals("getAccount")){
			Integer accountId = (Integer)rmi.getArguments()[0];

			log.debug("intercepted getAccount({})", accountId);

            DuracloudUser user = (DuracloudUser)authentication.getPrincipal();
            Set<Role> roles = user.getRolesByAcct(accountId);
            if(null != roles && roles.contains(Role.ROLE_USER)){
                decision = ACCESS_GRANTED;
            }else{
                decision = ACCESS_DENIED;
            }
		}
        log.debug("decision: " + decisionToString(decision));
        return decision;
    }

    private String decisionToString(int decision) {
        String text;
        switch (decision) {
            case 1:
                text = "ACCESS_GRANTED";
                break;
            case 0:
                text = "ACCESS_ABSTAIN";
                break;
            case -1:
                text = "ACCESS_DENIED";
                break;
            default:
                text = "UNRECOGNIZED_DECISION:" + decision;
        }
        return text;
    }
	
}