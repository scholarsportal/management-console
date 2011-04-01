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
 *         Date: 3/31/11
 */
public class AccountManagerAccessDecisionVoter implements AccessDecisionVoter {

    private Logger log = LoggerFactory.getLogger(
        AccountManagerAccessDecisionVoter.class);

    private DuracloudRepoMgr repoMgr;

    public AccountManagerAccessDecisionVoter(DuracloudRepoMgr repoMgr) {
        this.repoMgr = repoMgr;
    }

    public boolean supports(ConfigAttribute attribute) {
        log.debug("supports attribute{}", attribute.getAttribute());
        return true;
    }

    public boolean supports(Class<?> clazz) {
        log.debug("supports {}", clazz.getName());
        return MethodInvocation.class.isAssignableFrom(clazz);
    }

    @Override
    public int vote(Authentication authentication,
                    Object argInvocation,
                    Collection<ConfigAttribute> configAttributes) {
        int decision = ACCESS_DENIED;

        // Collect target method invocation.
        MethodInvocation invocation = (MethodInvocation) argInvocation;

        // Collect user making the call.
        DuracloudUser user = (DuracloudUser) authentication.getPrincipal();

        // Collect security constraints on method.
        SecuredRule securedRule = getRule(configAttributes);
        String role = securedRule.getRole();
        String scope = securedRule.getScope();

        Collection<String> userRoles = getUserRoles(authentication);

        if (scope.equals("any")) {
            decision = voteHasRole(role, userRoles);

        } else if (scope.equals("self-acct")) {
            int acctId = getIntArg(invocation.getArguments());
            decision = voteUserHasRoleOnAccount(user, role, acctId);

        } else if (scope.equals("self")) {
            if (voteHasRole(role, userRoles) == ACCESS_GRANTED) {
                int userId = getIntArg(invocation.getArguments());
                decision = voteMyUserId(user, userId);
            }

        } else {
            String err = "Invalid scope: " + scope;
            log.error(err);
            throw new DuraCloudRuntimeException(err);
        }

        log.debug("{}() = {}", invocation.getMethod().getName(), asString(
            decision));
        return decision;
    }

    private SecuredRule getRule(Collection<ConfigAttribute> atts) {
        if (null == atts || atts.size() != 1) {
            throw new DuraCloudRuntimeException("Invalid security att " + atts);
        }

        return new SecuredRule(atts.iterator().next().getAttribute());
    }

    private Collection<String> getUserRoles(Authentication authentication) {
        Set<String> roles = new HashSet<String>();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            roles.add(authority.getAuthority());
        }
        return roles;
    }

    private int getIntArg(Object[] arguments) {
        if (arguments.length != 1) {
            log.error("Illegal number of args: " + arguments.length);
        }
        return (Integer) arguments[0];
    }

    private int voteHasRole(String role, Collection<String> userRoles) {
        return userRoles.contains(role) ? ACCESS_GRANTED : ACCESS_DENIED;
    }

    private int voteUserHasRoleOnAccount(DuracloudUser user,
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

    private int voteMyUserId(DuracloudUser user, int userId) {
        return user.getId() == userId ? ACCESS_GRANTED : ACCESS_DENIED;
    }

    private String asString(int decision) {
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
