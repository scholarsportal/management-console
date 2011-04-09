/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.usermgmt.impl;

import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.util.AccountServiceFactory;
import org.duracloud.account.util.DuracloudInstanceManagerService;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.usermgmt.UserDetailsPropagator;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * This class propagates UserDetail updates down to the Duracloud Instance.
 *
 * @author Andrew Woods
 *         Date: Feb 2, 2011
 */
public class UserDetailsPropagatorImpl implements UserDetailsPropagator {

    private Logger log = LoggerFactory.getLogger(UserDetailsPropagatorImpl.class);

    private DuracloudInstanceManagerService instanceManagerService;
    private AccountServiceFactory accountServiceFactory;

    private Exception error = null;

    public UserDetailsPropagatorImpl(DuracloudInstanceManagerService instanceManagerService,
                                     AccountServiceFactory accountServiceFactory) {
        this.instanceManagerService = instanceManagerService;
        this.accountServiceFactory = accountServiceFactory;
    }

    @Override
    public void propagateRevocation(int acctId, int userId) {
        propagateRights(acctId, userId, null);
    }

    @Override
    public void propagateRights(int acctId, int userId, Set<Role> roles) {
        Set<DuracloudUser> users = findUsers(acctId);
        Set<DuracloudUser> newUsers;
        if (null == roles) {
            newUsers = removeUser(userId, users);

        } else {
            newUsers = updateUserRoles(userId, acctId, roles, users);
        }

        doPropagate(acctId, newUsers);
        checkForErrors(acctId, userId);
    }

    @Override
    public void propagatePasswordUpdate(int acctId, int userId) {
        Set<DuracloudUser> users = findUsers(acctId);
        doPropagate(acctId, users);
        checkForErrors(acctId, userId);
    }

    private void checkForErrors(int acctId, int userId) {
        if (null != error) {
            throw new DuraCloudRuntimeException(
                "Failed to propagate: acctId" + acctId + ", userId" + userId,
                error);
        }
    }

    private Set<DuracloudUser> findUsers(int acctId) {
        Set<DuracloudUser> users = new HashSet<DuracloudUser>();
        try {
            users = accountServiceFactory.getAccount(acctId).getUsers();

        } catch (AccountNotFoundException e) {
            log.error("Unable to get users for acct: " + acctId, e);
            error = e;
        }
        return users;
    }

    private Set<DuracloudUser> removeUser(int userId,
                                          Set<DuracloudUser> users) {
        Set<DuracloudUser> results = new HashSet<DuracloudUser>();
        for (DuracloudUser user : users) {
            if (userId != user.getId()) {
                results.add(user);
            }
        }
        return results;
    }

    private Set<DuracloudUser> updateUserRoles(int userId,
                                               int acctId,
                                               Set<Role> roles,
                                               Set<DuracloudUser> users) {
        Set<DuracloudUser> results = new HashSet<DuracloudUser>();
        for (DuracloudUser user : users) {
            if (userId == user.getId()) {
                Set<AccountRights> userRights = new HashSet<AccountRights>();
                userRights.add(new AccountRights(-1, acctId, userId, roles));
                user.setAccountRights(userRights);
            }

            results.add(user);
        }
        return results;
    }

    private void doPropagate(int acctId, Set<DuracloudUser> users) {
        log.debug("propagating user roles for acct: " + acctId);

        Set<DuracloudInstanceService> services =
            instanceManagerService.getInstanceServices(acctId);
        for (DuracloudInstanceService service : services) {
            DuracloudInstance instanceInfo = service.getInstanceInfo();
            log.debug("propagating user roles: {}, {}",
                      instanceInfo.getHostName(),
                      users);

            service.setUserRoles(users);
        }
    }

}
