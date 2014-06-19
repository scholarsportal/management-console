/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.vote;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.util.DuracloudInstanceManagerService;
import org.duracloud.account.security.domain.SecuredRule;
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
    protected Class<?> getTargetService() {
        return DuracloudInstanceManagerService.class;
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
            decision = voteHasRole(role, userRoles);

        } else if (scope.equals(SecuredRule.Scope.SELF_ACCT)) {
            Long acctId = getAccountIdArg(methodArgs);
            decision = voteUserHasRoleOnAccount(user, role, acctId);

        } else {
            String err = "Invalid scope: " + scope;
            log.error(err);
            throw new DuraCloudRuntimeException(err);
        }

        return castVote(decision, invocation);
    }

    private Long getAccountIdArg(Object[] arguments) {
        if (arguments.length != 1 && arguments.length != 2) {
            log.error("Illegal number of args: " + arguments.length);
        }
        return (Long) arguments[ACCT_ID_INDEX];
    }
}
