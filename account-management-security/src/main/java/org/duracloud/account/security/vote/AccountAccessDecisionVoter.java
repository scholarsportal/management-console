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
import org.duracloud.account.db.util.AccountService;
import org.duracloud.account.security.domain.SecuredRule;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

/**
 * This class votes on calls to the AccountAccessService.
 *
 * @author Andrew Woods
 * Date: 4/6/11
 */
public class AccountAccessDecisionVoter extends BaseAccessDecisionVoter {

    private Logger log = LoggerFactory.getLogger(AccountAccessDecisionVoter.class);

    public AccountAccessDecisionVoter(DuracloudRepoMgr repoMgr) {
        super(repoMgr);
    }

    @Override
    protected Class<?> getTargetService() {
        return AccountService.class;
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

        if (scope.equals(SecuredRule.Scope.ANY)) {
            Collection<String> userRoles = getUserRoles(authentication);
            decision = super.voteHasRole(role, userRoles);

        } else if (scope.equals(SecuredRule.Scope.SELF_ACCT)) {
            Long acctId = getAcctId(invocation);
            decision = voteUserHasRoleOnAccount(user, role, acctId);

        } else {
            String err = "Invalid scope: " + scope;
            log.error(err);
            throw new DuraCloudRuntimeException(err);
        }

        return castVote(decision, invocation);
    }

    private Long getAcctId(MethodInvocation invocation) {
        AccountService acctService = (AccountService) invocation.getThis();
        return acctService.getAccountId();
    }

}
