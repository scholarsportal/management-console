/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountCreationInfo;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.ServicePlan;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudComputeProviderAccountRepo;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudStorageProviderAccountRepo;
import org.duracloud.account.db.DuracloudUserInvitationRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.IdUtil;
import org.duracloud.account.util.AccountServiceFactory;
import org.duracloud.account.util.DuracloudInstanceManagerService;
import org.duracloud.account.util.notification.NotificationMgr;
import org.duracloud.account.util.usermgmt.UserDetailsPropagator;
import org.duracloud.common.util.ChecksumUtil;
import org.duracloud.storage.domain.StorageProviderType;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: Dec 8, 2010
 */
public class DuracloudServiceTestBase {

    protected DuracloudRepoMgr repoMgr;
    protected NotificationMgr notificationMgr;
    protected DuracloudUserRepo userRepo;
    protected DuracloudAccountRepo accountRepo;
    protected DuracloudRightsRepo rightsRepo;
    protected DuracloudUserInvitationRepo invitationRepo;
    protected DuracloudInstanceRepo instanceRepo;
    protected UserDetailsPropagator propagator;
    protected DuracloudUserServiceImpl userService;
    protected DuracloudProviderAccountUtil providerAccountUtil;
    protected AccountServiceFactory accountServiceFactory;
    protected DuracloudStorageProviderAccountRepo storageProviderAcctRepo;
    protected DuracloudComputeProviderAccountRepo computeProviderAcctRepo;
    protected DuracloudInstanceManagerService instanceManagerService;

    protected IdUtil idUtil;

    protected static final int NUM_USERS = 4;
    protected static final int NUM_ACCTS = 4;
    protected static final int NUM_RIGHTS = 4;
    protected static final int NUM_INVITES = 4;
    protected static final int NUM_INSTANCES = 4;

    @Before
    public void before() throws Exception {
        userRepo = EasyMock.createMock("DuracloudUserRepo",
                                       DuracloudUserRepo.class);
        accountRepo = EasyMock.createMock("DuracloudAccountRepo",
                                          DuracloudAccountRepo.class);
        rightsRepo = EasyMock.createMock("DuracloudRightsRepo",
                                         DuracloudRightsRepo.class);
        invitationRepo = EasyMock.createMock("DuracloudUserInvitationRepo",
                                             DuracloudUserInvitationRepo.class);
        instanceRepo = EasyMock.createMock("DuracloudInstanceRepo",
                                           DuracloudInstanceRepo.class);
        storageProviderAcctRepo =
            EasyMock.createMock("DuracloudStorageProviderAccountRepo",
                                DuracloudStorageProviderAccountRepo.class);
        computeProviderAcctRepo =
            EasyMock.createMock("DuracloudComputeProviderAccountRepo",
                                DuracloudComputeProviderAccountRepo.class);
        propagator = EasyMock.createMock("UserDetailsPropagator",
                                         UserDetailsPropagator.class);
        providerAccountUtil =
            EasyMock.createMock("DuracloudProviderAccountUtil",
                                DuracloudProviderAccountUtil.class);
        accountServiceFactory = EasyMock.createMock("AccountServiceFactory",
                                                    AccountServiceFactory.class);
        idUtil = EasyMock.createMock("IdUtil", IdUtil.class);
        repoMgr = EasyMock.createMock("DuracloudRepoMgr",
                                      DuracloudRepoMgr.class);
        notificationMgr = EasyMock.createMock("NotificationMgr",
                                      NotificationMgr.class);
        instanceManagerService = EasyMock.createMock("DuracloudInstanceManagerService",
                                      DuracloudInstanceManagerService.class);

        Set<Integer> userIds = createIds(NUM_USERS);
        Set<Integer> acctIds = createIds(NUM_ACCTS);
        Set<Integer> rightsIds = createIds(NUM_RIGHTS);
        Set<Integer> invitationIds = createIds(NUM_INVITES);
        Set<Integer> instanceIds = createIds(NUM_INSTANCES);

        EasyMock.expect(userRepo.getIds()).andReturn(userIds).anyTimes();
        EasyMock.expect(accountRepo.getIds()).andReturn(acctIds).anyTimes();
        EasyMock.expect(rightsRepo.getIds()).andReturn(rightsIds).anyTimes();
        EasyMock.expect(invitationRepo.getIds()).andReturn(invitationIds).anyTimes();
        EasyMock.expect(instanceRepo.getIds()).andReturn(instanceIds).anyTimes();

        EasyMock.expect(repoMgr.getUserRepo())
            .andReturn(userRepo)
            .anyTimes();
        EasyMock.expect(repoMgr.getAccountRepo())
            .andReturn(accountRepo)
            .anyTimes();
        EasyMock.expect(repoMgr.getRightsRepo())
            .andReturn(rightsRepo)
            .anyTimes();
        EasyMock.expect(repoMgr.getUserInvitationRepo())
            .andReturn(invitationRepo)
            .anyTimes();
        EasyMock.expect(repoMgr.getInstanceRepo())
            .andReturn(instanceRepo)
            .anyTimes();
        EasyMock.expect(repoMgr.getStorageProviderAccountRepo())
            .andReturn(storageProviderAcctRepo)
            .anyTimes();
        EasyMock.expect(repoMgr.getComputeProviderAccountRepo())
            .andReturn(computeProviderAcctRepo)
            .anyTimes();
        EasyMock.expect(repoMgr.getIdUtil()).andReturn(idUtil).anyTimes();
    }

