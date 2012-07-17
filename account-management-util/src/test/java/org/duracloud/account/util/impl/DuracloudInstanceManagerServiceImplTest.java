/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountType;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.InstanceType;
import org.duracloud.account.common.domain.ServerImage;
import org.duracloud.account.compute.error.DuracloudInstanceNotAvailableException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.DuracloudInstanceService;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

/**
 * @author: Bill Branan
 * Date: 2/9/11
 */
public class DuracloudInstanceManagerServiceImplTest
    extends DuracloudInstanceServiceTestBase {

    DuracloudInstanceManagerServiceImpl managerService;

    @Before
    public void setup() throws Exception {
        super.setup();
        managerService =
            new DuracloudInstanceManagerServiceImpl(repoMgr,
                                                    accountClusterUtil,
                                                    computeProviderUtil,
                                                    instanceServiceFactory);
    }

    @Test
    public void testGetVersions() throws Exception {
        setupGetServerImages();
        replayMocks();

        Set<String> resultVersions = managerService.getVersions();
        assertEquals(2, resultVersions.size());
    }

    private void setupGetServerImages() throws Exception {
        Set<Integer> imageIds = new HashSet<Integer>();
        imageIds.add(1);
        imageIds.add(2);
        EasyMock.expect(serverImageRepo.getIds())
            .andReturn(imageIds)
            .times(1);

        ServerImage one = new ServerImage(1, 1, "1", "1.0", "1", "1", false);
        EasyMock.expect(serverImageRepo.findById(EasyMock.anyInt()))
            .andReturn(one);
        ServerImage two = new ServerImage(2, 2, "2", "2.0", "2", "2", false);
        EasyMock.expect(serverImageRepo.findById(EasyMock.anyInt()))
            .andReturn(two);
    }

    @Test
    public void testGetLatestVersion() throws Exception {
        setupGetLatestImage();
        replayMocks();

        String resultVersion = managerService.getLatestVersion();
        assertEquals("1.0", resultVersion);
    }

    private void setupGetLatestImage() throws Exception {
        ServerImage latest = new ServerImage(1, 1, "1", "1.0", "1", "1", false);
        EasyMock.expect(serverImageRepo.findLatest())
            .andReturn(latest)
            .times(1);
    }

    @Test
    public void testCreateInstance() throws Exception {
        int accountId = 33;
        int serverDetailsId = 22;
        int computeProvAcctId = 5;
        int imageId = 1;
        String username = "username";
        String password = "password";
        String providerImageId = "1";
        String securityGroup = "security-group";
        String keypair = "keypair";
        String elasticIp = "127.0.0.1";
        String providerInstanceId = "27";
        String subdomain = "subdomain";
        int instanceId = 87;

        EasyMock.expect(accountRepo.findById(accountId))
            .andReturn(account)
            .times(1);
        EasyMock.expect(account.getType())
            .andReturn(AccountType.FULL)
            .times(1);
        EasyMock.expect(account.getServerDetailsId())
            .andReturn(serverDetailsId)
            .times(1);
        EasyMock.expect(serverDetailsRepo.findById(serverDetailsId))
            .andReturn(serverDetails);
        EasyMock.expect(serverDetails.getComputeProviderAccountId())
            .andReturn(computeProvAcctId)
            .times(1);
        EasyMock.expect(computeProviderAcctRepo.findById(computeProvAcctId))
            .andReturn(computeProviderAcct)
            .times(1);

        EasyMock.expect(computeProviderUtil.getComputeProvider(username,
                                                               password))
            .andReturn(computeProvider)
            .times(1);
        EasyMock.expect(computeProviderAcct.getUsername())
            .andReturn(username)
            .times(1);
        EasyMock.expect(computeProviderAcct.getPassword())
            .andReturn(password)
            .times(1);

        EasyMock.expect(serverImage.getProviderImageId())
            .andReturn(providerImageId)
            .times(1);
        EasyMock.expect(computeProviderAcct.getSecurityGroup())
            .andReturn(securityGroup)
            .times(1);
        EasyMock.expect(computeProviderAcct.getKeypair())
            .andReturn(keypair)
            .times(1);
        EasyMock.expect(computeProviderAcct.getElasticIp())
            .andReturn(elasticIp)
            .times(1);
        EasyMock.expect(computeProvider.start(providerImageId,
                                              securityGroup,
                                              keypair,
                                              elasticIp, 
                                              InstanceType.SMALL))
            .andReturn(providerInstanceId)
            .times(1);

        EasyMock.expect(account.getSubdomain())
            .andReturn(subdomain)
            .times(1);
        EasyMock.expect(idUtil.newInstanceId())
            .andReturn(instanceId)
            .times(1);
        EasyMock.expect(serverImage.getId())
            .andReturn(imageId)
            .times(1);
        EasyMock.expect(instanceRepo.findById(instanceId))
            .andReturn(instance)
            .times(1);
        instance.setProviderInstanceId(providerInstanceId);
        EasyMock.expectLastCall();
        
        instanceRepo.save(EasyMock.isA(DuracloudInstance.class));
        EasyMock.expectLastCall()
            .times(2);

        setUpInitComputeProvider();
        replayMocks();

        DuracloudInstance instance =
            managerService.doCreateInstance(accountId,
                                            serverImage,
                                            InstanceType.SMALL);
        assertNotNull(instance);
    }

    @Test
    public void testGetInstanceService() throws Exception {
        setUpInitComputeProvider();

        int instanceId = 2;
        int invalidInstanceId = -2;
        String providerId = "i-123";

        EasyMock.expect(instanceServiceFactory.getInstance(instance))
            .andReturn(service);

        // A known instance ID returns a valid instance
        EasyMock.expect(instanceRepo.findById(instanceId))
            .andReturn(instance)
            .times(1);

        EasyMock.expect(instance.getProviderInstanceId()).andReturn(providerId);
        EasyMock.expect(computeProvider.getStatus(providerId)).andReturn(
            "runnning");

        // An invalid instance ID throws
        EasyMock.expect(instanceRepo.findById(invalidInstanceId))
            .andThrow(new DBNotFoundException("Not Found"))
            .times(1);

        replayMocks();

        DuracloudInstanceService instanceService =
            managerService.getInstanceService(instanceId);
        assertNotNull(instanceService);

        try {
            instanceService =
                managerService.getInstanceService(invalidInstanceId);
            fail("Exception expected");
        } catch(DuracloudInstanceNotAvailableException expected) {
            assertNotNull(expected);
        }
    }

    @Test
    public void testGetInstanceServiceError() throws Exception {
        setUpInitComputeProvider();

        int instanceId = 2;
        String providerId = "i-123";

        EasyMock.expect(instanceServiceFactory.getInstance(instance)).andReturn(
            service);

        EasyMock.expect(instanceRepo.findById(instanceId)).andReturn(instance);
        EasyMock.expect(instance.getProviderInstanceId()).andReturn(providerId);
        EasyMock.expect(computeProvider.getStatus(providerId))
            .andThrow(new DuracloudInstanceNotAvailableException(
                "canned-exception"));

        instanceRepo.delete(instanceId);
        EasyMock.expectLastCall();

        replayMocks();

        try {
            managerService.getInstanceService(instanceId);
            fail("Exception expected");
        } catch (DuracloudInstanceNotAvailableException expected) {
            assertNotNull(expected);
        }
    }

    @Test
    public void testGetInstanceServices() throws Exception {
        setUpInitComputeProvider();

        int acctId = 1;
        int instanceId = 2;
        String providerId = "i-876";
        
        setUpGetInstanceIds(acctId, instanceId, 1);

        EasyMock.expect(instanceServiceFactory.getInstance(instance))
            .andReturn(service);

        EasyMock.expect(instanceRepo.findById(EasyMock.anyInt()))
            .andReturn(instance)
            .times(1);

        EasyMock.expect(instance.getProviderInstanceId()).andReturn(providerId);
        EasyMock.expect(computeProvider.getStatus(providerId)).andReturn(
            "runnning");

        replayMocks();

        Set<DuracloudInstanceService> instanceServices =
            managerService.getInstanceServices(acctId);
        assertNotNull(instanceServices);
        assertEquals(1, instanceServices.size());
    }

    private void setUpGetInstanceIds(int accountId,
                                     int instanceId,
                                     int times) throws Exception{
        Set<Integer> instanceIds = new HashSet<Integer>();
        instanceIds.add(instanceId);
        EasyMock.expect(instanceRepo.findByAccountId(accountId))
            .andReturn(instanceIds)
            .times(times);
    }

}
