/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.impl;

import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudComputeProviderAccountRepo;
import org.duracloud.account.db.DuracloudGroupRepo;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.DuracloudServerDetailsRepo;
import org.duracloud.account.db.DuracloudStorageProviderAccountRepo;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudServerImageRepo;
import org.duracloud.account.db.DuracloudServiceRepositoryRepo;
import org.duracloud.account.db.DuracloudUserInvitationRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: Dec 7, 2010
 */
public class IdUtilImplTest {

    private IdUtilImpl idUtil;

    private DuracloudAccountRepo accountRepo;
    private DuracloudUserRepo userRepo;
    private DuracloudGroupRepo groupRepo;
    private DuracloudRightsRepo rightsRepo;
    private DuracloudUserInvitationRepo userInvitationRepo;
    private DuracloudInstanceRepo instanceRepo;
    private DuracloudServerImageRepo serverImageRepo;
    private DuracloudComputeProviderAccountRepo computeProviderAccountRepo;
    private DuracloudStorageProviderAccountRepo storageProviderAccountRepo;
    private DuracloudServiceRepositoryRepo serviceRepositoryRepo;
    private DuracloudServerDetailsRepo serverDetailsRepo;

    private static final int COUNT = 5;

    @Before
    public void setUp() throws Exception {
        accountRepo = createMockAccountRepo(COUNT);
        userRepo = createMockUserRepo(COUNT);
        groupRepo = createMockGroupRepo(COUNT);
        rightsRepo = createMockRightsRepo(COUNT);
        userInvitationRepo = createMockUserInvitationRepo(COUNT);
        instanceRepo = createMockInstanceRepo(COUNT);
        serverImageRepo = createMockServerImageRepo(COUNT);
        computeProviderAccountRepo = createMockComputeProviderAccountRepo(COUNT);
        storageProviderAccountRepo = createMockStorageProviderAccountRepo(COUNT);
        serviceRepositoryRepo = createMockServiceRepositoryRepo(COUNT);
        serverDetailsRepo = createMockServerDetailsRepo(COUNT);

        idUtil = new IdUtilImpl();
        idUtil.initialize(userRepo,
                          groupRepo,
                          accountRepo,
                          rightsRepo,
                          userInvitationRepo,
                          instanceRepo,
                          serverImageRepo,
                          computeProviderAccountRepo,
                          storageProviderAccountRepo,
                          serviceRepositoryRepo,
                          serverDetailsRepo);
    }

    private DuracloudUserInvitationRepo createMockUserInvitationRepo(int count) {
        DuracloudUserInvitationRepo repo =
            EasyMock.createMock(DuracloudUserInvitationRepo.class);
        EasyMock.expect(repo.getIds()).andReturn(createIds(count));
        EasyMock.replay(repo);
        return repo;
    }

    private DuracloudAccountRepo createMockAccountRepo(int count) {
        DuracloudAccountRepo repo =
            EasyMock.createMock(DuracloudAccountRepo.class);
        EasyMock.expect(repo.getIds()).andReturn(createIds(count));
        EasyMock.replay(repo);
        return repo;
    }

    private DuracloudUserRepo createMockUserRepo(int count) {
        DuracloudUserRepo repo =
            EasyMock.createMock(DuracloudUserRepo.class);
        EasyMock.expect(repo.getIds()).andReturn(createIds(count));
        EasyMock.replay(repo);
        return repo;
    }

    private DuracloudGroupRepo createMockGroupRepo(int count) {
        DuracloudGroupRepo repo = EasyMock.createMock(DuracloudGroupRepo.class);
        EasyMock.expect(repo.getIds()).andReturn(createIds(count));
        EasyMock.replay(repo);
        return repo;
    }

    private DuracloudRightsRepo createMockRightsRepo(int count) {
        DuracloudRightsRepo repo =
            EasyMock.createMock(DuracloudRightsRepo.class);
        EasyMock.expect(repo.getIds()).andReturn(createIds(count));
        EasyMock.replay(repo);
        return repo;
    }

