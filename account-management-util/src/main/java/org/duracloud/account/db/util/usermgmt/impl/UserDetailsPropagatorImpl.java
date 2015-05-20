/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.usermgmt.impl;

import java.util.HashSet;
import java.util.Set;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.DuracloudInstance;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.model.Role;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.util.DuracloudInstanceManagerService;
import org.duracloud.account.db.util.DuracloudInstanceService;
import org.duracloud.account.db.util.usermgmt.UserDetailsPropagator;
import org.duracloud.account.db.util.util.UserFinderUtil;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class propagates UserDetail updates down to the Duracloud Instance.
 *
 * @author Andrew Woods
 *         Date: Feb 2, 2011
 */
public class UserDetailsPropagatorImpl implements UserDetailsPropagator {

    private Logger log = LoggerFactory.getLogger(UserDetailsPropagatorImpl.class);

    private DuracloudRepoMgr repoMgr;
    private DuracloudInstanceManagerService instanceManagerService;

    private Exception error = null;
    
    private UserFinderUtil userFinder;

    public UserDetailsPropagatorImpl(DuracloudRepoMgr repoMgr,
                                     DuracloudInstanceManagerService instanceManagerService,
                                     UserFinderUtil userFinder) {
        this.repoMgr = repoMgr;
        this.instanceManagerService = instanceManagerService;
        this.userFinder = userFinder;
    }

    @Override
    public void propagateRevocation(Long acctId, Long userId) {
        propagateRights(acctId, userId, null);
    }

    @Override
    public void propagateRights(Long acctId, Long userId, Set<Role> roles) {
        Set<DuracloudUser> users = findUsers(acctId);
        if (null == roles) {
            users = removeUser(userId, users);
        }

        doPropagate(acctId, users);
        checkForErrors(acctId, userId, "userId");
    }

    @Override
    public void propagateUserUpdate(Long acctId, Long userId) {
        Set<DuracloudUser> users = findUsers(acctId);
        doPropagate(acctId, users);
        checkForErrors(acctId, userId, "userId");
    }

    @Override
    public void propagateGroupUpdate(Long acctId, Long groupId) {
        Set<DuracloudUser> users = findUsers(acctId);
        doPropagate(acctId, users);
        checkForErrors(acctId, groupId, "groupId");
    }

    private void checkForErrors(Long acctId, Long id, String idName) {
        if (null != error) {
            StringBuilder msg = new StringBuilder("Failed to propagate, ");
            msg.append("acctId: ");
            msg.append(acctId);
            msg.append(", ");
            msg.append(idName);
            msg.append(": ");
            msg.append(id);
            throw new DuraCloudRuntimeException(msg.toString(), error);
        }
    }

    private Set<DuracloudUser> findUsers(Long acctId) {
        Set<DuracloudUser> users = new HashSet<DuracloudUser>();
        AccountInfo acctInfo = repoMgr.getAccountRepo().findOne(acctId);
        users = userFinder.getAccountUsers(acctInfo);
        return users;
    }

    private Set<DuracloudUser> removeUser(Long userId,
                                          Set<DuracloudUser> users) {
        Set<DuracloudUser> results = new HashSet<DuracloudUser>();
        for (DuracloudUser user : users) {
            if (userId.longValue() != user.getId().longValue()) {
                results.add(user);
            }
        }
        return results;
    }

    private void doPropagate(Long acctId, Set<DuracloudUser> users) {
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
