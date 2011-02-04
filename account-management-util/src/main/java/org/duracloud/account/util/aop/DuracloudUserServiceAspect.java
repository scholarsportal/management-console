/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.usermgmt.UserDetailsPropagator;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * This class defines an aspect over calls to UserDetailsService.
 * It is responsible for delegating the task of propagating UserDetails
 * updates down to the underlying DuraCloud webapps.
 *
 * @author Andrew Woods
 *         Date: Feb 2, 2011
 */
@Aspect
public class DuracloudUserServiceAspect {

    private final Logger log = LoggerFactory.getLogger(
        DuracloudUserServiceAspect.class);

    private static final int ACCT_ID_INDEX = 0;
    private static final int USER_ID_INDEX = 1;
    private static final int ROLES_INDEX = 2;

    private UserDetailsPropagator propagator;

    public DuracloudUserServiceAspect(UserDetailsPropagator propagator) {
        this.propagator = propagator;
    }

    /**
     * This method applies advice over calls to DuracloudUserService.setUserRights.
     * It delegates down the task of propagating UserDetails updates to the
     * underlying DuraCloud webapps.
     *
     * @param jp     JoinPoint context information of the call
     * @param result return result of target call
     */
    @AfterReturning(
        pointcut = "execution(* org.duracloud.account.util.DuracloudUserService.setUserRights(..))",
        returning = "result")
    public void setUserRights(JoinPoint jp, boolean result) {

        if (result == false) {
            log.info("setUserRights() failed, not propagating updates.");
            return;
        }

        Object[] args = jp.getArgs();
        if (null == args) {
            String msg = "setUserRights() had no args?!";
            log.error(msg);
            throw new DuraCloudRuntimeException(msg);
        }

        int acctId = getAcctId(args);
        int userId = getUserId(args);
        Set<Role> roles = getRoles(args);

        log.debug("propagating update for: " + acctId + ", " + userId + ", " +
            asString(roles));
        propagator.propagateRights(acctId, userId, roles);
    }

    private int getAcctId(Object[] args) {
        return (Integer) args[ACCT_ID_INDEX];
    }

    private int getUserId(Object[] args) {
        return (Integer) args[USER_ID_INDEX];
    }

    private Set<Role> getRoles(Object[] args) {
        Set<Role> roles = new HashSet<Role>();
        for (Role role : (Role[]) args[ROLES_INDEX]) {
            roles.add(role);
        }

        return roles;
    }

    private String asString(Set<Role> roles) {
        StringBuilder sb = new StringBuilder();
        for (Role role : roles) {
            sb.append(role.name());
            sb.append(",");
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    /**
     * This method applies advice over calls to DuracloudUserService.setUserRights.
     * It delegates down the task of propagating UserDetails updates to the
     * underlying DuraCloud webapps.
     *
     * @param jp JoinPoint context information of the call
     * @throws DBNotFoundException
     */
    @AfterReturning(
        pointcut = "execution(* org.duracloud.account.util.DuracloudUserService.revokeUserRights(..))")
    public void revokeUserRights(JoinPoint jp) {
        Object[] args = jp.getArgs();
        if (null == args) {
            String msg = "revokeUserRights() had no args?!";
            log.error(msg);
            throw new DuraCloudRuntimeException(msg);
        }

        int acctId = getAcctId(args);
        int userId = getUserId(args);

        log.debug("propagating revocation for: " + acctId + ", " + userId);
        propagator.propagateRevocation(acctId, userId);
    }
}