    private DuracloudInstanceRepo createMockInstanceRepo(int count) {
        DuracloudInstanceRepo repo =
            EasyMock.createMock(DuracloudInstanceRepo.class);
        EasyMock.expect(repo.getIds()).andReturn(createIds(count));
        EasyMock.replay(repo);
        return repo;
    }

    private DuracloudServerImageRepo createMockServerImageRepo(int count) {
        DuracloudServerImageRepo repo =
            EasyMock.createMock(DuracloudServerImageRepo.class);
        EasyMock.expect(repo.getIds()).andReturn(createIds(count));
        EasyMock.replay(repo);
        return repo;
    }

    private DuracloudComputeProviderAccountRepo createMockComputeProviderAccountRepo(int count) {
        DuracloudComputeProviderAccountRepo repo =
            EasyMock.createMock(DuracloudComputeProviderAccountRepo.class);
        EasyMock.expect(repo.getIds()).andReturn(createIds(count));
        EasyMock.replay(repo);
        return repo;
    }

    private DuracloudStorageProviderAccountRepo createMockStorageProviderAccountRepo(int count) {
        DuracloudStorageProviderAccountRepo repo =
            EasyMock.createMock(DuracloudStorageProviderAccountRepo.class);
        EasyMock.expect(repo.getIds()).andReturn(createIds(count));
        EasyMock.replay(repo);
        return repo;
    }

    private DuracloudServiceRepositoryRepo createMockServiceRepositoryRepo(int count) {
        DuracloudServiceRepositoryRepo repo =
            EasyMock.createMock(DuracloudServiceRepositoryRepo.class);
        EasyMock.expect(repo.getIds()).andReturn(createIds(count));
        EasyMock.replay(repo);
        return repo;
    }

    private DuracloudServerDetailsRepo createMockServerDetailsRepo(int count) {
        DuracloudServerDetailsRepo repo =
            EasyMock.createMock(DuracloudServerDetailsRepo.class);
        EasyMock.expect(repo.getIds()).andReturn(createIds(count));
        EasyMock.replay(repo);
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
        EasyMock.verify(accountRepo);
        EasyMock.verify(userRepo);
        EasyMock.verify(groupRepo);
        EasyMock.verify(rightsRepo);
        EasyMock.verify(userInvitationRepo);
        EasyMock.verify(instanceRepo);
        EasyMock.verify(serverImageRepo);
        EasyMock.verify(storageProviderAccountRepo);
        EasyMock.verify(serviceRepositoryRepo);
        EasyMock.verify(serverDetailsRepo);
    }

    @Test
    public void testNewAccountId() throws Exception {
        Assert.assertEquals(COUNT, idUtil.newAccountId());
    }

    @Test
    public void testNewUserId() throws Exception {
        Assert.assertEquals(COUNT, idUtil.newUserId());
    }

    @Test
    public void testNewGroupId() throws Exception {
        Assert.assertEquals(COUNT, idUtil.newGroupId());
    }

    @Test
    public void testNewRightsId() throws Exception {
        Assert.assertEquals(COUNT, idUtil.newRightsId());
    }

    @Test
    public void testNewUserInvitationId() throws Exception {
        Assert.assertEquals(COUNT, idUtil.newUserInvitationId());
    }

    @Test
    public void testNewInstanceInvitationId() throws Exception {
        Assert.assertEquals(COUNT, idUtil.newInstanceId());
    }

    @Test
    public void testNewServerImageId() throws Exception {
        Assert.assertEquals(COUNT, idUtil.newInstanceId());
    }

    @Test
    public void testNewProviderAccountId() throws Exception {
        Assert.assertEquals(COUNT, idUtil.newInstanceId());
    }

    @Test
    public void testNewServiceRepositoryId() throws Exception {
        Assert.assertEquals(COUNT, idUtil.newInstanceId());
    }

    @Test
    public void testNewServerDetailsId() throws Exception {
        Assert.assertEquals(COUNT, idUtil.newServerDetailsId());
    }

}
