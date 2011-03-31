/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.error.DuracloudInstanceNotAvailableException;
import org.easymock.EasyMock;
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

        int instanceId = 2;
        int invalidInstanceId = -2;

        EasyMock.expect(repoMgr.getInstanceRepo())
            .andReturn(instanceRepo)
            .times(2);

        // A known instance ID returns a valid instance
        EasyMock.expect(instanceRepo.findById(instanceId))
            .andReturn(instance)
            .times(1);

        // An invalid instance ID throws
        EasyMock.expect(instanceRepo.findById(invalidInstanceId))
            .andThrow(new DBNotFoundException("Not Found"))
            .times(1);

        replayMocks();

        DuracloudInstanceManagerServiceImpl managerService =
            new DuracloudInstanceManagerServiceImpl(repoMgr,
                                                    computeProviderUtil);

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
    public void testGetInstanceServices() throws Exception {
        setUpInitComputeProvider();

        int acctId = 1;
        int instanceId = 2;
        
        setUpGetInstanceIds(acctId, instanceId, 1);

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

    private void setUpGetInstanceIds(int accountId,
                                     int instanceId,
                                     int times) throws Exception{
        EasyMock.expect(repoMgr.getInstanceRepo())
            .andReturn(instanceRepo)
            .times(times);

        Set<Integer> instanceIds = new HashSet<Integer>();
        instanceIds.add(instanceId);
        EasyMock.expect(instanceRepo.findByAccountId(accountId))
            .andReturn(instanceIds)
            .times(times);
    }

}
