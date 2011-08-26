/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.security.web;

import org.aopalliance.intercept.MethodInvocation;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.error.DBNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class UserAccessDecisionVoter extends AbstractAccessDecisionVoter {
    private Logger log = LoggerFactory.getLogger(getClass());

    private static final int ACCT_ID_INDEX = 0;
    private static final int USER_ID_INDEX = 1;
    private static final int ROLES_ID_INDEX = 2;
    
    @Autowired
    private DuracloudRepoMgr duracloudRepoMgr;

    @Override
    protected int voteImpl(
        Authentication authentication, MethodInvocation rmi,
        Collection<ConfigAttribute> attributes) {
        String method = rmi.getMethod().getName();


        if (method.matches(".*UserByUsername.*")) {
            if(!authentication.isAuthenticated()){
                return ACCESS_DENIED;
            }
            DuracloudUser user = getUser(authentication);
            String username = (String) rmi.getArguments()[0];
            log.trace("intercepted ({})", username);
            if (username.equals(user.getUsername())) {
                return ACCESS_GRANTED;
            } else {
                return ACCESS_DENIED;
            }
        }

        // if caller is root or owner for the account all is permitted
        // otherwise call must be an admin changing another admin or user
        if (method.matches("(setUserRights|revokeUserRights).*")) {
            if(!authentication.isAuthenticated()){
                return ACCESS_DENIED;
            }

            Integer accountId = getAccountId(rmi.getArguments());
            Integer userId = getUserId(rmi.getArguments());
            Set<Role> setToRoles = getRoles(rmi.getArguments());

            DuracloudUser user = getUser(authentication);
            boolean isRoot = user.isRootForAcct(accountId);
            boolean isOwner = user.isOwnerForAcct(accountId);
            boolean isAdmin = user.isAdminForAcct(accountId);

            log.trace("intercepted {} calling {}(acountId={},userId={})",
                new Object[] { user.getUsername(), method, accountId, userId });

            if (!isRoot && !isOwner && !isAdmin) {
                //it is a user creating a new account
                //  - in this case, make sure that there are no other users of this account
                if(!accountHasRights(accountId)){
                    return ACCESS_GRANTED;
                }else{
                    return ACCESS_DENIED;
                }
            } else if (isRoot || isOwner) {
                return ACCESS_GRANTED;
            } else { // Admin
                    // under no circumstances can an admin modify an owner
                    if (setToRoles.contains(Role.ROLE_OWNER)) {
                        return ACCESS_DENIED;
                    }
                    // if the user is trying to remove their own admin rights, they
                    // can do that
                    if (userId == user.getId() && !setToRoles.contains(Role.ROLE_OWNER)) {
                        return ACCESS_GRANTED;
                    }


                // an admin can only change the rights of an admin, user or user
                // without rights.
                Set<Role> calleeRoles = null;
                try {
                    AccountRights rights =
                        rightsRepo().findByAccountIdAndUserId(accountId, userId);
                    calleeRoles = rights.getRoles();
                } catch (DBNotFoundException e) {
                }

                if (calleeRoles != null) {
                    return calleeRoles.contains(Role.ROLE_OWNER)
                        ? ACCESS_DENIED : ACCESS_GRANTED;
                } else {
                    return ACCESS_GRANTED;
                }
            }
        }

        return ACCESS_ABSTAIN;
    }

    private Integer getAccountId(Object[] methodArgs) {
        log.trace("Returning 'accountId' at index: {}", ACCT_ID_INDEX);
        return (Integer) methodArgs[ACCT_ID_INDEX];
    }

    private Integer getUserId(Object[] methodArgs) {
        log.trace("Returning 'userId' at index: {}", USER_ID_INDEX);
        return (Integer) methodArgs[USER_ID_INDEX];
    }

    private Set<Role> getRoles(Object[] methodArgs) {
        log.trace("Returning 'roles' at index: {}", ROLES_ID_INDEX);
        Set<Role> roles = new HashSet<Role>();

        if (methodArgs.length > ROLES_ID_INDEX) {
            Role[] arg = (Role[]) methodArgs[ROLES_ID_INDEX];
            if (null != arg) {
                for (Role role : arg) {
                    roles.add(role);
                }
            }
        }
        return roles;
    }

    private boolean accountHasRights(Integer accountId) {
        try {
            Set<AccountRights> rights =
                rightsRepo().findByAccountId(accountId);
            return (rights != null && rights.size() > 0);
        } catch (DBNotFoundException e) {
            return false;
        }
    }
    
    private DuracloudRightsRepo rightsRepo(){
        return  getDuracloudRepoMgr().getRightsRepo();
    }

    private DuracloudUser getUser(Authentication authentication) {
        Object p = authentication.getPrincipal();
        if (p instanceof DuracloudUser) {
            return (DuracloudUser) authentication.getPrincipal();
        }

        return null;
    }



    public void setDuracloudRepoMgr(DuracloudRepoMgr duracloudRepoMgr) {
        this.duracloudRepoMgr = duracloudRepoMgr;
    }

    public DuracloudRepoMgr getDuracloudRepoMgr() {
        return duracloudRepoMgr;
    }

}
