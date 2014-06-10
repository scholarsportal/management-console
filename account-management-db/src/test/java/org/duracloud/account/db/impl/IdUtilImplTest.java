/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.impl;

import java.util.HashSet;
import java.util.Set;

import org.duracloud.account.db.DuracloudAccountClusterRepo;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudComputeProviderAccountRepo;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.DuracloudServerDetailsRepo;
import org.duracloud.account.db.DuracloudServerImageRepo;
import org.duracloud.account.db.DuracloudStorageProviderAccountRepo;
import org.duracloud.account.db.DuracloudUserInvitationRepo;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.common.web.RestHttpHelper;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: Dec 7, 2010
 */
public class IdUtilImplTest {

    private IdUtilImpl idUtil;

    private static final String host = "host";
    private static final String port = "port";
    private static final String context = "context";

    private RestHttpHelper restHelper;
    private RestHttpHelper.HttpResponse response;

    private DuracloudAccountRepo accountRepo;
    private DuracloudUserInvitationRepo userInvitationRepo;
    private DuracloudInstanceRepo instanceRepo;
    private DuracloudServerImageRepo serverImageRepo;
    private DuracloudComputeProviderAccountRepo computeProviderAccountRepo;
    private DuracloudStorageProviderAccountRepo storageProviderAccountRepo;
    private DuracloudServerDetailsRepo serverDetailsRepo;
    private DuracloudAccountClusterRepo accountClusterRepo;

    private static final int COUNT = 5;

    @Before
    public void setUp() throws Exception {
        restHelper = EasyMock.createMock("RestHttpHelper",
                                         RestHttpHelper.class);
        response = EasyMock.createMock("HttpResponse",
                                       RestHttpHelper.HttpResponse.class);

        accountRepo = createMockAccountRepo(COUNT);
        userInvitationRepo = createMockUserInvitationRepo(COUNT);
        instanceRepo = createMockInstanceRepo(COUNT);
        serverImageRepo = createMockServerImageRepo(COUNT);
        computeProviderAccountRepo = createMockComputeProviderAccountRepo(COUNT);
        storageProviderAccountRepo = createMockStorageProviderAccountRepo(COUNT);
        serverDetailsRepo = createMockServerDetailsRepo(COUNT);
        accountClusterRepo = createMockAccountClusterRepo(COUNT);

        idUtil = new IdUtilImpl();
    }

    private void initialize() {
        idUtil.initialize(host,
                          port,
                          context,
                          restHelper,
                          accountRepo,
                          userInvitationRepo,
                          instanceRepo,
                          serverImageRepo,
                          computeProviderAccountRepo,
                          storageProviderAccountRepo,
                          serverDetailsRepo,
                          accountClusterRepo);
    }

    private void replayMocks() {
        EasyMock.replay(restHelper,
                        response,
                        accountRepo,
                        userInvitationRepo,
                        instanceRepo,
                        serverImageRepo,
                        computeProviderAccountRepo,
                        storageProviderAccountRepo,
                        serverDetailsRepo,
                        accountClusterRepo);
    }

    private DuracloudUserInvitationRepo createMockUserInvitationRepo(int count) {
        DuracloudUserInvitationRepo repo =
            EasyMock.createMock(DuracloudUserInvitationRepo.class);
        EasyMock.expect(repo.getIds()).andReturn(createIds(count));
        return repo;
    }

    private DuracloudAccountRepo createMockAccountRepo(int count) {
        DuracloudAccountRepo repo =
            EasyMock.createMock(DuracloudAccountRepo.class);
        EasyMock.expect(repo.getIds()).andReturn(createIds(count));
        return repo;
    }

    private DuracloudInstanceRepo createMockInstanceRepo(int count) {
        DuracloudInstanceRepo repo =
            EasyMock.createMock(DuracloudInstanceRepo.class);
        EasyMock.expect(repo.getIds()).andReturn(createIds(count));
        return repo;
    }

    private DuracloudServerImageRepo createMockServerImageRepo(int count) {
        DuracloudServerImageRepo repo =
            EasyMock.createMock(DuracloudServerImageRepo.class);
        EasyMock.expect(repo.getIds()).andReturn(createIds(count));
        return repo;
    }

