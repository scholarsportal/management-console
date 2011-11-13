/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

import java.util.HashSet;
import java.util.Set;

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

    private static final String testGroupname = DuracloudGroup.PREFIX + "test";

    @Before
    public void setUp() throws Exception {
        user = new DuracloudUser(0,
                                 testGroupname,
                                 "password",
                                 "first",
                                 "last",
                                 "email",
                                 "question",
                                 "answer",
                                 0);
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
    public void testConstructor() {
        group = new DuracloudGroup(testGroupname);
        Assert.assertEquals(testGroupname, group.getName());
    }

    @Test
    public void testSetDuracloudUser(){
        group = new DuracloudGroup(testGroupname);
        Set<Integer> userIds = new HashSet<Integer>();
        userIds.add(user.getId());
        group.setUserIds(userIds);
        
        Assert.assertEquals(1, group.getUserIds().size());
    }

    @Test
    public void testAddUserId(){
        group = new DuracloudGroup(testGroupname);
        group.addUserId(user.getId());
        Assert.assertEquals(1, group.getUserIds().size());
    }

    @Test
    public void testRemoveUser(){
        testSetDuracloudUser();
        boolean removed = group.removeUserId(user.getId());
        Assert.assertTrue(removed);

        removed = group.removeUserId(user.getId());
        Assert.assertTrue(!removed);

        Assert.assertEquals(0, group.getUserIds().size());
    }

    @Test
    public void testEquals(){
        DuracloudGroup group1 = new DuracloudGroup(testGroupname);
        DuracloudGroup group2 = new DuracloudGroup(testGroupname);
        DuracloudGroup group3 = new DuracloudGroup(testGroupname + 1);

        Assert.assertTrue(group1.equals(group2));
        Assert.assertFalse(group2.equals(group3));
    }
    
}
