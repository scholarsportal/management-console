/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.instance.impl;

import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.ProviderAccount;
import org.duracloud.account.common.domain.ServerImage;
import org.duracloud.account.common.domain.ServiceRepository;
import org.duracloud.account.db.DuracloudProviderAccountRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudServerImageRepo;
import org.duracloud.account.db.DuracloudServiceRepositoryRepo;
import org.duracloud.appconfig.domain.DuradminConfig;
import org.duracloud.appconfig.domain.DuraserviceConfig;
import org.duracloud.appconfig.domain.DurastoreConfig;
import org.duracloud.storage.domain.StorageAccount;
import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

/**
 * @author: Bill Branan
 * Date: 2/24/11
 */
public class InstanceConfigUtilImplTest {

    protected InstanceConfigUtilImpl instanceConfigUtil;

    protected DuracloudInstance instance;
    protected DuracloudRepoMgr repoMgr;
    protected DuracloudProviderAccountRepo providerAcctRepo;
    protected DuracloudServerImageRepo serverImageRepo;
    protected DuracloudServiceRepositoryRepo serviceRepositoryRepo;

    @Before
    public void setup() throws Exception {
        Set<Integer> ids = new HashSet<Integer>();
        ids.add(1);

        instance = EasyMock.createMock("DuracloudInstance",
                                       DuracloudInstance.class);
        repoMgr = EasyMock.createMock("DuracloudRepoMgr",
                                      DuracloudRepoMgr.class);
        providerAcctRepo =
            EasyMock.createMock("DuracloudProviderAccountRepo",
                                DuracloudProviderAccountRepo.class);
        serverImageRepo =
            EasyMock.createMock("DuracloudServerImageRepo",
                                DuracloudServerImageRepo.class);
        serviceRepositoryRepo =
            EasyMock.createMock("DuracloudServiceRepositoryRepo",
                                DuracloudServiceRepositoryRepo.class);

        instanceConfigUtil = new InstanceConfigUtilImpl(instance, repoMgr);
    }

    protected void replayMocks() {
        EasyMock.replay(instance,
                        repoMgr,
                        providerAcctRepo,
                        serverImageRepo,
                        serviceRepositoryRepo);
    }

    @After
    public void teardown() {
        EasyMock.verify(instance,
                        repoMgr,
                        providerAcctRepo,
                        serverImageRepo,
                        serviceRepositoryRepo);
    }

    @Test
    public void testGetDuradminConfig() {
        String hostName = "host";
        EasyMock.expect(instance.getHostName())
            .andReturn(hostName)
            .times(2);

        replayMocks();

        DuradminConfig config = instanceConfigUtil.getDuradminConfig();
        assertNotNull(config);

        assertEquals(hostName, config.getDurastoreHost());
        assertEquals(InstanceConfigUtilImpl.DEFAULT_SSL_PORT,
                     config.getDurastorePort());
        assertEquals(DurastoreConfig.QUALIFIER,
                     config.getDurastoreContext());

        assertEquals(hostName, config.getDuraserviceHost());
        assertEquals(InstanceConfigUtilImpl.DEFAULT_SSL_PORT,
                     config.getDuraservicePort());
        assertEquals(DuraserviceConfig.QUALIFIER,
                     config.getDuraserviceContext());

    }

    @Test
    public void testGetDurastoreConfig() throws Exception {
        int primaryProviderId = 0;
        EasyMock.expect(instance.getPrimaryStorageProviderAccountId())
            .andReturn(primaryProviderId)
            .times(1);

        int secondaryProviderId = 1;
        Set<Integer> secondaryProviderIds = new HashSet<Integer>();
        secondaryProviderIds.add(secondaryProviderId);
        EasyMock.expect(instance.getSecondaryStorageProviderAccountIds())
            .andReturn(secondaryProviderIds)
            .times(1);

        String pUser = "primaryUsername";
        String pPass = "primaryPassword";
        ProviderAccount.ProviderType primaryType =
            ProviderAccount.ProviderType.AMAZON;
        ProviderAccount primaryProviderAccount =
            new ProviderAccount(primaryProviderId, primaryType, pUser, pPass);
        EasyMock.expect(providerAcctRepo.findById(primaryProviderId))
            .andReturn(primaryProviderAccount);

        String sUser = "secondaryUsername";
        String sPass = "secondaryPassword";
        ProviderAccount.ProviderType secondaryType =
            ProviderAccount.ProviderType.RACKSPACE;
        ProviderAccount secondaryProviderAccount =
            new ProviderAccount(secondaryProviderId, secondaryType, sUser, sPass);
        EasyMock.expect(providerAcctRepo.findById(secondaryProviderId))
            .andReturn(secondaryProviderAccount);

        EasyMock.expect(repoMgr.getProviderAccountRepo())
            .andReturn(providerAcctRepo)
            .times(1);

        replayMocks();

        DurastoreConfig config = instanceConfigUtil.getDurastoreConfig();
        assertNotNull(config);

        Collection<StorageAccount> storageAccounts = config.getStorageAccounts();
        assertNotNull(storageAccounts);
        assertEquals(2, storageAccounts.size());

        for(StorageAccount storageAccount : storageAccounts) {
            assertNotNull(storageAccount);
            int storageAccountId = Integer.parseInt(storageAccount.getId());
            if(storageAccountId == primaryProviderId) {
                assertEquals(instanceConfigUtil.translateType(primaryType),
                             storageAccount.getType());
                assertEquals(pUser, storageAccount.getUsername());
                assertEquals(pPass, storageAccount.getPassword());
            } else if(storageAccountId == secondaryProviderId) {
                assertEquals(instanceConfigUtil.translateType(secondaryType),
                             storageAccount.getType());
                assertEquals(sUser, storageAccount.getUsername());
                assertEquals(sPass, storageAccount.getPassword());
            } else {
                fail("Unexpected storage account, has ID:" + storageAccountId);
            }
        }
    }

