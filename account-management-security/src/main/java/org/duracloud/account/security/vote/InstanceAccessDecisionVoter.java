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
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class votes on calls to the DuracloudInstanceService.
 *
 * @author Andrew Woods
 *         Date: 4/10/11
 */
public class InstanceAccessDecisionVoter extends BaseAccessDecisionVoter {

    private Logger log = LoggerFactory.getLogger(AccountAccessDecisionVoter.class);

    private final int USER_INDEX = 0;

    public InstanceAccessDecisionVoter(DuracloudRepoMgr repoMgr) {
        super(repoMgr);
    }

    @Override
    protected Class getTargetService() {
        return DuracloudInstanceService.class;
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

        if (scope.equals(SecuredRule.Scope.ANY)) {
            Collection<String> userRoles = getUserRoles(authentication);
            decision = super.voteHasRole(role, userRoles);

        } else if (scope.equals(SecuredRule.Scope.SELF_ACCT)) {
            int acctId = getAcctId(invocation);
            decision = voteUserHasRoleOnAccount(user, role, acctId);

        } else if (scope.equals(SecuredRule.Scope.SELF_ACCT_PEER_UPDATE)) {
            // Does user have required role on the account AND
            //  does the calling user have adequate rights to update the
            //  target user from previous roles to new roles?
            int acctId = getAcctId(invocation);
            if (hasVote(voteUserHasRoleOnAccount(user, role, acctId))) {
                Set<AccountRights> existingRights = getAllUserRightsForAcct(
                    acctId);
                Set<AccountRights> updatedRights = getUpdatedRights(methodArgs,
                                                                    acctId);
                decision = voteUserHasRoleOnAcctToUpdateUsers(user.getId(),
                                                              acctId,
                                                              existingRights,
                                                              updatedRights);
            }

        } else {
            String err = "Invalid scope: " + scope;
            log.error(err);
            throw new DuraCloudRuntimeException(err);
        }

        return castVote(decision, invocation);
    }

    private Set<AccountRights> getUpdatedRights(Object[] arguments,
                                                int acctId) {
        Set<AccountRights> rights = new HashSet<AccountRights>();

        Set<DuracloudUser> users = getUsersArg(arguments);
        for (DuracloudUser user : users) {
            rights.add(new AccountRights(-1,
                                         acctId,
                                         user.getId(),
                                         user.getRolesByAcct(acctId)));
        }
        return rights;
    }

    private int voteUserHasRoleOnAcctToUpdateUsers(int userId,
                                                   int acctId,
                                                   Set<AccountRights> existingRights,
                                                   Set<AccountRights> updatedRights) {
        AccountRights userRights = getUserRightsForAcct(userId, acctId);
        Set<Role> userRoles = userRights.getRoles();

        for (AccountRights rights : existingRights) {
            if (!hasVote(voteRolesAreSufficientToUpdateOther(userRoles,
                                                             rights.getRoles()))) {
                return ACCESS_DENIED;
            }
        }
        for (AccountRights rights : updatedRights) {
            if (!hasVote(voteRolesAreSufficientToUpdateOther(userRoles,
                                                             rights.getRoles()))) {
                return ACCESS_DENIED;
            }
        }
        return ACCESS_GRANTED;
    }

    private Set<DuracloudUser> getUsersArg(Object[] arguments) {
        if (arguments.length <= USER_INDEX) {
            log.error("Illegal number of args: " + arguments.length);
        }
        return (Set) arguments[USER_INDEX];
    }

    private int getAcctId(MethodInvocation invocation) {
        DuracloudInstanceService instanceService = (DuracloudInstanceService) invocation
            .getThis();
        return instanceService.getAccountId();
    }

    private int castVote(int decision, MethodInvocation invocation) {
        String methodName = invocation.getMethod().getName();
        String className = invocation.getThis().getClass().getSimpleName();
        log.debug("{}.{}() = {}", new Object[]{className, methodName, asString(
            decision)});
        return decision;
    }

}
