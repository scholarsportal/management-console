/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.IdUtil;
import org.duracloud.storage.domain.StorageProviderType;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;

/**
 * @author Andrew Woods
 *         Date: Dec 8, 2010
 */
public class DuracloudServiceTestBase {

    protected DuracloudUserRepo userRepo;
    protected DuracloudAccountRepo accountRepo;
    protected DuracloudRightsRepo rightsRepo;

    protected IdUtil idUtil;

    protected static final int NUM_USERS = 4;
    protected static final int NUM_ACCTS = 4;
    protected static final int NUM_RIGHTS = 4;

    @Before
    public void before() throws Exception {
        userRepo = EasyMock.createMock("DuracloudUserRepo",
                                       DuracloudUserRepo.class);
        accountRepo = EasyMock.createMock("DuracloudAccountRepo",
                                          DuracloudAccountRepo.class);
        rightsRepo = EasyMock.createMock("DuraCloudRightsRepo",
                                         DuracloudRightsRepo.class);
        idUtil = EasyMock.createMock("IdUtil", IdUtil.class);

        Set<Integer> userIds = createIds(NUM_USERS);
        Set<Integer> acctIds = createIds(NUM_ACCTS);
        Set<Integer> rightsIds = createIds(NUM_RIGHTS);

        EasyMock.expect(userRepo.getIds()).andReturn(userIds).anyTimes();
        EasyMock.expect(accountRepo.getIds()).andReturn(acctIds).anyTimes();
        EasyMock.expect(rightsRepo.getIds()).andReturn(rightsIds).anyTimes();
    }

    protected Set<Integer> createIds(int count) {
        Set<Integer> ids = new HashSet<Integer>();
        for (int i = 0; i < count; ++i) {
            ids.add(i);
        }
        return ids;
    }

    @After
    public void tearDown() {
        EasyMock.verify(userRepo);
        EasyMock.verify(accountRepo);
        EasyMock.verify(rightsRepo);
        EasyMock.verify(idUtil);
    }

    protected DuracloudUser newDuracloudUser(int userId, String username) {
        String password = "password";
        String firstName = "firstName";
        String lastName = "lastName";
        String email = "email";
        return new DuracloudUser(userId,
                                 username,
                                 password,
                                 firstName,
                                 lastName,
                                 email);
    }

    protected AccountInfo newAccountInfo(int acctId) {
        return newAccountInfo(acctId, "subdomain-" + acctId);
    }

    protected AccountInfo newAccountInfo(int acctId, String subdomain) {
        int paymentInfoId = 0;
        Set<Integer> instanceIds = new HashSet<Integer>();
        Set<StorageProviderType> storageProviders = new HashSet<StorageProviderType>(
            Arrays.asList(StorageProviderType.values()));
        return new AccountInfo(acctId,
                               subdomain,
                               "account-" + acctId,
                               "org-" + acctId,
                               "dept-" + acctId,
                               paymentInfoId,
                               instanceIds,
                               storageProviders);
    }

    protected void replayRepos() {
        EasyMock.replay(userRepo);
        EasyMock.replay(accountRepo);
        EasyMock.replay(rightsRepo);
        EasyMock.replay(idUtil);
    }
}
