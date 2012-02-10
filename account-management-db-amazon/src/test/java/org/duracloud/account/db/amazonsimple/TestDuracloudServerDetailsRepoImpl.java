/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import org.duracloud.account.common.domain.ServerDetails;
import org.duracloud.account.common.domain.ServicePlan;
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
 * @author: Bill Branan
 * Date: 2/8/12
 */
public class TestDuracloudServerDetailsRepoImpl extends BaseTestDuracloudRepoImpl {

    private DuracloudServerDetailsRepoImpl serverDetailsRepo;

    private static final String DOMAIN = "TEST_DURACLOUD_SERVER_DETAILS";

    private static final int computeProviderAccountId = 1;
    private static final int primaryStorageProviderAccountId = 5;
    private static Set<Integer> secondaryStorageProviderAccountIds = null;
    private static Set<Integer> secondaryServiceRepositoryIds = null;
    private static ServicePlan servicePlan = ServicePlan.PROFESSIONAL;

    @BeforeClass
    public static void initialize() throws Exception {
        secondaryStorageProviderAccountIds = new HashSet<Integer>();
        secondaryStorageProviderAccountIds.add(10);
        secondaryStorageProviderAccountIds.add(15);

        secondaryServiceRepositoryIds = new HashSet<Integer>();
        secondaryServiceRepositoryIds.add(1);
        secondaryServiceRepositoryIds.add(2);
    }

    @Before
    public void setUp() throws Exception {
        serverDetailsRepo = createServerDetailsRepo();
    }

    private static DuracloudServerDetailsRepoImpl createServerDetailsRepo()
        throws Exception {
        return new DuracloudServerDetailsRepoImpl(getDBManager(), DOMAIN);
    }

    @After
    public void tearDown() throws Exception {
        for(Integer itemId : serverDetailsRepo.getItemIds()) {
            serverDetailsRepo.delete(itemId);
        }
        verifyRepoSize(serverDetailsRepo, 0);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        createServerDetailsRepo().removeDomain();
    }

    @Test
    public void testNotFound() {
        boolean thrown = false;
        try {
            serverDetailsRepo.findById(-100);
            Assert.fail("exception expected");

        } catch (DBNotFoundException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testGetIds() throws Exception {
        ServerDetails details0 = createServerDetails(0);
        ServerDetails details1 = createServerDetails(1);
        ServerDetails details2 = createServerDetails(2);

        serverDetailsRepo.save(details0);
        serverDetailsRepo.save(details1);
        serverDetailsRepo.save(details2);

        List<Integer> expectedIds = new ArrayList<Integer>();
        expectedIds.add(details0.getId());
        expectedIds.add(details1.getId());
        expectedIds.add(details2.getId());

        new DBCaller<Integer>() {
            protected Integer doCall() throws Exception {
                return serverDetailsRepo.getIds().size();
            }
        }.call(expectedIds.size());

        verifyServerDetails(details0);
        verifyServerDetails(details1);
        verifyServerDetails(details2);

        // test concurrency
        verifyCounter(details0, 1);

        ServerDetails details = null;
        while (null == details) {
            details = serverDetailsRepo.findById(details0.getId());
        }
        Assert.assertNotNull(details);

        boolean thrown = false;
        try {
            serverDetailsRepo.save(details);
            serverDetailsRepo.save(details);
            serverDetailsRepo.save(details);
            serverDetailsRepo.save(details);

        } catch (DBConcurrentUpdateException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);

        verifyCounter(details0, 2);
    }

    @Test
    public void testDelete() throws Exception {
        ServerDetails details0 = createServerDetails(0);
        serverDetailsRepo.save(details0);
        verifyRepoSize(serverDetailsRepo, 1);

        serverDetailsRepo.delete(details0.getId());
        verifyRepoSize(serverDetailsRepo, 0);
    }

    private ServerDetails createServerDetails(int id) {
        return new ServerDetails(id,
                                 computeProviderAccountId,
                                 primaryStorageProviderAccountId,
                                 secondaryStorageProviderAccountIds,
                                 secondaryServiceRepositoryIds,
                                 servicePlan);
    }

    private void verifyServerDetails(final ServerDetails details) {
        new DBCaller<ServerDetails>() {
            protected ServerDetails doCall() throws Exception {
                return serverDetailsRepo.findById(details.getId());
            }
        }.call(details);
    }

    private void verifyCounter(final ServerDetails details, final int counter) {
        new DBCaller<Integer>() {
            protected Integer doCall() throws Exception {
                return serverDetailsRepo.findById(details.getId()).getCounter();
            }
        }.call(counter);
    }

}
