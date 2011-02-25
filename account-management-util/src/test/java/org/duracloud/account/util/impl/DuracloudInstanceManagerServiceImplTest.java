/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.error.DuracloudInstanceNotAvailableException;
import org.easymock.classextension.EasyMock;
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

    @Test
    public void testGetInstanceService() throws Exception {
        setUpInitComputeProvider();

        int acctId = 1;
        int instanceId = 2;

        setUpGetInstanceIds(instanceId, 2);

        EasyMock.expect(repoMgr.getInstanceRepo())
            .andReturn(instanceRepo)
            .times(1);
        EasyMock.expect(instanceRepo.findById(EasyMock.anyInt()))
            .andReturn(instance)
            .times(1);

        replayMocks();

        DuracloudInstanceManagerServiceImpl managerService =
            new DuracloudInstanceManagerServiceImpl(repoMgr,
                                                    computeProviderUtil);

        DuracloudInstanceService instanceService =
            managerService.getInstanceService(acctId, instanceId);
        assertNotNull(instanceService);

        try {
            instanceService = managerService.getInstanceService(acctId, -10);
            fail("Exception expected");
        } catch(DuracloudInstanceNotAvailableException expected) {
            assertNotNull(expected);
        }
    }

    @Test
    public void testGetInstanceServices() throws Exception {
        setUpInitComputeProvider();

        int acctId = 1;
        int instanceId = 2;
        
        setUpGetInstanceIds(instanceId, 1);

        EasyMock.expect(repoMgr.getInstanceRepo())
            .andReturn(instanceRepo)
            .times(1);
        EasyMock.expect(instanceRepo.findById(EasyMock.anyInt()))
            .andReturn(instance)
            .times(1);

        replayMocks();

        DuracloudInstanceManagerServiceImpl managerService =
            new DuracloudInstanceManagerServiceImpl(repoMgr,
                                                    computeProviderUtil);

        Set<DuracloudInstanceService> instanceServices =
            managerService.getInstanceServices(acctId);
        assertNotNull(instanceServices);
        assertEquals(1, instanceServices.size());
    }

    private void setUpGetInstanceIds(int instanceId, int times) throws Exception{
        EasyMock.expect(repoMgr.getAccountRepo())
            .andReturn(accountRepo)
            .times(times);
        Set<Integer> instanceIds = new HashSet<Integer>();
        instanceIds.add(instanceId);
        AccountInfo accountInfo =
            new AccountInfo(0, "subdomain", "acctName", "orgName", "dept",
                            0, instanceIds, null);
        EasyMock.expect(accountRepo.findById(EasyMock.anyInt()))
            .andReturn(accountInfo)
            .times(times);
    }

}
