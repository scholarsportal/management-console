/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.security.vote;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.db.model.AccountRights;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.model.Role;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.util.DuracloudUserService;
import org.duracloud.account.security.domain.SecuredRule;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

/**
 * This class votes on calls to the DuracloudUserService.
 *
 * @author Andrew Woods
 * Date: 4/6/11
 */
public class UserAccessDecisionVoter extends BaseAccessDecisionVoter {

    private Logger log = LoggerFactory.getLogger(UserAccessDecisionVoter.class);

    private final int ACCT_ID_INDEX = 0;
    private final int USER_ID_INDEX = 0;
    private final int USER_NAME_INDEX = 0;
    private final int OTHER_USER_ID_INDEX = 1;
    private final int NEW_ROLES_INDEX = 2;

    public UserAccessDecisionVoter(DuracloudRepoMgr repoMgr) {
        super(repoMgr);
    }

    @Override
    protected Class<?> getTargetService() {
        return DuracloudUserService.class;
    }

    @Override
    protected int voteImpl(Authentication authentication,
                           MethodInvocation invocation,
                           Collection<ConfigAttribute> attributes,
                           Object[] methodArgs,
                           DuracloudUser user,
                           SecuredRule securedRule,
                           String role,
                           SecuredRule.Scope scope) {

        int decision = ACCESS_DENIED;

        Collection<String> userRoles = getUserRoles(authentication);

        if (scope.equals(SecuredRule.Scope.ANY)) {
            decision = voteHasRole(role, userRoles);

        } else if (scope.equals(SecuredRule.Scope.SELF_ID)) {
            // Does user have required role AND
            //  is call acting on the calling user's userId?
            if (hasVote(voteHasRole(role, userRoles))) {
                Long userId = getUserIdArg(methodArgs);
                decision = voteMyUserId(user, userId);
            }

        } else if (scope.equals(SecuredRule.Scope.SELF_NAME)) {
            // Does user have required role AND
            //  is call acting on the calling user's username?
            if (hasVote(voteHasRole(role, userRoles))) {
                String username = getUsernameArg(methodArgs);
                decision = voteMyUsername(user, username);
            }

        } else if (scope.equals(SecuredRule.Scope.SELF_ACCT)) {
            // Does user have required role on the account?
            Long acctId = getAccountIdArg(methodArgs);
            decision = voteUserHasRoleOnAccount(user, role, acctId);

        } else if (scope.equals(SecuredRule.Scope.SELF_ACCT_PEER)) {
            // Does user have required role on the account AND
            //  does the calling user have adequate rights to manage the
            //  target user?
            Long acctId = getAccountIdArg(methodArgs);
            if (hasVote(voteUserHasRoleOnAccount(user, role, acctId))) {
                Long otherUserId = getOtherUserIdArg(methodArgs);
                decision = voteUserHasRoleOnAcctToManageOther(user.getId(),
                                                              acctId,
                                                              otherUserId);
            }

        } else if (scope.equals(SecuredRule.Scope.SELF_ACCT_PEER_UPDATE)) {
            // Does user have required role on the account AND
            //  does the calling user have adequate rights to update the
            //  target user from previous roles to new roles?
            Long acctId = getAccountIdArg(methodArgs);
            Long otherUserId = getOtherUserIdArg(methodArgs);
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

    private int voteUserHasRoleOnAcctToManageOther(Long userId,
                                                   Long acctId,
                                                   Long otherUserId) {
        log.trace("Voting if user {} has roles on acct {} to manage {}.",
                  new Object[] {userId, acctId, otherUserId});

        AccountRights rights = getUserRightsForAcct(userId, acctId);
        AccountRights other = getUserRightsForAcct(otherUserId, acctId);

        if (null == rights || null == other) {
            log.warn("No rights found for users {}, {} on acct {}",
                     new Object[] {userId, otherUserId, acctId});
            return ACCESS_DENIED;
        }

        return voteRolesAreSufficientToUpdateOther(rights.getRoles(),
                                                   other.getRoles());
    }

    private int voteUserIsCreatingNewAcct(DuracloudUser user,
                                          Long acctId,
                                          Long otherUserId,
                                          Set<Role> otherRoles) {
        return hasVote(voteMyUserId(user, otherUserId)) &&
               accountIsEmpty(acctId) &&
               isOwner(otherRoles) ? ACCESS_GRANTED : ACCESS_DENIED;
    }

    private boolean accountIsEmpty(Long acctId) {
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
    private Long getOtherUserIdArg(Object[] arguments) {
        if (arguments.length <= OTHER_USER_ID_INDEX) {
            log.error("Illegal number of args: " + arguments.length);
        }
        return (Long) arguments[OTHER_USER_ID_INDEX];
    }

    /**
     * This method returns userId argument of the target method invocation.
     */
    private Long getUserIdArg(Object[] arguments) {
        if (arguments.length <= USER_ID_INDEX) {
            log.error("Illegal number of args: " + arguments.length);
        }
        return (Long) arguments[USER_ID_INDEX];
    }

    private String getUsernameArg(Object[] arguments) {
        if (arguments.length <= USER_NAME_INDEX) {
            log.error("Illegal number of args: " + arguments.length);
        }
        return (String) arguments[USER_NAME_INDEX];
    }

    /**
     * This method returns acctId argument of the target method invocation.
     */
    private Long getAccountIdArg(Object[] arguments) {
        if (arguments.length <= ACCT_ID_INDEX) {
            log.error("Illegal number of args: " + arguments.length);
        }
        return (Long) arguments[ACCT_ID_INDEX];
    }

}
