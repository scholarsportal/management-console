/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import org.duracloud.account.common.domain.DuracloudGroup;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: Nov 12, 2011
 */
public class TestDuracloudGroupRepoImpl extends BaseTestDuracloudRepoImpl {

    private DuracloudGroupRepoImpl groupRepo;

    private static final String DOMAIN = "TEST_DURACLOUD_GROUPS";

    private static final String groupPrefix = DuracloudGroup.PREFIX;
    private static Set<Integer> userIds;

    @BeforeClass
    public static void initialize() throws Exception {
        userIds = new HashSet<Integer>();
        userIds.add(6);
        userIds.add(3);
        userIds.add(9);
    }

    @Before
    public void setUp() throws Exception {
        groupRepo = createGroupRepo();
    }

    private static DuracloudGroupRepoImpl createGroupRepo() throws Exception {
        return new DuracloudGroupRepoImpl(getDBManager(), DOMAIN);
    }

    @After
    public void tearDown() throws Exception {
        for (Integer itemId : groupRepo.getItemIds()) {
            groupRepo.delete(itemId);
        }
        verifyRepoSize(groupRepo, 0);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        createGroupRepo().removeDomain();
    }

    @Test
    public void testNotFound() {
        boolean thrown = false;
        try {
            groupRepo.findById(-100);
            Assert.fail("exception expected");

        } catch (DBNotFoundException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testGetIds() throws Exception {
        DuracloudGroup group0 = createGroup(0, groupPrefix + "0");
        DuracloudGroup group1 = createGroup(1, groupPrefix + "1");
        DuracloudGroup group2 = createGroup(2, groupPrefix + "2");

        groupRepo.save(group0);
        groupRepo.save(group1);
        groupRepo.save(group2);

        List<String> expectedNames = new ArrayList<String>();
        expectedNames.add(group0.getName());
        expectedNames.add(group1.getName());
        expectedNames.add(group2.getName());

        new DBCaller<Integer>() {
            protected Integer doCall() throws Exception {
                return groupRepo.getIds().size();
            }
        }.call(expectedNames.size());

        verifyGroup(group0);
        verifyGroup(group1);
        verifyGroup(group2);

        verifyGroupByName(group0);
        verifyGroupByName(group1);
        verifyGroupByName(group2);

        // test concurrency
        verifyCounter(group0, 1);

        DuracloudGroup group = null;
        while (null == group) {
            group = groupRepo.findById(group0.getId());
        }
        Assert.assertNotNull(group);

        boolean thrown = false;
        try {
            groupRepo.save(group);
            groupRepo.save(group);
            groupRepo.save(group);
            groupRepo.save(group);

        } catch (DBConcurrentUpdateException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);

        verifyCounter(group0, 2);
    }

    @Test
    public void testFindAllGroups() throws DBConcurrentUpdateException {
        DuracloudGroup group6 = createGroup(6, groupPrefix + "6");
        DuracloudGroup group7 = createGroup(7, groupPrefix + "7");
        DuracloudGroup group8 = createGroup(8, groupPrefix + "8");

        groupRepo.save(group6);
        groupRepo.save(group7);
        groupRepo.save(group8);

        Set<DuracloudGroup> expectedGroups = new HashSet<DuracloudGroup>();
        expectedGroups.add(group6);
        expectedGroups.add(group7);
        expectedGroups.add(group8);

        Object result = new DBCaller<Set<DuracloudGroup>>() {
            protected Set<DuracloudGroup> doCall() throws Exception {
                return groupRepo.findAllGroups();
            }
        }.call(expectedGroups);

        Set<DuracloudGroup> groups = (Set<DuracloudGroup>) result;
        Assert.assertEquals(expectedGroups.size(), groups.size());

        for (DuracloudGroup expectedGroup : expectedGroups) {
            Assert.assertTrue(groups.contains(expectedGroup));
        }
    }

    @Test
    public void testDelete() throws Exception {
        DuracloudGroup group0 = createGroup(0, groupPrefix + "0");
        groupRepo.save(group0);
        verifyRepoSize(groupRepo, 1);

        groupRepo.delete(group0.getId());
        verifyRepoSize(groupRepo, 0);
    }

    private DuracloudGroup createGroup(int id, String name) {
        return new DuracloudGroup(id, name, userIds);
    }

    private void verifyGroup(final DuracloudGroup group) {
        new DBCaller<DuracloudGroup>() {
            protected DuracloudGroup doCall() throws Exception {
                return groupRepo.findById(group.getId());
            }
        }.call(group);
    }

    private void verifyGroupByName(final DuracloudGroup group) {
        new DBCaller<DuracloudGroup>() {
            protected DuracloudGroup doCall() throws Exception {
                return groupRepo.findByGroupname(group.getName());
            }
        }.call(group);
    }

    private void verifyCounter(final DuracloudGroup group, final int counter) {
        new DBCaller<Integer>() {
            protected Integer doCall() throws Exception {
                return groupRepo.findById(group.getId()).getCounter();
            }
        }.call(counter);
    }

}
