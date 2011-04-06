/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.instance.impl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.ServerImage;
import org.duracloud.account.common.domain.ServiceRepository;
import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudServerImageRepo;
import org.duracloud.account.db.DuracloudServiceRepositoryRepo;
import org.duracloud.account.db.DuracloudStorageProviderAccountRepo;
import org.duracloud.appconfig.domain.DuradminConfig;
import org.duracloud.appconfig.domain.DuraserviceConfig;
import org.duracloud.appconfig.domain.DurastoreConfig;
import org.duracloud.storage.domain.StorageAccount;
import org.duracloud.storage.domain.StorageProviderType;
import org.easymock.EasyMock;
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
    protected AccountInfo account;
    protected DuracloudRepoMgr repoMgr;
    protected DuracloudAccountRepo accountRepo;
    protected DuracloudStorageProviderAccountRepo storageProviderAcctRepo;
    protected DuracloudServerImageRepo serverImageRepo;
    protected DuracloudServiceRepositoryRepo serviceRepositoryRepo;

    @Before
    public void setup() throws Exception {
        Set<Integer> ids = new HashSet<Integer>();
        ids.add(1);

        instance = EasyMock.createMock("DuracloudInstance",
                                       DuracloudInstance.class);
        account = EasyMock.createMock("AccountInfo", AccountInfo.class);
        repoMgr = EasyMock.createMock("DuracloudRepoMgr",
                                      DuracloudRepoMgr.class);
        accountRepo = EasyMock.createMock("DuracloudAccountRepo",
                                          DuracloudAccountRepo.class);
        storageProviderAcctRepo =
            EasyMock.createMock("DuracloudStorageProviderAccountRepo",
                                DuracloudStorageProviderAccountRepo.class);
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
                        account,
                        repoMgr,
                        accountRepo,
                        storageProviderAcctRepo,
                        serverImageRepo,
                        serviceRepositoryRepo);
    }

    @After
    public void teardown() {
        EasyMock.verify(instance,
                        account,
                        repoMgr,
                        accountRepo,
                        storageProviderAcctRepo,
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
        int accountId = 12;
        setUpGetAccount(accountId);

        int primaryProviderId = 0;
        EasyMock.expect(account.getPrimaryStorageProviderAccountId())
            .andReturn(primaryProviderId)
            .times(1);

        int secondaryProviderId = 1;
        Set<Integer> secondaryProviderIds = new HashSet<Integer>();
        secondaryProviderIds.add(secondaryProviderId);
        EasyMock.expect(account.getSecondaryStorageProviderAccountIds())
            .andReturn(secondaryProviderIds)
            .times(1);

        String pUser = "primaryUsername";
        String pPass = "primaryPassword";
        boolean rrs = false;
        StorageProviderType primaryType = StorageProviderType.AMAZON_S3;
        StorageProviderAccount primaryStorageProviderAccount =
            new StorageProviderAccount(primaryProviderId, primaryType, pUser, pPass, rrs);
        EasyMock.expect(storageProviderAcctRepo.findById(primaryProviderId))
            .andReturn(primaryStorageProviderAccount);

        String sUser = "secondaryUsername";
        String sPass = "secondaryPassword";
        StorageProviderType secondaryType = StorageProviderType.RACKSPACE;
        StorageProviderAccount secondaryStorageProviderAccount =
            new StorageProviderAccount(secondaryProviderId, secondaryType, sUser, sPass, rrs);
        EasyMock.expect(storageProviderAcctRepo.findById(secondaryProviderId))
            .andReturn(secondaryStorageProviderAccount);

        EasyMock.expect(repoMgr.getStorageProviderAccountRepo())
            .andReturn(storageProviderAcctRepo)
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
                assertEquals(primaryType,
                             storageAccount.getType());
                assertEquals(pUser, storageAccount.getUsername());
                assertEquals(pPass, storageAccount.getPassword());
            } else if(storageAccountId == secondaryProviderId) {
                assertEquals(secondaryType,
                             storageAccount.getType());
                assertEquals(sUser, storageAccount.getUsername());
                assertEquals(sPass, storageAccount.getPassword());
            } else {
                fail("Unexpected storage account, has ID:" + storageAccountId);
            }
        }
    }

    private void setUpGetAccount(int accountId) throws Exception {
        EasyMock.expect(repoMgr.getAccountRepo())
            .andReturn(accountRepo)
            .times(1);
        EasyMock.expect(accountRepo.findById(accountId))
            .andReturn(account)
            .times(1);
        EasyMock.expect(instance.getAccountId())
            .andReturn(accountId)
            .times(1);
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

        String version = "version";
        ServerImage serverImage = new ServerImage(serverImageId,
                                                  0,
                                                  "providerImageId",
                                                  version,
                                                  "description",
                                                  "rootPass");
        EasyMock.expect(serverImageRepo.findById(serverImageId))
            .andReturn(serverImage)
            .times(1);

        EasyMock.expect(repoMgr.getServiceRepositoryRepo())
            .andReturn(serviceRepositoryRepo)
            .times(1);

        Set<ServiceRepository> serviceRepos = new HashSet<ServiceRepository>();
        ServiceRepository.ServiceRepositoryType serviceRepoType =
            ServiceRepository.ServiceRepositoryType.VERIFIED;
        int serviceRepoId = 1;
        String serviceRepoHost = "serviceRepoHost";
        String serviceRepoSpaceId = "serviceRepoSpaceId";
        String serviceRepoUsername = "serviceRepoUsername";
        String serviceRepoPassword = "serviceRepoPassword";
        ServiceRepository serviceRepo = new ServiceRepository(serviceRepoId,
                                                              serviceRepoType,
                                                              serviceRepoHost,
                                                              serviceRepoSpaceId,
                                                              version,
                                                              serviceRepoUsername,
                                                              serviceRepoPassword);
        serviceRepos.add(serviceRepo);
        EasyMock.expect(serviceRepositoryRepo.findByVersion(version))
            .andReturn(serviceRepos)
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
                     version,
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