    @Test
    public void testGetDuraserviceConfig() throws Exception {
        String instanceHost = "host";
        EasyMock.expect(instance.getHostName())
            .andReturn(instanceHost)
            .times(1);

        EasyMock.expect(repoMgr.getServerImageRepo())
            .andReturn(serverImageRepo)
            .times(1);

        int serverImageId = 0;
        EasyMock.expect(instance.getImageId())
            .andReturn(serverImageId)
            .times(1);

        String serverImageVersion = "version";
        ServerImage serverImage = new ServerImage(serverImageId,
                                                  0,
                                                  "providerImageId",
                                                  serverImageVersion,
                                                  "description");
        EasyMock.expect(serverImageRepo.findById(serverImageId))
            .andReturn(serverImage)
            .times(1);

        int serviceRepoId = 1;
        Set<Integer> serviceRepoIds = new HashSet<Integer>();
        serviceRepoIds.add(serviceRepoId);
        EasyMock.expect(instance.getServiceRepositoryIds())
            .andReturn(serviceRepoIds)
            .times(1);

        EasyMock.expect(repoMgr.getServiceRepositoryRepo())
            .andReturn(serviceRepositoryRepo)
            .times(1);

        ServiceRepository.ServiceRepositoryType serviceRepoType =
            ServiceRepository.ServiceRepositoryType.VERIFIED;
        String serviceRepoHost = "serviceRepoHost";
        String serviceRepoSpaceId = "serviceRepoSpaceId";
        String serviceRepoVersion = "serviceRepoVersion";
        String serviceRepoUsername = "serviceRepoUsername";
        String serviceRepoPassword = "serviceRepoPassword";
        ServiceRepository serviceRepo = new ServiceRepository(serviceRepoId,
                                                              serviceRepoType,
                                                              serviceRepoHost,
                                                              serviceRepoSpaceId,
                                                              serviceRepoVersion,
                                                              serviceRepoUsername,
                                                              serviceRepoPassword);
        EasyMock.expect(serviceRepositoryRepo.findById(serviceRepoId))
            .andReturn(serviceRepo)
            .times(1);

        replayMocks();

        DuraserviceConfig config = instanceConfigUtil.getDuraserviceConfig();
        assertNotNull(config);

        // Primary Instance
        DuraserviceConfig.PrimaryInstance primaryInstance =
            config.getPrimaryInstance();
        assertNotNull(primaryInstance);

        assertEquals(instanceHost, primaryInstance.getHost());
        assertEquals(InstanceConfigUtilImpl.DEFAULT_SERVICES_ADMIN_PORT,
                     primaryInstance.getServicesAdminPort());
        assertEquals(InstanceConfigUtilImpl.DEFAULT_SERVICES_ADMIN_CONTEXT_PREFIX +
                     serverImageVersion,
                     primaryInstance.getServicesAdminContext());

        // User Store
        DuraserviceConfig.UserStore userStore = config.getUserStore();
        assertNotNull(userStore);

        assertEquals(instanceHost, userStore.getHost());
        assertEquals(InstanceConfigUtilImpl.DEFAULT_SSL_PORT,
                     userStore.getPort());
        assertEquals(InstanceConfigUtilImpl.DEFAULT_DURASTORE_CONTEXT,
                     userStore.getContext());
        assertEquals("tcp://" + instanceHost + ":" +
                     InstanceConfigUtilImpl.DEFAULT_MSG_BROKER_PORT ,
                     userStore.getMsgBrokerUrl());

        // Service Store
        DuraserviceConfig.ServiceStore serviceStore = config.getServiceStore();
        assertNotNull(serviceStore);

        assertEquals(serviceRepoHost, serviceStore.getHost());
        assertEquals(InstanceConfigUtilImpl.DEFAULT_SSL_PORT,
                     serviceStore.getPort());
        assertEquals(InstanceConfigUtilImpl.DEFAULT_DURASTORE_CONTEXT,
                     serviceStore.getContext());
        assertEquals(serviceRepoSpaceId, serviceStore.getSpaceId());

        // Service Compute
        DuraserviceConfig.ServiceCompute serviceCompute =
            config.getServiceCompute();
        assertNotNull(serviceCompute);

        assertEquals(InstanceConfigUtilImpl.DEFAULT_SERVICE_COMPUTE_TYPE,
                     serviceCompute.getType());
        assertEquals(InstanceConfigUtilImpl.DEFAULT_SERVICE_COMPUTE_IMAGE_ID,
                     serviceCompute.getImageId());
        assertEquals(serviceRepoUsername, serviceCompute.getUsername());
        assertEquals(serviceRepoPassword, serviceCompute.getPassword());
    }

}
