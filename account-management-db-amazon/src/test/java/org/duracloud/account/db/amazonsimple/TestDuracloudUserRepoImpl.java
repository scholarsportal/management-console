/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.amazonsimple;

import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.common.model.Credential;
import org.duracloud.storage.domain.StorageProviderType;
import org.duracloud.unittestdb.UnitTestDatabaseUtil;
import org.duracloud.unittestdb.domain.ResourceType;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;

/**
 * @author Andrew Woods
 *         Date: Oct 8, 2010
 */
public class TestDuracloudUserRepoImpl {

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
        Credential cred = getCredential();
        AmazonSimpleDBClientMgr mgr = new AmazonSimpleDBClientMgr(cred.getUsername(),
                                                                  cred.getPassword());
        return new DuracloudUserRepoImpl(mgr, DOMAIN);
    }

    private static Credential getCredential() throws Exception {
        UnitTestDatabaseUtil dbUtil = new UnitTestDatabaseUtil();
        Credential s3Credential = dbUtil.findCredentialForResource(ResourceType.fromStorageProviderType(
            StorageProviderType.AMAZON_S3));
        assertNotNull(s3Credential);
        assertNotNull(s3Credential.getUsername());
        assertNotNull(s3Credential.getPassword());

        return s3Credential;
    }

    @AfterClass
    public static void afterClass() throws Exception {
        createUserRepo().removeDomain();
    }

    @Test
    public void testNotFound() {
        boolean thrown = false;
        try {
            userRepo.findById("not-found");
            Assert.fail("exception expected");

        } catch (DBNotFoundException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testGetIds() throws Exception {
        DuracloudUser user0 = createUser(username + "0");
        DuracloudUser user1 = createUser(username + "1");
        DuracloudUser user2 = createUser(username + "2");

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

        // test concurrency
        verifyCounter(user0, 1);

        DuracloudUser user = null;
        while (null == user) {
            user = userRepo.findById(user0.getUsername());
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

    private DuracloudUser createUser(String name) {
        return new DuracloudUser(name, password, firstName, lastName, email);
    }

    private void verifyUser(final DuracloudUser user) {
        new DBCaller<DuracloudUser>() {
            protected DuracloudUser doCall() throws Exception {
                return userRepo.findById(user.getUsername());
            }
        }.call(user);
    }

    private void verifyCounter(final DuracloudUser user, final int counter) {
        new DBCaller<Integer>() {
            protected Integer doCall() throws Exception {
                return userRepo.findById(user.getUsername()).getCounter();
            }
        }.call(counter);
    }

    private static abstract class DBCaller<T> {

        public void call(T expected) {
            boolean callComplete = false;
            int maxTries = 20;
            int tries = 0;

            while (!callComplete && tries < maxTries) {
                try {
                    callComplete = expected.equals(doCall());

                } catch (DBException dbe) {
                    callComplete = true;

                } catch (Exception e) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        // do nothing
                    }
                }
                tries++;
            }
            Assert.assertTrue(
                expected + " not found after " + tries + " tries.",
                callComplete);
        }

        protected abstract T doCall() throws Exception;
    }
}