    private DuracloudComputeProviderAccountRepo createMockComputeProviderAccountRepo(int count) {
        DuracloudComputeProviderAccountRepo repo =
            EasyMock.createMock(DuracloudComputeProviderAccountRepo.class);
        EasyMock.expect(repo.getIds()).andReturn(createIds(count));
        return repo;
    }

    private DuracloudStorageProviderAccountRepo createMockStorageProviderAccountRepo(int count) {
        DuracloudStorageProviderAccountRepo repo =
            EasyMock.createMock(DuracloudStorageProviderAccountRepo.class);
        EasyMock.expect(repo.getIds()).andReturn(createIds(count));
        return repo;
    }


    private DuracloudServerDetailsRepo createMockServerDetailsRepo(int count) {
        DuracloudServerDetailsRepo repo =
            EasyMock.createMock(DuracloudServerDetailsRepo.class);
        EasyMock.expect(repo.getIds()).andReturn(createIds(count));
        return repo;
    }

    private DuracloudAccountClusterRepo createMockAccountClusterRepo(int count) {
        DuracloudAccountClusterRepo repo =
            EasyMock.createMock(DuracloudAccountClusterRepo.class);
        EasyMock.expect(repo.getIds()).andReturn(createIds(count));
        return repo;
    }

    private Set<Integer> createIds(int count) {
        Set<Integer> ids = new HashSet<Integer>();
        for (int i = 0; i < count; ++i) {
            ids.add(i);
        }
        return ids;
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(restHelper);
        EasyMock.verify(response);
        EasyMock.verify(accountRepo);
        EasyMock.verify(userInvitationRepo);
        EasyMock.verify(instanceRepo);
        EasyMock.verify(serverImageRepo);
        EasyMock.verify(storageProviderAccountRepo);
        EasyMock.verify(serverDetailsRepo);
        EasyMock.verify(accountClusterRepo);
    }

    @Test
    public void testNewUserId() throws Exception {
        Integer id = 7;
        createMocks(id.toString(), "user");
        replayMocks();
        initialize();

        Integer result = idUtil.newUserId();
        Assert.assertEquals(id, result);
    }

    @Test
    public void testNewUserIdError() throws Exception {
        createMocks("not a number", "user");
        replayMocks();
        initialize();

        boolean thrown = false;
        try {
            idUtil.newUserId();
            Assert.fail("exception expected");
        } catch (DuraCloudRuntimeException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    @Test
    public void testNewRightsId() throws Exception {
        Integer id = 7;
        createMocks(id.toString(), "rights");
        replayMocks();
        initialize();

        Integer result = idUtil.newRightsId();
        Assert.assertEquals(id, result);
    }

    @Test
    public void testNewRightsIdError() throws Exception {
        createMocks("not a number", "rights");
        replayMocks();
        initialize();

        boolean thrown = false;
        try {
            idUtil.newRightsId();
            Assert.fail("exception expected");
        } catch (DuraCloudRuntimeException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    private void createMocks(String id, String resource) throws Exception {
        EasyMock.expect(response.getResponseBody()).andReturn(id);
        EasyMock.expect(restHelper.post("http://" + host + ":" + port + "/" + context + "/id/" + resource,
                                        null,
                                        null)).andReturn(response);
    }


    @Test
    public void testNewAccountId() throws Exception {
        replayMocks();
        initialize();
        Assert.assertEquals(COUNT, idUtil.newAccountId());
    }

    @Test
    public void testNewUserInvitationId() throws Exception {
        replayMocks();
        initialize();
        Assert.assertEquals(COUNT, idUtil.newUserInvitationId());
    }

    @Test
    public void testNewInstanceInvitationId() throws Exception {
        replayMocks();
        initialize();
        Assert.assertEquals(COUNT, idUtil.newInstanceId());
    }

    @Test
    public void testNewServerImageId() throws Exception {
        replayMocks();
        initialize();
        Assert.assertEquals(COUNT, idUtil.newInstanceId());
    }

    @Test
    public void testNewProviderAccountId() throws Exception {
        replayMocks();
        initialize();
        Assert.assertEquals(COUNT, idUtil.newInstanceId());
    }


    @Test
    public void testNewServerDetailsId() throws Exception {
        replayMocks();
        initialize();
        Assert.assertEquals(COUNT, idUtil.newServerDetailsId());
    }

    @Test
    public void testNewAccountClusterId() throws Exception {
        replayMocks();
        initialize();
        Assert.assertEquals(COUNT, idUtil.newAccountClusterId());
    }

}
