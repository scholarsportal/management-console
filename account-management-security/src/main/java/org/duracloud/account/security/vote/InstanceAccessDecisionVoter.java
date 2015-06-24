/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.security.vote;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.db.model.AccountRights;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.model.Role;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.util.DuracloudInstanceService;
import org.duracloud.account.security.domain.SecuredRule;
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
    protected Class<?> getTargetService() {
        return DuracloudInstanceService.class;
    }

    @Override
    protected int voteImpl(Authentication authentication,
            MethodInvocation invocation,
            Collection<ConfigAttribute> attributes, Object[] methodArgs,
            DuracloudUser user, SecuredRule securedRule, String role,
            SecuredRule.Scope scope) {
        int decision = ACCESS_DENIED;


        if (scope.equals(SecuredRule.Scope.ANY)) {
            Collection<String> userRoles = getUserRoles(authentication);
            decision = super.voteHasRole(role, userRoles);

        } else if (scope.equals(SecuredRule.Scope.SELF_ACCT)) {
            Long acctId = getAcctId(invocation);
            decision = voteUserHasRoleOnAccount(user, role, acctId);

        } else if (scope.equals(SecuredRule.Scope.SELF_ACCT_PEER_UPDATE)) {
            // Does user have required role on the account AND
            //  does the calling user have adequate rights to update the
            //  target user from previous roles to new roles?
            Long acctId = getAcctId(invocation);
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
                                                Long acctId) {
        Set<AccountRights> rights = new HashSet<AccountRights>();

        Set<DuracloudUser> users = getUsersArg(arguments);
        for (DuracloudUser user : users) {
            Set<AccountRights> userRights = user.getAccountRights();
            for(AccountRights accountRights: userRights) {
                if(accountRights.getAccount().getId().equals(acctId)) {
                    rights.add(accountRights);
                }
            }
        }
        return rights;
    }

    private int voteUserHasRoleOnAcctToUpdateUsers(Long userId,
                                                   Long acctId,
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

    @SuppressWarnings("unchecked")
    private Set<DuracloudUser> getUsersArg(Object[] arguments) {
        if (arguments.length <= USER_INDEX) {
            log.error("Illegal number of args: " + arguments.length);
        }
        return (Set<DuracloudUser>) arguments[USER_INDEX];
    }

    private Long getAcctId(MethodInvocation invocation) {
        DuracloudInstanceService instanceService = (DuracloudInstanceService) invocation
            .getThis();
        return instanceService.getAccountId();
    }



}
