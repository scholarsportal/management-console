/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.web;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.common.domain.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public abstract class AbstractAccessDecisionVoter implements AccessDecisionVoter<MethodInvocation> {

	private Logger log = LoggerFactory.getLogger(AbstractAccessDecisionVoter.class);

	public AbstractAccessDecisionVoter() {
		super();
	}

	@Override
	public boolean supports(ConfigAttribute attribute) {
		log.trace("supports attribute{}", attribute.getAttribute());
		return true;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		log.trace("supports {}", clazz.getName());
		return MethodInvocation.class.isAssignableFrom(clazz);
	}
	
	@Override
    public int vote(Authentication authentication,
                    MethodInvocation invocation,
                    Collection<ConfigAttribute> attributes) {
        log.trace("voting on {} for {} using attributes {}", new Object[] {
            authentication, invocation, attributes });
        for (ConfigAttribute ca : attributes) {
            log.trace("attribute: {}", ca.getAttribute());
        }

        if (isRoot(authentication)) {
            return ACCESS_GRANTED;
        }

        return voteImpl(authentication, invocation, attributes);
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

    protected String decisionToString(int decision) {
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