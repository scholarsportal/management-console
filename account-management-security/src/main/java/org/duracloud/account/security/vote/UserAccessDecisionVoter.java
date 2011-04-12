/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.vote;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.security.domain.SecuredRule;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class votes on calls to the DuracloudUserService.
 *
 * @author Andrew Woods
 *         Date: 4/6/11
 */
public class UserAccessDecisionVoter extends BaseAccessDecisionVoter {

    private Logger log = LoggerFactory.getLogger(UserAccessDecisionVoter.class);

    private final int ACCT_ID_INDEX = 0;
    private final int USER_ID_INDEX = 0;
    private final int OTHER_USER_ID_INDEX = 1;
    private final int NEW_ROLES_INDEX = 2;

    public UserAccessDecisionVoter(DuracloudRepoMgr repoMgr) {
        super(repoMgr);
    }

    @Override
    protected Class getTargetService() {
        return DuracloudUserService.class;
    }

    @Override
    public int vote(Authentication authentication,
                    Object argInvocation,
                    Collection<ConfigAttribute> configAttributes) {
        int decision = ACCESS_DENIED;

        // Collect target method invocation.
        MethodInvocation invocation = (MethodInvocation) argInvocation;
        if (!supportsTarget(invocation)) {
            return castVote(ACCESS_ABSTAIN, invocation);
        }

        // Collect target method arguments
        Object[] methodArgs = invocation.getArguments();

        // Collect user making the call.
        DuracloudUser user = getCurrentUser(authentication);

        // Collect security constraints on method.
        SecuredRule securedRule = getRule(configAttributes);
        String role = securedRule.getRole().name();
        SecuredRule.Scope scope = securedRule.getScope();

        Collection<String> userRoles = getUserRoles(authentication);

        if (scope.equals(SecuredRule.Scope.ANY)) {
            decision = voteHasRole(role, userRoles);

        } else if (scope.equals(SecuredRule.Scope.SELF)) {
            // Does user have required role AND
            //  is call acting on the calling user's userId?
            if (hasVote(voteHasRole(role, userRoles))) {
                int userId = getUserIdArg(methodArgs);
                decision = voteMyUserId(user, userId);
            }

        } else if (scope.equals(SecuredRule.Scope.SELF_ACCT_PEER)) {
            // Does user have required role on the account AND
            //  does the calling user have adequate rights to manage the
            //  target user?
            int acctId = getAccountIdArg(methodArgs);
            if (hasVote(voteUserHasRoleOnAccount(user, role, acctId))) {
                int otherUserId = getOtherUserIdArg(methodArgs);
                decision = voteUserHasRoleOnAcctToManageOther(user.getId(),
                                                              acctId,
                                                              otherUserId);
            }

        } else if (scope.equals(SecuredRule.Scope.SELF_ACCT_PEER_UPDATE)) {
            // Does user have required role on the account AND
            //  does the calling user have adequate rights to update the
            //  target user from previous roles to new roles?
            int acctId = getAccountIdArg(methodArgs);
            int otherUserId = getOtherUserIdArg(methodArgs);
            Set<Role> otherRoles = getOtherRolesArg(methodArgs);
            if (hasVote(voteUserHasRoleOnAccount(user, role, acctId))) {
                decision = voteUserHasRoleOnAcctToUpdateOthersRoles(user.getId(),
                                                                    acctId,
                                                                    otherUserId,
                                                                    otherRoles);
            } else {
                // Or, is the calling user creating a new account?
                decision = voteUserIsCreatingNewAcct(user,
                                                     acctId,
                                                     otherUserId,
                                                     otherRoles);
            }

        } else {
            String err = "Invalid scope: " + scope;
            log.error(err);
            throw new DuraCloudRuntimeException(err);
        }

        return castVote(decision, invocation);
    }

    private int voteUserHasRoleOnAcctToManageOther(int userId,
                                                   int acctId,
                                                   int otherUserId) {
        log.debug("Voting if user {} has roles on acct {} to manage {}.",
                  new Object[]{userId, acctId, otherUserId});

        AccountRights rights = getUserRightsForAcct(userId, acctId);
        AccountRights other = getUserRightsForAcct(otherUserId, acctId);

        if (null == rights || null == other) {
            log.warn("No rights found for users {}, {} on acct {}",
                     new Object[]{userId, otherUserId, acctId});
            return ACCESS_DENIED;
        }

        return voteRolesAreSufficientToUpdateOther(rights.getRoles(),
                                                   other.getRoles());
    }

    private int voteUserIsCreatingNewAcct(DuracloudUser user,
                                          int acctId,
                                          int otherUserId,
                                          Set<Role> otherRoles) {
        return hasVote(voteMyUserId(user, otherUserId)) &&
            accountIsEmpty(acctId) &&
            isOwner(otherRoles) ? ACCESS_GRANTED : ACCESS_DENIED;
    }

    private boolean accountIsEmpty(int acctId) {
        return numUsersForAccount(acctId) == 0;
    }

    private boolean isOwner(Set<Role> roles) {
        return roles.contains(Role.ROLE_OWNER);
    }

    /**
     * This method returns roles argument of in the target method invocation.
     */
    private Set<Role> getOtherRolesArg(Object[] arguments) {
        if (arguments.length <= NEW_ROLES_INDEX) {
            log.error("Illegal number of args: " + arguments.length);
        }

        Set<Role> roles = new HashSet<Role>();
        Object[] rolesArray = (Object[]) arguments[NEW_ROLES_INDEX];
        if (null != rolesArray && rolesArray.length > 0) {
            for (Object role : rolesArray) {
                roles.add((Role) role);
            }
        }

        return roles;
    }

    /**
     * This method returns peer userId argument of the target method invocation.
     */
    private int getOtherUserIdArg(Object[] arguments) {
        if (arguments.length <= OTHER_USER_ID_INDEX) {
            log.error("Illegal number of args: " + arguments.length);
        }
        return (Integer) arguments[OTHER_USER_ID_INDEX];
    }

    /**
     * This method returns userId argument of the target method invocation.
     */
    private int getUserIdArg(Object[] arguments) {
        if (arguments.length <= USER_ID_INDEX) {
            log.error("Illegal number of args: " + arguments.length);
        }
        return (Integer) arguments[USER_ID_INDEX];
    }

    /**
     * This method returns acctId argument of the target method invocation.
     */
    private int getAccountIdArg(Object[] arguments) {
        if (arguments.length <= ACCT_ID_INDEX) {
            log.error("Illegal number of args: " + arguments.length);
        }
        return (Integer) arguments[ACCT_ID_INDEX];
    }

    private int castVote(int decision, MethodInvocation invocation) {
        String methodName = invocation.getMethod().getName();
        String className = invocation.getThis().getClass().getSimpleName();
        log.debug("{}.{}() = {}", new Object[]{className, methodName, asString(
            decision)});
        return decision;
    }

}
