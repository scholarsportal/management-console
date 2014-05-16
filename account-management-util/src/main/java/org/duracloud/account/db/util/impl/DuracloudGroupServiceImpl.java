/*
* Copyright (c) 2009-2011 DuraSpace. All rights reserved.
*/

package org.duracloud.account.db.util.impl;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.DuracloudGroup;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.util.DuracloudGroupService;
import org.duracloud.account.db.util.error.DuracloudGroupAlreadyExistsException;
import org.duracloud.account.db.util.error.DuracloudGroupNotFoundException;
import org.duracloud.account.db.util.error.InvalidGroupNameException;
import org.duracloud.account.db.util.usermgmt.UserDetailsPropagator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public Set<DuracloudGroup> getGroups(Long acctId) {
        List<DuracloudGroup > listGroups = repoMgr.getGroupRepo().findByAccountId(acctId);
        Set<DuracloudGroup> groups = new HashSet<DuracloudGroup>();
        groups.addAll(listGroups);
        return Collections.unmodifiableSet(groups);
    }

    @Override
    public DuracloudGroup getGroup(String name, Long acctId) {
        return repoMgr.getGroupRepo().findByNameAndAccountId(name, acctId);
    }

    @Override
    public DuracloudGroup createGroup(String name, Long acctId)
        throws DuracloudGroupAlreadyExistsException, InvalidGroupNameException {
        if (!isGroupNameValid(name)) {
            throw new InvalidGroupNameException(name);
        }

        if (groupExistsInAccount(name, acctId)) {
            throw new DuracloudGroupAlreadyExistsException(name);
        }

        AccountInfo accountInfo = repoMgr.getAccountRepo().findOne(acctId);

        DuracloudGroup group = new DuracloudGroup();
        group.setName(name);
        group.setAccount(accountInfo);
        group = repoMgr.getGroupRepo().save(group);
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

    private boolean groupExistsInAccount(String name, Long acctId) {
        DuracloudGroup group = getGroup(name, acctId);
        return group != null;
    }

    @Override
    public void deleteGroup(DuracloudGroup group, Long acctId) {
        if (null == group) {
            log.warn("Arg group is null.");
            return;
        }
        repoMgr.getGroupRepo().delete(group.getId());
        propagateUpdate(acctId, group);
    }

    @Override
    public void updateGroupUsers(DuracloudGroup group,
                                 Set<DuracloudUser> users,
                                 Long acctId)
        throws DuracloudGroupNotFoundException {
        group.setUsers(users);
        repoMgr.getGroupRepo().save(group);
        propagateUpdate(acctId, group);
    }

    private void propagateUpdate(Long acctId, DuracloudGroup group) {
        propagator.propagateGroupUpdate(acctId, group.getId());
    }

}
