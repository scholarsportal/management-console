/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.DuracloudGroup;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.error.DuracloudGroupAlreadyExistsException;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: 11/12/11
 */
public class DuracloudGroupServiceImplTest extends DuracloudServiceTestBase {

    private DuracloudGroupServiceImpl groupService;

    private Set<DuracloudGroup> groups;

    @Before
    public void setUp() throws Exception {
        super.before();

        groupService = new DuracloudGroupServiceImpl(repoMgr);
        groups = new HashSet<DuracloudGroup>();
        for (int i = 0; i < NUM_GROUPS; ++i) {
            groups.add(createGroup(i));
        }
    }

    private DuracloudGroup createGroup(int i) {
        return new DuracloudGroup(i, DuracloudGroup.PREFIX + i, null);
    }

    @Test
    public void testGetGroups() throws Exception {
        createGetGroupsMocks();

        Set<DuracloudGroup> groupsFound = groupService.getGroups();
        Assert.assertNotNull(groupsFound);
        Assert.assertEquals(groups, groupsFound);
    }

    private void createGetGroupsMocks() throws DBNotFoundException {
        EasyMock.expect(groupRepo.findAllGroups()).andReturn(groups);
        replayMocks();
    }

    @Test
    public void testGetGroup() throws Exception {
        DuracloudGroup groupExpected = createGetGroupMocks();

        DuracloudGroup groupFound =
            groupService.getGroup(groupExpected.getName());
        Assert.assertNotNull(groupFound);
        Assert.assertEquals(groupExpected, groupFound);
    }

    private DuracloudGroup createGetGroupMocks() throws DBNotFoundException {
        DuracloudGroup group = createGroup(0);
        EasyMock.expect(groupRepo.findByGroupname(group.getName())).andReturn(
            group);
        replayMocks();

        return group;
    }

    @Test
    public void testCreateGroup() throws Exception {
        boolean exists = false;
        DuracloudGroup groupExpected = createCreateGroupMocks(exists);

        DuracloudGroup group =
            groupService.createGroup(groupExpected.getName());
        Assert.assertNotNull(group);
        Assert.assertEquals(groupExpected.getName(), group.getName());
    }

    @Test
    public void testCreateGroupExists() throws Exception {
        boolean exists = true;
        DuracloudGroup groupExpected = createCreateGroupMocks(exists);

        try {
            groupService.createGroup(groupExpected.getName());
            Assert.fail("exception expected");

        } catch (DuracloudGroupAlreadyExistsException e) {
            Assert.assertNotNull(e.getMessage());
            Assert.assertTrue(e.getMessage().contains(groupExpected.getName()));
        }
    }

    private DuracloudGroup createCreateGroupMocks(boolean exists)
        throws DBNotFoundException, DBConcurrentUpdateException {
        DuracloudGroup group = createGroup(0);
        if (exists) {
            EasyMock.expect(groupRepo.findByGroupname(group.getName()))
                    .andReturn(group);

        } else {
            EasyMock.expect(idUtil.newGroupId()).andReturn(3);
            EasyMock.expect(groupRepo.findByGroupname(group.getName()))
                    .andThrow(new DBNotFoundException("canned exception"));
            groupRepo.save(EasyMock.<DuracloudGroup>anyObject());
            EasyMock.expectLastCall();
        }

        replayMocks();
        return group;
    }

    @Test
    public void testDeleteGroupNull() throws Exception {
        replayMocks();
        groupService.deleteGroup(null);
    }

    @Test
    public void testDeleteGroup() throws Exception {
        int groupId = 0;
        DuracloudGroup group = createDeleteGroupMocks(groupId);

        groupService.deleteGroup(group);
    }

    private DuracloudGroup createDeleteGroupMocks(int groupId) {
        groupRepo.delete(groupId);
        EasyMock.expectLastCall();

        replayMocks();
        return createGroup(0);
    }

    @Test
    public void testUpdateGroupUsers() throws Exception {
        int userId = 5;
        DuracloudUser user = newDuracloudUser(userId, "username");
        Set<DuracloudUser> users = new HashSet<DuracloudUser>();
        users.add(user);

        DuracloudGroup group = createUpdateGroupUsersMocks();
        Assert.assertEquals(0, group.getUserIds().size());

        groupService.updateGroupUsers(group, users);
        Set<Integer> userIds = group.getUserIds();
        Assert.assertNotNull(userIds);
        Assert.assertEquals(1, userIds.size());
        Assert.assertEquals(userId, userIds.iterator().next().intValue());
    }

    private DuracloudGroup createUpdateGroupUsersMocks()
        throws DBConcurrentUpdateException {
        int groupId = 0;
        DuracloudGroup group = createGroup(groupId);

        groupRepo.save(group);
        EasyMock.expectLastCall();
        replayMocks();
        return group;
    }
}
