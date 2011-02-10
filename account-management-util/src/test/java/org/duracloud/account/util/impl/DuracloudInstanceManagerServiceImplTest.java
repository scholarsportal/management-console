/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.util.DuracloudInstanceService;
import org.easymock.classextension.EasyMock;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author: Bill Branan
 * Date: 2/9/11
 */
public class DuracloudInstanceManagerServiceImplTest
    extends DuracloudInstanceServiceTestBase {

    @Test
    public void testGetInstanceService() throws Exception {
        setUpInitComputeProvider();

        EasyMock.expect(repoMgr.getInstanceRepo())
            .andReturn(instanceRepo)
            .times(1);
        EasyMock.expect(instanceRepo.findById(EasyMock.anyInt()))
            .andReturn(instance)
            .times(1);

        replayMocks();

        DuracloudInstanceManagerServiceImpl managerService =
            new DuracloudInstanceManagerServiceImpl(repoMgr);
        int acctId = 1;
        int instanceId = 2;
        DuracloudInstanceService instanceService =
            managerService.getInstanceService(acctId, instanceId);
        assertNotNull(instanceService);
    }

    @Test
    public void testGetInstanceServices() throws Exception {
        setUpInitComputeProvider();

        EasyMock.expect(repoMgr.getInstanceRepo())
            .andReturn(instanceRepo)
            .times(2);
        EasyMock.expect(instanceRepo.findById(EasyMock.anyInt()))
            .andReturn(instance)
            .times(1);

        Set<Integer> ids = new HashSet<Integer>();
        ids.add(1);
        EasyMock.expect(instanceRepo.getIds())
            .andReturn(ids)
            .times(1);

        replayMocks();

        DuracloudInstanceManagerServiceImpl managerService =
            new DuracloudInstanceManagerServiceImpl(repoMgr);
        int acctId = 1;
        Set<DuracloudInstanceService> instanceServices =
            managerService.getInstanceServices(acctId);
        assertNotNull(instanceServices);
        assertEquals(1, instanceServices.size());
    }
}
