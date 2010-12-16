/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudUserInvitationRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.impl.IdUtilImpl;
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

    private static final int COUNT = 5;

    @Before
    public void setUp() throws Exception {
        accountRepo = createMockAccountRepo(COUNT);
        userRepo = createMockUserRepo(COUNT);
        rightsRepo = createMockRightsRepo(COUNT);
        userInvitationRepo = createMockUserInvitationRepo(COUNT);

        idUtil = new IdUtilImpl();
        idUtil.initialize(userRepo, accountRepo, rightsRepo, userInvitationRepo);
    }

    private DuracloudUserInvitationRepo createMockUserInvitationRepo(int count) {
        DuracloudUserInvitationRepo repo = EasyMock.createMock(DuracloudUserInvitationRepo.class);
        EasyMock.expect(repo.getIds()).andReturn(createIds(count));
        EasyMock.replay(repo);
        return repo;
    }

    private DuracloudAccountRepo createMockAccountRepo(int count) {
        DuracloudAccountRepo repo = EasyMock.createMock(DuracloudAccountRepo.class);
        EasyMock.expect(repo.getIds()).andReturn(createIds(count));
        EasyMock.replay(repo);
        return repo;
    }

    private DuracloudUserRepo createMockUserRepo(int count) {
        DuracloudUserRepo repo = EasyMock.createMock(DuracloudUserRepo.class);
        EasyMock.expect(repo.getIds()).andReturn(createIds(count));
        EasyMock.replay(repo);
        return repo;
    }

    private DuracloudRightsRepo createMockRightsRepo(int count) {
        DuracloudRightsRepo repo = EasyMock.createMock(DuracloudRightsRepo.class);
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

}
