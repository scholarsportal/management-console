/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.vote;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.security.domain.SecuredRule;
import org.duracloud.account.util.DuracloudInstanceManagerService;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import java.util.Collection;

/**
 * This class votes on calls to the DuracloudInstanceManagerService.
 *
 * @author Andrew Woods
 *         Date: 4/5/11
 */
public class InstanceManagerAccessDecisionVoter extends BaseAccessDecisionVoter {

    private Logger log = LoggerFactory.getLogger(
        AccountManagerAccessDecisionVoter.class);

    private final int ACCT_ID_INDEX = 0;

    public InstanceManagerAccessDecisionVoter(DuracloudRepoMgr repoMgr) {
        super(repoMgr);
    }

    @Override
    protected Class getTargetService() {
        return DuracloudInstanceManagerService.class;
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

        // Collect user making the call.
        DuracloudUser user = getCurrentUser(authentication);

        // Collect security constraints on method.
        SecuredRule securedRule = getRule(configAttributes);
        String role = securedRule.getRole().name();
        SecuredRule.Scope scope = securedRule.getScope();

        if (scope.equals(SecuredRule.Scope.ANY)) {
            Collection<String> userRoles = getUserRoles(authentication);
            decision = voteHasRole(role, userRoles);

        } else if (scope.equals(SecuredRule.Scope.SELF_ACCT)) {
            int acctId = getAccountIdArg(invocation.getArguments());
            decision = voteUserHasRoleOnAccount(user, role, acctId);

        } else {
            String err = "Invalid scope: " + scope;
            log.error(err);
            throw new DuraCloudRuntimeException(err);
        }

        return castVote(decision, invocation);
    }

    private int getAccountIdArg(Object[] arguments) {
        if (arguments.length != 1 && arguments.length != 2) {
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
