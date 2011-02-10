/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.DuracloudInstance;
import org.easymock.classextension.EasyMock;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author: Bill Branan
 * Date: 2/9/11
 */
public class DuracloudInstanceServiceImplTest
    extends DuracloudInstanceServiceTestBase {

    @Test
    public void testGetInstanceInfo() throws Exception {
        replayMocks();

        DuracloudInstance instanceInfo = service.getInstanceInfo();
        assertNotNull(instanceInfo);
        assertEquals(instance, instanceInfo);
    }

    @Test
    public void testGetStatus() throws Exception {
        String status = "status";
        EasyMock.expect(computeProvider.getStatus(EasyMock.isA(String.class)))
            .andReturn(status)
            .times(1);
        EasyMock.expect(instance.getProviderInstanceId())
            .andReturn("id")
            .times(1);

        replayMocks();

        String resultStatus = service.getStatus();
        assertNotNull(resultStatus);
        assertEquals(status, resultStatus);
    }

    @Test
    public void testStop() throws Exception {
        computeProvider.stop(EasyMock.isA(String.class));
        EasyMock.expectLastCall()
            .times(1);
        EasyMock.expect(instance.getProviderInstanceId())
            .andReturn("id")
            .times(1);

        replayMocks();

        service.stop();
    }

    @Test
    public void testRestart() throws Exception {
        computeProvider.restart(EasyMock.isA(String.class));
        EasyMock.expectLastCall()
            .times(1);
        EasyMock.expect(instance.getProviderInstanceId())
            .andReturn("id")
            .times(1);

        replayMocks();

        service.restart();
    }

    @Test
    public void testInitializeComputeProvider() throws Exception {
        setUpInitComputeProvider();
        replayMocks();

        service = new DuracloudInstanceServiceImpl(accountId,
                                                   instance,
                                                   repoMgr);
    }

}
