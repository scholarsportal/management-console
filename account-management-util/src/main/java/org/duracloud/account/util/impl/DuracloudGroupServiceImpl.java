/*
* Copyright (c) 2009-2011 DuraSpace. All rights reserved.
*/

package org.duracloud.account.util.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.duracloud.account.common.domain.DuracloudGroup;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.DuracloudGroupRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.IdUtil;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.DuracloudGroupService;
import org.duracloud.account.util.error.DuracloudGroupAlreadyExistsException;
import org.duracloud.account.util.error.DuracloudGroupNotFoundException;
import org.duracloud.account.util.error.InvalidGroupNameException;
import org.duracloud.account.util.usermgmt.UserDetailsPropagator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Bernstein
 *         Date: Nov 11, 2011
 */
public class DuracloudGroupServiceImpl implements DuracloudGroupService {

    private Logger log =
        LoggerFactory.getLogger(DuracloudGroupServiceImpl.class);

    private DuracloudRepoMgr repoMgr;
    private UserDetailsPropagator propagator;

    public DuracloudGroupServiceImpl(DuracloudRepoMgr duracloudRepoMgr,
                                     UserDetailsPropagator propagator) {
        this.repoMgr = duracloudRepoMgr;
        this.propagator = propagator;
    }

    @Override
    public Set<DuracloudGroup> getGroups() {
        Set<DuracloudGroup> groups;
        try {
            groups = getGroupRepo().findAllGroups();

        } catch (DBNotFoundException e) {
            log.warn("No groups found.");
            groups = new HashSet<DuracloudGroup>();
        }
        return Collections.unmodifiableSet(groups);
    }

    @Override
    public DuracloudGroup getGroup(String name)
        throws DuracloudGroupNotFoundException {
        try {
            return getGroupRepo().findByGroupname(name);

        } catch (DBNotFoundException e) {
            throw new DuracloudGroupNotFoundException(name + " not found");
        }
    }

    @Override
    public DuracloudGroup createGroup(String name, int acctId)
        throws DuracloudGroupAlreadyExistsException, InvalidGroupNameException,
               DBConcurrentUpdateException {
        if (!isGroupNameValid(name)) {
            throw new InvalidGroupNameException(name);
        }

        if (groupExists(name)) {
            throw new DuracloudGroupAlreadyExistsException(name);
        }

        int newGroupId = getIdUtil().newGroupId();
        DuracloudGroup group = new DuracloudGroup(newGroupId, name);
        getGroupRepo().save(group);

        return group;
    }

    /**
     * This method is 'protected' for testing purposes only.
     */
    protected final boolean isGroupNameValid(String name) {
        if (name == null) {
            return false;
        }

        if (!name.startsWith(DuracloudGroup.PREFIX)) {
            return false;
        }

        if (DuracloudGroup.PUBLIC_GROUP_NAME.equalsIgnoreCase(name)) {
            return false;
        }

        return name.substring(DuracloudGroup.PREFIX.length()).matches(
            "\\A(?![_.@\\-])[a-z0-9_.@\\-]+(?<![_.@\\-])\\Z");
    }

    private boolean groupExists(String name) {
        try {
            getGroup(name);
            return true;

        } catch (DuracloudGroupNotFoundException e) {
            return false;
        }
    }

    @Override
    public void deleteGroup(DuracloudGroup group, int acctId)
        throws DBConcurrentUpdateException {
        if (null == group) {
            log.warn("Arg group is null.");
            return;
        }
        getGroupRepo().delete(group.getId());
        propagateUpdate(acctId, group);
    }

    @Override
    public void updateGroupUsers(DuracloudGroup group,
                                 Set<DuracloudUser> users,
                                 int acctId)
        throws DuracloudGroupNotFoundException, DBConcurrentUpdateException {
        Set<Integer> userIds = new HashSet<Integer>();
        for (DuracloudUser user : users) {
            userIds.add(user.getId());
        }

        group.setUserIds(userIds);
        getGroupRepo().save(group);
        propagateUpdate(acctId, group);
    }

    private void propagateUpdate(int acctId, DuracloudGroup group) {
        propagator.propagateGroupUpdate(acctId, group.getId());
    }

    private DuracloudGroupRepo getGroupRepo() {
        return repoMgr.getGroupRepo();
    }

    private IdUtil getIdUtil() {
        return repoMgr.getIdUtil();
    }
}
