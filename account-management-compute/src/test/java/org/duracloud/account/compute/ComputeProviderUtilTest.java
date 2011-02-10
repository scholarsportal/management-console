/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.compute;

import org.easymock.classextension.EasyMock;
import org.junit.Test;

import static junit.framework.Assert.assertNull;

/**
 * @author: Bill Branan
 * Date: 2/10/11
 */
public class ComputeProviderUtilTest {

    @Test
    public void testGetComputeProvider() throws Exception {
        String username = "user";
        String password = "pass";

        ComputeProviderFactory factory =
            EasyMock.createMock(ComputeProviderFactory.class);
        EasyMock.expect(factory.createComputeProvider(username, password))
            .andReturn(null)
            .times(1);

        EasyMock.replay(factory);

        ComputeProviderUtil util = new ComputeProviderUtil(factory);
        DuracloudComputeProvider provider =
            util.getComputeProvider(username, password);
        assertNull(provider);

        EasyMock.verify(factory);
    }
}
