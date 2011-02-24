/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.ProviderAccount;
import org.duracloud.account.compute.ComputeProviderUtil;
import org.duracloud.account.compute.DuracloudComputeProvider;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.DuracloudProviderAccountRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.instance.InstanceConfigUtil;
import org.duracloud.account.util.instance.InstanceUpdater;
import org.duracloud.account.util.instance.impl.InstanceUpdaterImpl;
import org.easymock.classextension.EasyMock;
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
    protected DuracloudRepoMgr repoMgr;
    protected ComputeProviderUtil computeProviderUtil;
    protected DuracloudComputeProvider computeProvider;
    protected DuracloudInstanceService service;
    protected DuracloudProviderAccountRepo providerAcctRepo;
    protected ProviderAccount providerAcct;
    protected DuracloudInstanceRepo instanceRepo;
    protected InstanceUpdater instanceUpdater;
    protected DuracloudRightsRepo rightsRepo;
    protected DuracloudUserRepo userRepo;
    protected InstanceConfigUtil instanceConfigUtil;

    @Before
    public void setup() throws Exception {
        Set<Integer> ids = new HashSet<Integer>();
        ids.add(1);

        instance = EasyMock.createMock("DuracloudInstance",
                                       DuracloudInstance.class);
        repoMgr = EasyMock.createMock("DuracloudRepoMgr",
                                      DuracloudRepoMgr.class);
        computeProviderUtil = EasyMock.createMock("ComputeProviderUtil",
                                                  ComputeProviderUtil.class);
        computeProvider = EasyMock.createMock("DuracloudComputeProvider",
                                              DuracloudComputeProvider.class);
        providerAcctRepo = EasyMock.createMock("DuracloudProviderAccountRepo",
                                               DuracloudProviderAccountRepo.class);
        providerAcct = EasyMock.createMock("ProviderAccount",
                                           ProviderAccount.class);
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
                        repoMgr,
                        computeProvider,
                        providerAcctRepo,
                        providerAcct,
                        instanceRepo,
                        instanceUpdater,
                        rightsRepo,
                        userRepo,
                        instanceConfigUtil);
    }

    @After
    public void teardown() {
        EasyMock.verify(instance,
                        repoMgr,
                        computeProvider,
                        providerAcctRepo,
                        providerAcct,
                        instanceRepo,
                        instanceUpdater,
                        rightsRepo,
                        userRepo,
                        instanceConfigUtil);
    }

    protected void setUpInitComputeProvider() throws Exception {
        EasyMock.expect(repoMgr.getProviderAccountRepo())
            .andReturn(providerAcctRepo)
            .times(1);
        EasyMock.expect(providerAcctRepo.findById(EasyMock.anyInt()))
            .andReturn(providerAcct)
            .times(1);
        EasyMock.expect(computeProviderUtil
                            .getComputeProvider(EasyMock.isA(String.class),
                                                EasyMock.isA(String.class)))
            .andReturn(null)
            .times(1);
        EasyMock.expect(providerAcct.getUsername())
            .andReturn("user")
            .times(1);
        EasyMock.expect(providerAcct.getPassword())
            .andReturn("pass")
            .times(1);
        EasyMock.expect(instance.getComputeProviderAccountId())
            .andReturn(1)
            .times(1);
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
