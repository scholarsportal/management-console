/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import junit.framework.Assert;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: Bill Branan
 * Date: Dec 20, 2010
 */
public class TestDuracloudInstanceRepoImpl extends BaseTestDuracloudRepoImpl {

    private DuracloudInstanceRepoImpl instRepo;

    private static final String DOMAIN = "TEST_DURACLOUD_INSTANCES";

    private static final int imageId = 10;
    private static final String hostName = "host";
    private static final String providerInstanceId = "ABCD";
    private static final int computeProviderAccountId = 1;
    private static final int primaryStorageProviderAccountId = 5;
    private static Set<Integer> secondaryStorageProviderAccountIds = null;
    private static Set<Integer> serviceRepositoryIds = null;

    @BeforeClass
    public static void init() {
        secondaryStorageProviderAccountIds = new HashSet<Integer>();
        secondaryStorageProviderAccountIds.add(10);
        secondaryStorageProviderAccountIds.add(15);

        serviceRepositoryIds = new HashSet<Integer>();
        serviceRepositoryIds.add(1);
        serviceRepositoryIds.add(2);
    }

    @Before
    public void setUp() throws Exception {
        instRepo = createInstanceRepo();
    }

    private static DuracloudInstanceRepoImpl createInstanceRepo() throws Exception {
        return new DuracloudInstanceRepoImpl(getDBManager(), DOMAIN);
    }

    @After
    public void tearDown() throws Exception {
        for(Integer itemId : instRepo.getItemIds()) {
            instRepo.delete(itemId);
        }
        verifyRepoSize(instRepo, 0);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        createInstanceRepo().removeDomain();
    }

    @Test
    public void testNotFound() {
        boolean thrown = false;
        try {
            instRepo.findById(-100);
            Assert.fail("exception expected");

        } catch (DBNotFoundException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testGetIds() throws Exception {
        DuracloudInstance inst0 = createInstance(0);
        DuracloudInstance inst1 = createInstance(1);
        DuracloudInstance inst2 = createInstance(2);

        instRepo.save(inst0);
        instRepo.save(inst1);
        instRepo.save(inst2);

        List<Integer> expectedIds = new ArrayList<Integer>();
        expectedIds.add(inst0.getId());
        expectedIds.add(inst1.getId());
        expectedIds.add(inst2.getId());

        new DBCaller<Integer>() {
            protected Integer doCall() throws Exception {
                return instRepo.getIds().size();
            }
        }.call(expectedIds.size());

        verifyInstance(inst0);
        verifyInstance(inst1);
        verifyInstance(inst2);

        // test concurrency
        verifyCounter(inst0, 1);

        DuracloudInstance inst = null;
        while (null == inst) {
            inst = instRepo.findById(inst0.getId());
        }
        Assert.assertNotNull(inst);

        boolean thrown = false;
        try {
            instRepo.save(inst);
            instRepo.save(inst);
            instRepo.save(inst);
            instRepo.save(inst);

        } catch (DBConcurrentUpdateException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);

        verifyCounter(inst0, 2);
    }

    @Test
    public void testDelete() throws Exception {
        DuracloudInstance acct0 = createInstance(0);
        instRepo.save(acct0);
        verifyRepoSize(instRepo, 1);

        instRepo.delete(acct0.getId());
        verifyRepoSize(instRepo, 0);
    }

    private DuracloudInstance createInstance(int id) {
        return new DuracloudInstance(id,
                                     imageId,
                                     hostName,
                                     providerInstanceId,
                                     computeProviderAccountId,
                                     primaryStorageProviderAccountId,
                                     secondaryStorageProviderAccountIds,
                                     serviceRepositoryIds);
    }

    private void verifyInstance(final DuracloudInstance inst) {
        new DBCaller<DuracloudInstance>() {
            protected DuracloudInstance doCall() throws Exception {
                return instRepo.findById(inst.getId());
            }
        }.call(inst);
    }

    private void verifyCounter(final DuracloudInstance inst, final int counter) {
        new DBCaller<Integer>() {
            protected Integer doCall() throws Exception {
                return instRepo.findById(inst.getId()).getCounter();
            }
        }.call(counter);
    }

}
