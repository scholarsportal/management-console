/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountCreationInfo;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountType;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.ServerDetails;
import org.duracloud.account.common.domain.ServicePlan;
import org.duracloud.account.db.*;
import org.duracloud.account.util.AccountServiceFactory;
import org.duracloud.account.util.DuracloudInstanceManagerService;
import org.duracloud.account.util.notification.NotificationMgr;
import org.duracloud.account.util.usermgmt.UserDetailsPropagator;
import org.duracloud.account.util.util.AccountClusterUtil;
import org.duracloud.account.util.util.AccountUtil;
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
    protected AccountUtil accountUtil;
    protected AccountClusterUtil clusterUtil;
    protected NotificationMgr notificationMgr;
    protected DuracloudUserRepo userRepo;
    protected DuracloudGroupRepo groupRepo;
    protected DuracloudAccountRepo accountRepo;
    protected DuracloudAccountClusterRepo accountClusterRepo;
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
    protected DuracloudServerImageRepo serverImageRepo;
    protected DuracloudServiceRepositoryRepo serviceRepositoryRepo;
    protected DuracloudServerDetailsRepo serverDetailsRepo;

    protected IdUtil idUtil;

    protected static final int NUM_USERS = 4;
    protected static final int NUM_GROUPS = 4;
    protected static final int NUM_ACCTS = 4;
    protected static final int NUM_RIGHTS = 4;
    protected static final int NUM_INVITES = 4;
    protected static final int NUM_INSTANCES = 4;

    @Before
    public void before() throws Exception {
        accountUtil = EasyMock.createMock("AccountUtil", AccountUtil.class);
        clusterUtil = EasyMock.createMock("AccountClusterUtil",
                                          AccountClusterUtil.class);
        userRepo = EasyMock.createMock("DuracloudUserRepo",
                                       DuracloudUserRepo.class);
        groupRepo = EasyMock.createMock("DuracloudGroupRepo",
                                        DuracloudGroupRepo.class);
        accountRepo = EasyMock.createMock("DuracloudAccountRepo",
                                          DuracloudAccountRepo.class);
        accountClusterRepo =
            EasyMock.createMock("DuracloudAccountClusterRepo",
                                DuracloudAccountClusterRepo.class);
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
        serverImageRepo = EasyMock.createMock("DuracloudServerImageRepo",
                                      DuracloudServerImageRepo.class);
        serviceRepositoryRepo = EasyMock.createMock("DuracloudServiceRepositoryRepo",
                                      DuracloudServiceRepositoryRepo.class);
        serverDetailsRepo = EasyMock.createMock("DuracloudServerDetailsRepo",
                                                DuracloudServerDetailsRepo.class);

        Set<Integer> userIds = createIds(NUM_USERS);
        Set<Integer> groupIds = createIds(NUM_GROUPS);
        Set<Integer> acctIds = createIds(NUM_ACCTS);
        Set<Integer> rightsIds = createIds(NUM_RIGHTS);
        Set<Integer> invitationIds = createIds(NUM_INVITES);
        Set<Integer> instanceIds = createIds(NUM_INSTANCES);

        EasyMock.expect(userRepo.getIds()).andReturn(userIds).anyTimes();
        EasyMock.expect(groupRepo.getIds()).andReturn(groupIds).anyTimes();
        EasyMock.expect(accountRepo.getIds()).andReturn(acctIds).anyTimes();
        EasyMock.expect(rightsRepo.getIds()).andReturn(rightsIds).anyTimes();
        EasyMock.expect(invitationRepo.getIds()).andReturn(invitationIds).anyTimes();
        EasyMock.expect(instanceRepo.getIds()).andReturn(instanceIds).anyTimes();

        EasyMock.expect(repoMgr.getUserRepo())
            .andReturn(userRepo)
            .anyTimes();
        EasyMock.expect(repoMgr.getGroupRepo())
            .andReturn(groupRepo)
            .anyTimes();
        EasyMock.expect(repoMgr.getAccountRepo())
            .andReturn(accountRepo)
            .anyTimes();
        EasyMock.expect(repoMgr.getAccountClusterRepo())
            .andReturn(accountClusterRepo)
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
        EasyMock.expect(repoMgr.getIdUtil())
            .andReturn(idUtil)
            .anyTimes();
        EasyMock.expect(repoMgr.getServerImageRepo())
            .andReturn(serverImageRepo)
            .anyTimes();
        EasyMock.expect(repoMgr.getServiceRepositoryRepo())
            .andReturn(serviceRepositoryRepo)
            .anyTimes();
        EasyMock.expect(repoMgr.getServerDetailsRepo())
            .andReturn(serverDetailsRepo)
            .anyTimes();
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
        EasyMock.verify(accountUtil);
        EasyMock.verify(clusterUtil);
        EasyMock.verify(notificationMgr);
        EasyMock.verify(userRepo);
        EasyMock.verify(groupRepo);
        EasyMock.verify(accountRepo);
        EasyMock.verify(accountClusterRepo);
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
        EasyMock.verify(serverImageRepo);
        EasyMock.verify(serviceRepositoryRepo);
        EasyMock.verify(serverDetailsRepo);
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
                                                         String subdomain,
                                                         AccountType type) {
        Set<StorageProviderType> secStorProvTypes =
            new HashSet<StorageProviderType>();
        return new AccountCreationInfo(subdomain,
                                       "account-" + acctId,
                                       "org-" + acctId,
                                       "dept-" + acctId,
                                       StorageProviderType.AMAZON_S3,
                                       secStorProvTypes,
                                       ServicePlan.PROFESSIONAL,
                                       type,
                                       acctId);
    }

    protected AccountInfo newAccountInfo(int acctId, String subdomain) {
        int paymentInfoId = 0;
        int serverDetailsId = 1;
        int accountClusterId = 2;
        AccountInfo.AccountStatus status = AccountInfo.AccountStatus.PENDING;
        return new AccountInfo(acctId,
                               subdomain,
                               "account-" + acctId,
                               "org-" + acctId,
                               "dept-" + acctId,
                               paymentInfoId,
                               serverDetailsId,
                               accountClusterId,
                               status,
                               AccountType.FULL);
    }

    protected ServerDetails newServerDetails(int serverDetailsId) {
        int computeProvAcctId = 0;
        int primStorageProvAcctId = 0;
        Set<Integer> secStorageProvAcctIds = new HashSet<Integer>();
        secStorageProvAcctIds.add(0);
        Set<Integer> secServiceRepoIds = new HashSet<Integer>();

        return new ServerDetails(serverDetailsId,
                                 computeProvAcctId,
                                 primStorageProvAcctId,
                                 secStorageProvAcctIds,
                                 secServiceRepoIds,
                                 ServicePlan.PROFESSIONAL);
    }

    protected void replayMocks() {
        EasyMock.replay(accountUtil);
        EasyMock.replay(clusterUtil);
        EasyMock.replay(userRepo);
        EasyMock.replay(groupRepo);
        EasyMock.replay(notificationMgr);
        EasyMock.replay(accountRepo);
        EasyMock.replay(accountClusterRepo);
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
        EasyMock.replay(serverImageRepo);
        EasyMock.replay(serviceRepositoryRepo);
        EasyMock.replay(serverDetailsRepo);
    }
}
