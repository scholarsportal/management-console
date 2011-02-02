/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.impl;

import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.DuracloudProviderAccountRepo;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudServerImageRepo;
import org.duracloud.account.db.DuracloudServiceRepositoryRepo;
import org.duracloud.account.db.DuracloudUserInvitationRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.easymock.classextension.EasyMock;
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
    private DuracloudRightsRepo rightsRepo;
    private DuracloudUserInvitationRepo userInvitationRepo;
    private DuracloudInstanceRepo instanceRepo;
    private DuracloudServerImageRepo serverImageRepo;
    private DuracloudProviderAccountRepo providerAccountRepo;
    private DuracloudServiceRepositoryRepo serviceRepositoryRepo;

    private static final int COUNT = 5;

    @Before
    public void setUp() throws Exception {
        accountRepo = createMockAccountRepo(COUNT);
        userRepo = createMockUserRepo(COUNT);
        rightsRepo = createMockRightsRepo(COUNT);
        userInvitationRepo = createMockUserInvitationRepo(COUNT);
        instanceRepo = createMockInstanceRepo(COUNT);
        serverImageRepo = createMockServerImageRepo(COUNT);
        providerAccountRepo = createMockProviderAccountRepo(COUNT);
        serviceRepositoryRepo = createMockServiceRepositoryRepo(COUNT);

        idUtil = new IdUtilImpl();
        idUtil.initialize(userRepo,
                          accountRepo,
                          rightsRepo,
                          userInvitationRepo,
                          instanceRepo,
                          serverImageRepo,
                          providerAccountRepo,
                          serviceRepositoryRepo);
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

    private DuracloudProviderAccountRepo createMockProviderAccountRepo(int count) {
        DuracloudProviderAccountRepo repo =
            EasyMock.createMock(DuracloudProviderAccountRepo.class);
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
        EasyMock.verify(rightsRepo);
        EasyMock.verify(userInvitationRepo);
        EasyMock.verify(instanceRepo);
        EasyMock.verify(serverImageRepo);
        EasyMock.verify(providerAccountRepo);
        EasyMock.verify(serviceRepositoryRepo);
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

}
