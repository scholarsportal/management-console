/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.compute;

import org.easymock.EasyMock;
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
