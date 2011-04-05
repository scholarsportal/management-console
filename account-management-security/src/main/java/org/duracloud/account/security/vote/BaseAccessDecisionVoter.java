/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.vote;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.security.domain.SecuredRule;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: 4/5/11
 */
public abstract class BaseAccessDecisionVoter implements AccessDecisionVoter {

    protected Logger log = LoggerFactory.getLogger(BaseAccessDecisionVoter.class);

    private DuracloudRepoMgr repoMgr;

    public BaseAccessDecisionVoter(DuracloudRepoMgr repoMgr) {
        this.repoMgr = repoMgr;
    }

    /**
     * This abstract method returns the class for which this voter has interest.
     *
     * @return class of target service interface
     */
    protected abstract Class getTargetService();

    public boolean supports(ConfigAttribute attribute) {
        log.debug("supports attribute{}", attribute.getAttribute());
        return true;
    }

    public boolean supports(Class<?> clazz) {
        log.debug("supports {}", clazz.getName());
        return MethodInvocation.class.isAssignableFrom(clazz);
    }

    protected boolean supportsTarget(MethodInvocation invocation) {
        Class[] interfaces = invocation.getThis().getClass().getInterfaces();
        if (null == interfaces || interfaces.length == 0) {
            return false;
        }

        for (Class c : interfaces) {
            if (c.equals(getTargetService())) {
                return true;
            }
        }
        return false;
    }

    protected SecuredRule getRule(Collection<ConfigAttribute> atts) {
        if (null == atts || atts.size() != 1) {
            throw new DuraCloudRuntimeException("Invalid security att " + atts);
        }

        return new SecuredRule(atts.iterator().next().getAttribute());
    }

    protected Collection<String> getUserRoles(Authentication authentication) {
        Set<String> roles = new HashSet<String>();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            roles.add(authority.getAuthority());
        }
        return roles;
    }

    protected int voteHasRole(String role, Collection<String> userRoles) {
        return userRoles.contains(role) ? ACCESS_GRANTED : ACCESS_DENIED;
    }

    protected int voteUserHasRoleOnAccount(DuracloudUser user,
                                           String role,
                                           int acctId) {
        AccountRights rights = getUserRightsForAcct(user, acctId);
        if (null == rights) {
            return ACCESS_DENIED;
        }

        Set<Role> acctRoles = rights.getRoles();
        if (acctRoles != null && acctRoles.size() > 0) {
            for (Role acctRole : acctRoles) {
                if (role.equals(acctRole.authority().getAuthority())) {
                    return ACCESS_GRANTED;
                }
            }
        }
        return ACCESS_DENIED;
    }

    private AccountRights getUserRightsForAcct(DuracloudUser user, int acctId) {
        DuracloudRightsRepo rightsRepo = repoMgr.getRightsRepo();
        AccountRights rights = null;
        try {
            rights = rightsRepo.findByAccountIdAndUserId(acctId, user.getId());

        } catch (DBNotFoundException e) {
            log.error("No rights for {}:{}, {}",
                      new Object[]{user.getUsername(), user.getId(), acctId});
        }
        return rights;
    }

    protected int voteMyUserId(DuracloudUser user, int userId) {
        return user.getId() == userId ? ACCESS_GRANTED : ACCESS_DENIED;
    }



    protected String asString(int decision) {
        String s = "unknown";
        switch (decision) {
            case ACCESS_DENIED:
                return "ACCESS_DENIED";
            case ACCESS_ABSTAIN:
                return "ACCESS_ABSTAIN";
            case ACCESS_GRANTED:
                return "ACCESS_GRANTED";
        }
        return s;
    }
}
