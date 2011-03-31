/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.ComputeProviderAccount;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.ServerImage;
import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.compute.ComputeProviderUtil;
import org.duracloud.account.compute.DuracloudComputeProvider;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudComputeProviderAccountRepo;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudServerImageRepo;
import org.duracloud.account.db.DuracloudStorageProviderAccountRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.util.instance.InstanceConfigUtil;
import org.duracloud.account.util.instance.InstanceUpdater;
import org.duracloud.account.util.instance.impl.InstanceUpdaterImpl;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: Bill Branan
 * Date: 2/9/11
 */
public class DuracloudInstanceServiceTestBase {

    protected int accountId = 1;
    protected DuracloudInstance instance;
    protected AccountInfo account;
    protected DuracloudRepoMgr repoMgr;
    protected ComputeProviderUtil computeProviderUtil;
    protected DuracloudComputeProvider computeProvider;
    protected DuracloudInstanceServiceImpl service;
    protected DuracloudComputeProviderAccountRepo computeProviderAcctRepo;
    protected DuracloudStorageProviderAccountRepo storageProviderAcctRepo;
    protected ComputeProviderAccount computeProviderAcct;
    protected StorageProviderAccount storageProviderAcct;
    protected DuracloudInstanceRepo instanceRepo;
    protected InstanceUpdater instanceUpdater;
    protected DuracloudRightsRepo rightsRepo;
    protected DuracloudUserRepo userRepo;
    protected InstanceConfigUtil instanceConfigUtil;
    protected DuracloudAccountRepo accountRepo;
    protected DuracloudServerImageRepo serverImageRepo;
    protected ServerImage serverImage;

    @Before
    public void setup() throws Exception {
        Set<Integer> ids = new HashSet<Integer>();
        ids.add(1);

        instance = EasyMock.createMock("DuracloudInstance",
                                       DuracloudInstance.class);
        account = EasyMock.createMock("AccountInfo", AccountInfo.class);
        repoMgr = EasyMock.createMock("DuracloudRepoMgr",
                                      DuracloudRepoMgr.class);
        computeProviderUtil = EasyMock.createMock("ComputeProviderUtil",
                                                  ComputeProviderUtil.class);
        computeProvider = EasyMock.createMock("DuracloudComputeProvider",
                                              DuracloudComputeProvider.class);
        computeProviderAcctRepo = EasyMock.createMock("DuracloudComputeProviderAccountRepo",
                                               DuracloudComputeProviderAccountRepo.class);
        computeProviderAcct = EasyMock.createMock("ComputeProviderAccount",
                                                  ComputeProviderAccount.class);
        storageProviderAcctRepo = EasyMock.createMock("DuracloudStorageProviderAccountRepo",
                                               DuracloudStorageProviderAccountRepo.class);
        storageProviderAcct = EasyMock.createMock("StorageProviderAccount",
                                           StorageProviderAccount.class);
        instanceRepo = EasyMock.createMock("DuracloudInstanceRepo",
                                           DuracloudInstanceRepo.class);
        instanceUpdater = EasyMock.createMock("InstanceUpdaterImpl",
                                              InstanceUpdaterImpl.class);
        rightsRepo = EasyMock.createMock("DuracloudRightsRepo",
                                         DuracloudRightsRepo.class);
        userRepo = EasyMock.createMock("DuracloudUserRepo",
                                       DuracloudUserRepo.class);
        instanceConfigUtil = EasyMock.createMock("InstanceConfigUtil",
                                                 InstanceConfigUtil.class);
        accountRepo = EasyMock.createMock("DuracloudAccountRepo",
                                          DuracloudAccountRepo.class);
        serverImageRepo = EasyMock.createMock("DuracloudServerImageRepo",
                                              DuracloudServerImageRepo.class);
        serverImage = EasyMock.createMock("ServerImage", ServerImage.class);

        service = new DuracloudInstanceServiceImpl(accountId,
                                                   instance,
                                                   repoMgr,
                                                   computeProviderUtil,
                                                   computeProvider,
                                                   instanceUpdater,
                                                   instanceConfigUtil);
    }

    protected void replayMocks() {
        EasyMock.replay(instance,
                        account,
                        repoMgr,
                        computeProviderUtil,
                        computeProvider,
                        computeProviderAcctRepo,
                        computeProviderAcct,
                        storageProviderAcctRepo,
                        storageProviderAcct,
                        instanceRepo,
                        instanceUpdater,
                        rightsRepo,
                        userRepo,
                        instanceConfigUtil,
                        accountRepo,
                        serverImageRepo,
                        serverImage);
    }

    @After
    public void teardown() {
        EasyMock.verify(instance,
                        account,
                        repoMgr,
                        computeProviderUtil,
                        computeProvider,
                        computeProviderAcctRepo,
                        computeProviderAcct,
                        storageProviderAcctRepo,
                        storageProviderAcct,
                        instanceRepo,
                        instanceUpdater,
                        rightsRepo,
                        userRepo,
                        instanceConfigUtil,
                        accountRepo,
                        serverImageRepo,
                        serverImage);
    }

    protected void setUpInitComputeProvider() throws Exception {
        EasyMock.expect(repoMgr.getAccountRepo())
            .andReturn(accountRepo)
            .anyTimes();
        EasyMock.expect(accountRepo.findById(EasyMock.anyInt()))
            .andReturn(account)
            .anyTimes();
        EasyMock.expect(instance.getAccountId())
            .andReturn(1)
            .anyTimes();

        EasyMock.expect(repoMgr.getComputeProviderAccountRepo())
            .andReturn(computeProviderAcctRepo)
            .anyTimes();
        EasyMock.expect(computeProviderAcctRepo.findById(EasyMock.anyInt()))
            .andReturn(computeProviderAcct)
            .anyTimes();
        EasyMock.expect(account.getComputeProviderAccountId())
            .andReturn(1)
            .anyTimes();
        
        String user = "username";
        String pass = "password";
        EasyMock.expect(computeProviderUtil.getComputeProvider(user, pass))
            .andReturn(computeProvider)
            .anyTimes();
        EasyMock.expect(computeProviderAcct.getUsername())
            .andReturn(user)
            .anyTimes();
        EasyMock.expect(computeProviderAcct.getPassword())
            .andReturn(pass)
            .anyTimes();
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

}
