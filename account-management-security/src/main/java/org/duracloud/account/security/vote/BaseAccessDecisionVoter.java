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
        log.debug("Does user {} have role {} on acct {}?",
                  new Object[]{user.getId(), role, acctId});

        AccountRights rights = getUserRightsForAcct(user.getId(), acctId);
        if (null == rights) {
            return ACCESS_DENIED;
        }

        Set<Role> acctRoles = rights.getRoles();
        log.debug("Roles found: {}", acctRoles);

        if (acctRoles != null && acctRoles.size() > 0) {
            for (Role acctRole : acctRoles) {
                if (role.equals(acctRole.authority().getAuthority())) {
                    return ACCESS_GRANTED;
                }
            }
        }
        return ACCESS_DENIED;
    }

    protected int voteUserHasRoleOnAcctToUpdateOthersRoles(int userId,
                                                           int acctId,
                                                           int otherUserId,
                                                           Set<Role> otherRoles) {
        log.debug("Voting if user {} has roles on acct {} to manage {}.",
                  new Object[]{userId, acctId, otherUserId});

        AccountRights rights = getUserRightsForAcct(userId, acctId);
        AccountRights other = getUserRightsForAcct(otherUserId, acctId);

        if (null != rights && isRoot(rights)) {
           return ACCESS_GRANTED;
        }

        if (null == rights || null == other) {
            log.warn("No rights found for users {}, {} on acct {}",
                     new Object[]{userId, otherUserId, acctId});
            return ACCESS_DENIED;
        }

        boolean existing = hasVote(voteRolesAreSufficientToUpdateOther(rights.getRoles(),
                                                                       other.getRoles()));

        boolean updates = hasVote(voteRolesAreSufficientToUpdateOther(rights.getRoles(),
                                                                      otherRoles));

        log.debug("Are {} sufficient to update both {} and {}?",
                  new Object[]{rights.getRoles(),
                               other.getRoles(),
                               otherRoles});

        return existing && updates ? ACCESS_GRANTED : ACCESS_DENIED;
    }

    private boolean isRoot(AccountRights rights) {
        return Role.ROLE_ROOT.equals(Role.highestRole(rights.getRoles()));
    }

    protected int voteRolesAreSufficientToUpdateOther(Set<Role> roles,
                                                      Set<Role> other) {
        if (null == roles || null == other) {
            log.warn("Null roles one or more {}, {}", roles, other);
            return ACCESS_DENIED;
        }

        Role otherHighestRole = Role.highestRole(other);
        if (null == otherHighestRole) {
            log.warn("No highest role found for {}", other);
            return ACCESS_DENIED;
        }

        boolean userHasRole = roles.contains(otherHighestRole);
        log.debug("Roles {} has permission to manage other {}",
                  roles,
                  otherHighestRole);

        return userHasRole ? ACCESS_GRANTED : ACCESS_DENIED;
    }

    protected boolean hasVote(int vote) {
        return vote == ACCESS_GRANTED;
    }

    protected int numUsersForAccount(int acctId) {
        Set<AccountRights> rights = null;
        try {
            rights = repoMgr.getRightsRepo().findByAccountId(acctId);
        } catch (DBNotFoundException e) {
            log.warn("Account not found: {}", acctId);
        }
        return (null != rights) ? rights.size() : 0;
    }

    protected AccountRights getUserRightsForAcct(int userId, int acctId) {
        DuracloudRightsRepo rightsRepo = repoMgr.getRightsRepo();
        AccountRights rights = null;
        try {
            rights = rightsRepo.findByAccountIdAndUserId(acctId, userId);

        } catch (DBNotFoundException e) {
            log.error("No rights for user:{}, acct:{}", userId, acctId);
        }
        return rights;
    }

    protected Set<AccountRights> getAllUserRightsForAcct(int acctId) {
        DuracloudRightsRepo rightsRepo = repoMgr.getRightsRepo();
        Set<AccountRights> rights = null;
        try {
            rights = rightsRepo.findByAccountId(acctId);

        } catch (DBNotFoundException e) {
            log.error("Error find rights for acct: {}", acctId);
            throw new DuraCloudRuntimeException(e);
        }
        return rights;
    }

    protected int voteMyUserId(DuracloudUser user, int userId) {
        return user.getId() == userId ? ACCESS_GRANTED : ACCESS_DENIED;
    }

    protected int voteMyUsername(DuracloudUser user, String username) {
        return user.getUsername().equals(username) ? ACCESS_GRANTED : ACCESS_DENIED;
    }

    protected DuracloudUser getCurrentUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof String) {
            log.debug("Unknown user {}", principal);
            return new DuracloudUser(-1,
                                     (String) principal,
                                     null,
                                     null,
                                     null,
                                     null,
                                     null,
                                     null,
                                     -1);
        } else {
            return (DuracloudUser) principal;
        }
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
