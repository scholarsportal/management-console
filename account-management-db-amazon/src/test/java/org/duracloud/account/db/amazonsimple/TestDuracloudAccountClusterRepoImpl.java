/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import org.duracloud.account.common.domain.AccountCluster;
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
 * Date: 2/16/12
 */
public class TestDuracloudAccountClusterRepoImpl extends BaseTestDuracloudRepoImpl {

    private DuracloudAccountClusterRepoImpl accountClusterRepo;

    private static final String DOMAIN = "TEST_DURACLOUD_ACCOUNT_CLUSTER";

    private static final String clusterName = "cluster-name";
    private static Set<Integer> clusterAccountIds = null;

    @BeforeClass
    public static void initialize() throws Exception {
        clusterAccountIds = new HashSet<Integer>();
        clusterAccountIds.add(1);
        clusterAccountIds.add(2);
    }

    @Before
    public void setUp() throws Exception {
        accountClusterRepo = createAccountClusterRepo();
    }

    private static DuracloudAccountClusterRepoImpl createAccountClusterRepo()
        throws Exception {
        return new DuracloudAccountClusterRepoImpl(getDBManager(), DOMAIN);
    }

    @After
    public void tearDown() throws Exception {
        for(Integer itemId : accountClusterRepo.getItemIds()) {
            accountClusterRepo.delete(itemId);
        }
        verifyRepoSize(accountClusterRepo, 0);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        createAccountClusterRepo().removeDomain();
    }

    @Test
    public void testNotFound() {
        boolean thrown = false;
        try {
            accountClusterRepo.findById(-100);
            Assert.fail("exception expected");

        } catch (DBNotFoundException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testGetIds() throws Exception {
        AccountCluster cluster0 = createAccountCluster(0);
        AccountCluster cluster1 = createAccountCluster(1);
        AccountCluster cluster2 = createAccountCluster(2);

        accountClusterRepo.save(cluster0);
        accountClusterRepo.save(cluster1);
        accountClusterRepo.save(cluster2);

        List<Integer> expectedIds = new ArrayList<Integer>();
        expectedIds.add(cluster0.getId());
        expectedIds.add(cluster1.getId());
        expectedIds.add(cluster2.getId());

        new DBCaller<Integer>() {
            protected Integer doCall() throws Exception {
                return accountClusterRepo.getIds().size();
            }
        }.call(expectedIds.size());

        verifyAccountCluster(cluster0);
        verifyAccountCluster(cluster1);
        verifyAccountCluster(cluster2);

        // test concurrency
        verifyCounter(cluster0, 1);

        AccountCluster cluster = null;
        while (null == cluster) {
            cluster = accountClusterRepo.findById(cluster0.getId());
        }
        Assert.assertNotNull(cluster);

        boolean thrown = false;
        try {
            accountClusterRepo.save(cluster);
            accountClusterRepo.save(cluster);
            accountClusterRepo.save(cluster);
            accountClusterRepo.save(cluster);

        } catch (DBConcurrentUpdateException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);

        verifyCounter(cluster0, 2);
    }

    @Test
    public void testDelete() throws Exception {
        AccountCluster cluster0 = createAccountCluster(0);
        accountClusterRepo.save(cluster0);
        verifyRepoSize(accountClusterRepo, 1);

        accountClusterRepo.delete(cluster0.getId());
        verifyRepoSize(accountClusterRepo, 0);
    }

    private AccountCluster createAccountCluster(int id) {
        return new AccountCluster(id,
                                  clusterName,
                                  clusterAccountIds);
    }

    private void verifyAccountCluster(final AccountCluster cluster) {
        new DBCaller<AccountCluster>() {
            protected AccountCluster doCall() throws Exception {
                return accountClusterRepo.findById(cluster.getId());
            }
        }.call(cluster);
    }

    private void verifyCounter(final AccountCluster cluster, final int counter) {
        new DBCaller<Integer>() {
            protected Integer doCall() throws Exception {
                return accountClusterRepo.findById(cluster.getId()).getCounter();
            }
        }.call(counter);
    }

}
