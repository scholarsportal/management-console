/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.security.vote;

import java.util.Collection;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.util.AccountManagerService;
import org.duracloud.account.security.domain.SecuredRule;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

/**
 * This class votes on calls to the AccountManagerService.
 *
 * @author Andrew Woods
 * Date: 3/31/11
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

        } else if (scope.equals(SecuredRule.Scope.SELF_ACCT)) {
            Long acctId = getIntArg(invocation.getArguments());
            decision = voteUserHasRoleOnAccount(user, role, acctId);

        } else if (scope.equals(SecuredRule.Scope.SELF_ID)) {
            if (voteHasRole(role, userRoles) == ACCESS_GRANTED) {
                Long userId = getIntArg(invocation.getArguments());
                decision = voteMyUserId(user, userId);
            }

        } else {
            String err = "Invalid scope: " + scope;
            log.error(err);
            throw new DuraCloudRuntimeException(err);
        }

        return castVote(decision, invocation);
    }

    private Long getIntArg(Object[] arguments) {
        if (arguments.length != 1) {
            log.error("Illegal number of args: " + arguments.length);
        }
        return (Long) arguments[0];
    }

}
