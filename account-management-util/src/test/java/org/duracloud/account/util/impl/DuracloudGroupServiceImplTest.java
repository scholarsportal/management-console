/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */

package org.duracloud.account.util.impl;

import java.util.HashSet;
import java.util.Set;

import org.duracloud.account.common.domain.DuracloudGroup;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.util.error.DuracloudGroupAlreadyExistsException;
import org.duracloud.account.util.error.DuracloudGroupNotFoundException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Daniel Bernstein Date: Nov 11, 2011
 * 
 */
public class DuracloudGroupServiceImplTest {

    private DuracloudGroupServiceImpl service;
    private String defaultGroupName = "group1";

    @Before
    public void setUp() throws Exception {
        service = new DuracloudGroupServiceImpl();
        service.createGroup(defaultGroupName);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetGroups() {
        Assert.assertEquals(1, service.getGroups().size());
    }

    @Test
    public void testGetGroup() throws Exception {
        Assert.assertNotNull(service.getGroup(defaultGroupName));
    }

    @Test
    public void testGetGroupNotFound() {
        try {
            Assert.assertNotNull(service.getGroup("test"));
            Assert.assertTrue(false);
        } catch (DuracloudGroupNotFoundException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testCreateGroup() throws Exception {
        DuracloudGroup group = service.createGroup("group");
        Assert.assertEquals("group", group.getName());
    }

    @Test
    public void testCreateGroupAlreadyExists()
        throws DBConcurrentUpdateException {
        try {
            Assert.assertNotNull(service.createGroup(defaultGroupName));
            Assert.assertTrue(false);
        } catch (DuracloudGroupAlreadyExistsException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testDeleteGroup() throws DBConcurrentUpdateException {
        Assert.assertEquals(1, service.getGroups().size());
        service.deleteGroup(defaultGroupName);
        Assert.assertEquals(0, service.getGroups().size());
    }

    @Test
    public void testUpdateGroupUsers() throws Exception {
        DuracloudGroup g = service.getGroup(defaultGroupName);
        Assert.assertNull(g.getUsers());
        DuracloudUser user =
            new DuracloudUser(0,
                              "username",
                              "password",
                              "first",
                              "last",
                              "email",
                              "question",
                              "answer");

        Set<DuracloudUser>  users = new HashSet<DuracloudUser>();
        users.add(user);
        service.updateGroupUsers(defaultGroupName, users);
        Assert.assertEquals(1,service.getGroup(defaultGroupName).getUsers().size());
        service.updateGroupUsers(defaultGroupName, new HashSet<DuracloudUser>());
        Assert.assertEquals(0,service.getGroup(defaultGroupName).getUsers().size());
    }

}
