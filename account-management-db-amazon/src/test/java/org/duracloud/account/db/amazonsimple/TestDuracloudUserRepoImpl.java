/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Woods
 *         Date: Oct 8, 2010
 */
public class TestDuracloudUserRepoImpl extends BaseTestDuracloudRepoImpl {

    private DuracloudUserRepoImpl userRepo;

    private static final String DOMAIN = "TEST_DURACLOUD_USERS";

    private String username = "username";
    private String password = "password";
    private String firstName = "punky";
    private String lastName = "brewster";
    private String email = "pb@home.com";

    @Before
    public void setUp() throws Exception {
        userRepo = createUserRepo();
    }

    private static DuracloudUserRepoImpl createUserRepo() throws Exception {
        return new DuracloudUserRepoImpl(getDBManager(), DOMAIN);
    }

    @After
    public void tearDown() throws Exception {
        for(Integer itemId : userRepo.getItemIds()) {
            userRepo.delete(itemId);
        }
        verifyRepoSize(userRepo, 0);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        createUserRepo().removeDomain();
    }

    @Test
    public void testNotFound() {
        boolean thrown = false;
        try {
            userRepo.findById(-100);
            Assert.fail("exception expected");

        } catch (DBNotFoundException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testGetIds() throws Exception {
        DuracloudUser user0 = createUser(0, username + "0");
        DuracloudUser user1 = createUser(1, username + "1");
        DuracloudUser user2 = createUser(2, username + "2");

        userRepo.save(user0);
        userRepo.save(user1);
        userRepo.save(user2);

        List<String> expectedIds = new ArrayList<String>();
        expectedIds.add(user0.getUsername());
        expectedIds.add(user1.getUsername());
        expectedIds.add(user2.getUsername());

        new DBCaller<Integer>() {
            protected Integer doCall() throws Exception {
                return userRepo.getIds().size();
            }
        }.call(expectedIds.size());

        verifyUser(user0);
        verifyUser(user1);
        verifyUser(user2);

        verifyUserByName(user0);
        verifyUserByName(user1);
        verifyUserByName(user2);        

        // test concurrency
        verifyCounter(user0, 1);

        DuracloudUser user = null;
        while (null == user) {
            user = userRepo.findById(user0.getId());
        }
        Assert.assertNotNull(user);

        boolean thrown = false;
        try {
            userRepo.save(user);
            userRepo.save(user);
            userRepo.save(user);
            userRepo.save(user);

        } catch (DBConcurrentUpdateException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);

        verifyCounter(user0, 2);
    }

    @Test
    public void testDelete() throws Exception {
        DuracloudUser user0 = createUser(0, username + "0");
        userRepo.save(user0);
        verifyRepoSize(userRepo, 1);

        userRepo.delete(user0.getId());
        verifyRepoSize(userRepo, 0);
    }

    private DuracloudUser createUser(int id, String name) {
        return new DuracloudUser(id, name, password, firstName, lastName, email);
    }

    private void verifyUser(final DuracloudUser user) {
        new DBCaller<DuracloudUser>() {
            protected DuracloudUser doCall() throws Exception {
                return userRepo.findById(user.getId());
            }
        }.call(user);
    }

    private void verifyUserByName(final DuracloudUser user) {
        new DBCaller<DuracloudUser>() {
            protected DuracloudUser doCall() throws Exception {
                return userRepo.findByUsername(user.getUsername());
            }
        }.call(user);
    }

    private void verifyCounter(final DuracloudUser user, final int counter) {
        new DBCaller<Integer>() {
            protected Integer doCall() throws Exception {
                return userRepo.findById(user.getId()).getCounter();
            }
        }.call(counter);
    }

}
