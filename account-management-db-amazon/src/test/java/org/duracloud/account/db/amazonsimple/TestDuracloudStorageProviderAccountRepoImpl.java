/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.storage.domain.StorageProviderType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Bill Branan
 * Date: Feb 2, 2011
 */
public class TestDuracloudStorageProviderAccountRepoImpl extends BaseTestDuracloudRepoImpl {

    private DuracloudStorageProviderAccountRepoImpl providerAccountRepo;

    private static final String DOMAIN = "TEST_DURACLOUD_STORAGE_PROVIDER_ACCOUNTS";

    private static final StorageProviderType providerType =
        StorageProviderType.AMAZON_S3;
    private static final String username = "username";
    private static final String password = "password";
    private static final boolean rrs = true;

    @Before
    public void setUp() throws Exception {
        providerAccountRepo = createProviderAccountRepo();
    }

    private static DuracloudStorageProviderAccountRepoImpl createProviderAccountRepo()
        throws Exception {
        return new DuracloudStorageProviderAccountRepoImpl(getDBManager(), DOMAIN);
    }

    @After
    public void tearDown() throws Exception {
        for(Integer itemId : providerAccountRepo.getItemIds()) {
            providerAccountRepo.delete(itemId);
        }
        verifyRepoSize(providerAccountRepo, 0);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        createProviderAccountRepo().removeDomain();
    }

    @Test
    public void testNotFound() {
        boolean thrown = false;
        try {
            providerAccountRepo.findById(-100);
            Assert.fail("exception expected");

        } catch (DBNotFoundException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testGetIds() throws Exception {
        StorageProviderAccount providerAcct0 = createProviderAccount(0);
        StorageProviderAccount providerAcct1 = createProviderAccount(1);
        StorageProviderAccount providerAcct2 = createProviderAccount(2);

        providerAccountRepo.save(providerAcct0);
        providerAccountRepo.save(providerAcct1);
        providerAccountRepo.save(providerAcct2);

        List<Integer> expectedIds = new ArrayList<Integer>();
        expectedIds.add(providerAcct0.getId());
        expectedIds.add(providerAcct1.getId());
        expectedIds.add(providerAcct2.getId());

        new DBCaller<Integer>() {
            protected Integer doCall() throws Exception {
                return providerAccountRepo.getIds().size();
            }
        }.call(expectedIds.size());

        verifyAccount(providerAcct0);
        verifyAccount(providerAcct1);
        verifyAccount(providerAcct2);

        // test concurrency
        verifyCounter(providerAcct0, 1);

        StorageProviderAccount providerAcct = null;
        while (null == providerAcct) {
            providerAcct = providerAccountRepo.findById(providerAcct0.getId());
        }
        Assert.assertNotNull(providerAcct);

        boolean thrown = false;
        try {
            providerAccountRepo.save(providerAcct);
            providerAccountRepo.save(providerAcct);
            providerAccountRepo.save(providerAcct);
            providerAccountRepo.save(providerAcct);

        } catch (DBConcurrentUpdateException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);

        verifyCounter(providerAcct0, 2);
    }

    @Test
    public void testDelete() throws Exception {
        StorageProviderAccount providerAcct = createProviderAccount(0);
        providerAccountRepo.save(providerAcct);
        verifyRepoSize(providerAccountRepo, 1);

        providerAccountRepo.delete(providerAcct.getId());
        verifyRepoSize(providerAccountRepo, 0);
    }

    private StorageProviderAccount createProviderAccount(int id) {
        return new StorageProviderAccount(id,
                                          providerType,
                                          username,
                                          password,
                                          rrs);
    }

    private void verifyAccount(final StorageProviderAccount providerAcct) {
        new DBCaller<StorageProviderAccount>() {
            protected StorageProviderAccount doCall() throws Exception {
                return providerAccountRepo.findById(providerAcct.getId());
            }
        }.call(providerAcct);
    }

    private void verifyCounter(final StorageProviderAccount providerAcct, final int counter) {
        new DBCaller<Integer>() {
            protected Integer doCall() throws Exception {
                return providerAccountRepo.findById(providerAcct.getId()).getCounter();
            }
        }.call(counter);
    }

}
