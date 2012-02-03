/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.vote;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.security.domain.SecuredRule;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import java.util.Collection;

/**
 * This class votes on calls to the AccountManagerService.
 *
 * @author Andrew Woods
 *         Date: 3/31/11
 */
public class AccountManagerAccessDecisionVoter extends BaseAccessDecisionVoter {

    private Logger log = LoggerFactory.getLogger(
        AccountManagerAccessDecisionVoter.class);

    public AccountManagerAccessDecisionVoter(DuracloudRepoMgr repoMgr) {
        super(repoMgr);
    }

    @Override
    protected Class<?> getTargetService() {
        return AccountManagerService.class;
    }

    @Override
    public int vote(Authentication authentication,
                    MethodInvocation invocation,
                    Collection<ConfigAttribute> configAttributes) {
        int decision = ACCESS_DENIED;

        if (!supportsTarget(invocation)) {
            return castVote(ACCESS_ABSTAIN, invocation);
        }

        // Collect user making the call.
        DuracloudUser user = getCurrentUser(authentication);

        // Collect security constraints on method.
        SecuredRule securedRule = getRule(configAttributes);
        String role = securedRule.getRole().name();
        SecuredRule.Scope scope = securedRule.getScope();

        Collection<String> userRoles = getUserRoles(authentication);

        if (scope.equals(SecuredRule.Scope.ANY)) {
            decision = voteHasRole(role, userRoles);

        } else if (scope.equals(SecuredRule.Scope.SELF_ACCT)) {
            int acctId = getIntArg(invocation.getArguments());
            decision = voteUserHasRoleOnAccount(user, role, acctId);

        } else if (scope.equals(SecuredRule.Scope.SELF_ID)) {
            if (voteHasRole(role, userRoles) == ACCESS_GRANTED) {
                int userId = getIntArg(invocation.getArguments());
                decision = voteMyUserId(user, userId);
            }

        } else {
            String err = "Invalid scope: " + scope;
            log.error(err);
            throw new DuraCloudRuntimeException(err);
        }

        return castVote(decision, invocation);
    }


    private int getIntArg(Object[] arguments) {
        if (arguments.length != 1) {
            log.error("Illegal number of args: " + arguments.length);
        }
        return (Integer) arguments[0];
    }

    private int castVote(int decision, MethodInvocation invocation) {
        String methodName = invocation.getMethod().getName();
        String className = invocation.getThis().getClass().getSimpleName();
        log.trace("{}.{}() = {}", new Object[]{className, methodName, asString(
            decision)});
        return decision;
    }

}
