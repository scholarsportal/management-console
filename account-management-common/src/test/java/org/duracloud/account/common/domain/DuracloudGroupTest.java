/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Daniel Bernstein
 *         Date: Nov 11, 2011
 */
public class DuracloudGroupTest {

    private DuracloudUser user;
    private DuracloudGroup group;
    
    private String testUsername;

    @Before
    public void setUp() throws Exception {
        
        user = new DuracloudUser(0,
                                 testUsername = "test",
                                 "password",
                                 "first",
                                 "last",
                                 "email",
                                 "question",
                                 "answer",
                                 0);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testNullConstructor() {
        try{
            group = new DuracloudGroup(null);
            Assert.assertTrue(false);
        }catch(IllegalArgumentException ex){
            Assert.assertTrue(true);
        }
    }
 
    @Test
    public void testConsructor(){
        group = new DuracloudGroup("test");
        Assert.assertEquals("test", group.getName());
    }
    
    @Test
    public void testSetDuracloudUser(){
        group = new DuracloudGroup("test");
        List<DuracloudUser> users = new LinkedList<DuracloudUser>();
        users.add(user);
        group.setUsers(users);
        
        Assert.assertEquals(1, group.getUsers().size());
    }

    @Test
    public void testAddUser(){
        group = new DuracloudGroup("test");
        
        group.addUser(user);
        
        Assert.assertEquals(1, group.getUsers().size());
    }

    @Test
    public void testRemoveUser(){
        testSetDuracloudUser();
        DuracloudUser removedUser = group.removeUser(testUsername);
        Assert.assertNotNull(removedUser);
        Assert.assertEquals(testUsername, removedUser.getUsername());
        Assert.assertEquals(0, group.getUsers().size());
        
    }

    @Test
    public void testEquals(){
        DuracloudGroup group1 = new DuracloudGroup("test");
        DuracloudGroup group2 = new DuracloudGroup("test");
        DuracloudGroup group3 = new DuracloudGroup("test1");

        Assert.assertTrue(group1.equals(group2));
        Assert.assertFalse(group2.equals(group3));
        
    }
    
}
