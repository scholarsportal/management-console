package org.duracloud.security.vote;

import org.duracloud.client.ContentStore;
import static org.duracloud.security.vote.VoterUtil.debugText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.intercept.web.FilterInvocation;
import org.springframework.security.providers.anonymous.AnonymousAuthenticationToken;
import org.springframework.security.vote.AccessDecisionVoter;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Andrew Woods
 *         Date: Mar 12, 2010
 */
public class SpaceAccessVoter implements AccessDecisionVoter {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private ContentStore contentStore;

    /**
     * This method always returns true because all configAttributes are able
     * to be handled by this voter.
     *
     * @param configAttribute any att
     * @return true
     */
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    /**
     * This methods returns true if the arg class is an instance of or
     * subclass of FilterInvocation.
     * No other classes can be handled by this voter.
     *
     * @param aClass to be analyized for an AuthZ vote.
     * @return true if is an instance of or subclass of FilterInvocation
     */
    public boolean supports(Class aClass) {
        return FilterInvocation.class.isAssignableFrom(aClass);
    }

    /**
     * This method checks the access state of the arg resource
     * (space and provider) and makes denies access to anonymous principals if
     * the space is closed.
     *
     * @param auth     principal seeking AuthZ
     * @param resource that is under protection
     * @param config   access-attributes defined on resource
     * @return vote (AccessDecisionVoter.ACCESS_GRANTED, ACCESS_DENIED, ACCESS_ABSTAIN)
     */
    public int vote(Authentication auth,
                    Object resource,
                    ConfigAttributeDefinition config) {
        if (resource != null && !supports(resource.getClass())) {
            return ACCESS_ABSTAIN;
        }

        HttpServletRequest httpRequest = getHttpServletRequest(resource);
        if (null == httpRequest) {
            String msg = debugText("null request",
                                   auth,
                                   config,
                                   "null",
                                   ACCESS_DENIED);
            log.warn("HttpServletRequest was null!  " + msg);
            return ACCESS_DENIED;
        }

        // If space is 'open' or 'closed' only matters if user is anonymous.
        if (!(auth instanceof AnonymousAuthenticationToken)) {
            return ACCESS_GRANTED;
        }

        String servletPath = httpRequest.getServletPath();
        String contextPath = httpRequest.getContextPath();

        // FIXME: check/cache space's access state
        int grant = ACCESS_GRANTED;
        log.debug(debugText("SpaceAccessVoter", auth, config, resource, grant));
        return grant;
    }

    private HttpServletRequest getHttpServletRequest(Object resource) {
        FilterInvocation invocation = (FilterInvocation) resource;
        return invocation.getHttpRequest();
    }

}