    protected Set<Integer> createIds(int count) {
        Set<Integer> ids = new HashSet<Integer>();
        for (int i = 0; i < count; ++i) {
            ids.add(i);
        }
        return ids;
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(repoMgr);
        EasyMock.verify(notificationMgr);
        EasyMock.verify(userRepo);
        EasyMock.verify(accountRepo);
        EasyMock.verify(rightsRepo);
        EasyMock.verify(invitationRepo);
        EasyMock.verify(instanceRepo);
        EasyMock.verify(storageProviderAcctRepo);
        EasyMock.verify(computeProviderAcctRepo);
        EasyMock.verify(providerAccountUtil);
        EasyMock.verify(accountServiceFactory);
        EasyMock.verify(propagator);
        EasyMock.verify(idUtil);
        EasyMock.verify(instanceManagerService);
    }

    protected DuracloudUser newDuracloudUser(int userId, String username) {
        ChecksumUtil util = new ChecksumUtil(ChecksumUtil.Algorithm.SHA_256);

        String password = util.generateChecksum("password");
        String firstName = "firstName";
        String lastName = "lastName";
        String email = "email";
        String securityQuestion = "question";
        String securityAnswer = "answer";
        return new DuracloudUser(userId,
                                 username,
                                 password,
                                 firstName,
                                 lastName,
                                 email,
                                 securityQuestion,
                                 securityAnswer);
    }

    protected AccountInfo newAccountInfo(int acctId) {
        return newAccountInfo(acctId, "subdomain-" + acctId);
    }

    protected AccountCreationInfo newAccountCreationInfo(int acctId,
                                                         String subdomain) {
        Set<StorageProviderType> secStorProvTypes =
            new HashSet<StorageProviderType>();
        return new AccountCreationInfo(subdomain,
                                       "account-" + acctId,
                                       "org-" + acctId,
                                       "dept-" + acctId,
                                       StorageProviderType.AMAZON_S3,
                                       secStorProvTypes,
                                       ServicePlan.STARTER_ARCHIVING);
    }

    protected AccountInfo newAccountInfo(int acctId, String subdomain) {
        int computeProvAcctId = 0;
        int primStorageProvAcctId = 0;
        Set<Integer> secStorageProvAcctIds = new HashSet<Integer>();
        secStorageProvAcctIds.add(0);
        Set<Integer> secServiceRepoIds = new HashSet<Integer>();
        int paymentInfoId = 0;
        AccountInfo.AccountStatus status = AccountInfo.AccountStatus.PENDING;
        return new AccountInfo(acctId,
                               subdomain,
                               "account-" + acctId,
                               "org-" + acctId,
                               "dept-" + acctId,
                               computeProvAcctId,
                               primStorageProvAcctId,
                               secStorageProvAcctIds,
                               secServiceRepoIds,
                               paymentInfoId,
                               ServicePlan.PROFESSIONAL,
                               status);
    }

    protected void replayMocks() {
        EasyMock.replay(userRepo);
        EasyMock.replay(notificationMgr);
        EasyMock.replay(accountRepo);
        EasyMock.replay(rightsRepo);
        EasyMock.replay(invitationRepo);
        EasyMock.replay(instanceRepo);
        EasyMock.replay(storageProviderAcctRepo);
        EasyMock.replay(computeProviderAcctRepo);
        EasyMock.replay(providerAccountUtil);
        EasyMock.replay(accountServiceFactory);
        EasyMock.replay(propagator);
        EasyMock.replay(idUtil);
        EasyMock.replay(repoMgr);
        EasyMock.replay(instanceManagerService);
    }
}
