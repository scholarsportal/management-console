/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.usermgmt.impl;

import org.duracloud.account.db.repo.DuracloudAccountClusterRepo;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.model.*;
import org.duracloud.account.db.util.DuracloudInstanceManagerService;
import org.duracloud.account.db.util.DuracloudInstanceService;
import org.duracloud.account.db.util.usermgmt.UserDetailsPropagator;
import org.duracloud.account.db.util.util.AccountClusterUtil;
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

    private DuracloudRepoMgr repoMgr;
    private DuracloudInstanceManagerService instanceManagerService;
    private AccountClusterUtil accountClusterUtil;

    private Exception error = null;

    public UserDetailsPropagatorImpl(DuracloudRepoMgr repoMgr,
                                     DuracloudInstanceManagerService instanceManagerService,
                                     AccountClusterUtil accountClusterUtil) {
        this.repoMgr = repoMgr;
        this.instanceManagerService = instanceManagerService;
        this.accountClusterUtil = accountClusterUtil;
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

    @Override
    public void propagateClusterUpdate(Long acctId, Long clusterId) {
        Set<DuracloudUser> users = findUsers(acctId);
        doPropagate(acctId, users);
        checkForErrors(acctId, clusterId, "clusterId");
    }

    @Override
    public void propagateClusterUpdate(Long clusterId) {
        DuracloudAccountClusterRepo clusterRepo =
            repoMgr.getAccountClusterRepo();
        AccountCluster cluster =  clusterRepo.findOne(clusterId);
        Set<AccountInfo> clusterAccts = cluster.getClusterAccounts();
        if(null != clusterAccts && clusterAccts.size() > 0) {
            propagateClusterUpdate(clusterAccts.iterator().next().getId(),
                                   clusterId);
        } else {
            log.info("No accounts found within cluster with ID: " +
                     clusterId + ". No propagation necessary.");
        }
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
        users = accountClusterUtil.getAccountClusterUsers(acctInfo);
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

//    private Set<DuracloudUser> updateUserRoles(Long userId,
//                                               Long acctId,
//                                               Set<Role> roles,
//                                               Set<DuracloudUser> users) {
//        Set<DuracloudUser> results = new HashSet<DuracloudUser>();
//        for (DuracloudUser user : users) {
//            if (userId == user.getId()) {
//                Set<AccountRights> userRights = new HashSet<AccountRights>();
//                userRights.add(new AccountRights(-1L, acctId, userId, roles));
//                user.setAccountRights(userRights);
//            }
//
//            results.add(user);
//        }
//        return results;
//    }

    private void doPropagate(Long acctId, Set<DuracloudUser> users) {
        log.debug("propagating user roles for acct: " + acctId);

        Set<DuracloudInstanceService> services =
            instanceManagerService.getClusterInstanceServices(acctId);
        for (DuracloudInstanceService service : services) {
            DuracloudInstance instanceInfo = service.getInstanceInfo();
            log.debug("propagating user roles: {}, {}",
                      instanceInfo.getHostName(),
                      users);

            service.setUserRoles(users);
        }
    }

}
