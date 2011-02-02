/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import org.duracloud.account.common.domain.ServerImage;
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
 * @author: Bill Branan
 * Date: Feb 2, 2011
 */
public class TestDuracloudServerImageRepoImpl extends BaseTestDuracloudRepoImpl {

    private DuracloudServerImageRepoImpl serverImageRepo;

    private static final String DOMAIN = "TEST_DURACLOUD_SERVER_IMAGES";

    private static final int providerAccountId = 1;
    private static final String providerImageId = "provider-image-id";
    private static final String version = "version-1";
    private static final String description = "description";

    @Before
    public void setUp() throws Exception {
        serverImageRepo = createServerImageRepo();
    }

    private static DuracloudServerImageRepoImpl createServerImageRepo()
        throws Exception {
        return new DuracloudServerImageRepoImpl(getDBManager(), DOMAIN);
    }

    @After
    public void tearDown() throws Exception {
        for(Integer itemId : serverImageRepo.getItemIds()) {
            serverImageRepo.delete(itemId);
        }
        verifyRepoSize(serverImageRepo, 0);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        createServerImageRepo().removeDomain();
    }

    @Test
    public void testNotFound() {
        boolean thrown = false;
        try {
            serverImageRepo.findById(-100);
            Assert.fail("exception expected");

        } catch (DBNotFoundException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testGetIds() throws Exception {
        ServerImage serverImg0 = createServerImage(0);
        ServerImage serverImg1 = createServerImage(1);
        ServerImage serverImg2 = createServerImage(2);

        serverImageRepo.save(serverImg0);
        serverImageRepo.save(serverImg1);
        serverImageRepo.save(serverImg2);

        List<Integer> expectedIds = new ArrayList<Integer>();
        expectedIds.add(serverImg0.getId());
        expectedIds.add(serverImg1.getId());
        expectedIds.add(serverImg2.getId());

        new DBCaller<Integer>() {
            protected Integer doCall() throws Exception {
                return serverImageRepo.getIds().size();
            }
        }.call(expectedIds.size());

        verifyAccount(serverImg0);
        verifyAccount(serverImg1);
        verifyAccount(serverImg2);

        // test concurrency
        verifyCounter(serverImg0, 1);

        ServerImage serverImg = null;
        while (null == serverImg) {
            serverImg = serverImageRepo.findById(serverImg0.getId());
        }
        Assert.assertNotNull(serverImg);

        boolean thrown = false;
        try {
            serverImageRepo.save(serverImg);
            serverImageRepo.save(serverImg);
            serverImageRepo.save(serverImg);
            serverImageRepo.save(serverImg);

        } catch (DBConcurrentUpdateException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);

        verifyCounter(serverImg0, 2);
    }

    @Test
    public void testDelete() throws Exception {
        ServerImage serverImg0 = createServerImage(0);
        serverImageRepo.save(serverImg0);
        verifyRepoSize(serverImageRepo, 1);

        serverImageRepo.delete(serverImg0.getId());
        verifyRepoSize(serverImageRepo, 0);
    }

    private ServerImage createServerImage(int id) {
        return new ServerImage(id,
                               providerAccountId,
                               providerImageId,
                               version,
                               description);
    }

    private void verifyAccount(final ServerImage serverImg) {
        new DBCaller<ServerImage>() {
            protected ServerImage doCall() throws Exception {
                return serverImageRepo.findById(serverImg.getId());
            }
        }.call(serverImg);
    }

    private void verifyCounter(final ServerImage serverImg, final int counter) {
        new DBCaller<Integer>() {
            protected Integer doCall() throws Exception {
                return serverImageRepo.findById(serverImg.getId()).getCounter();
            }
        }.call(counter);
    }

}
