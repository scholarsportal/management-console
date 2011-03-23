/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.common.domain;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: 3/21/11
 */
public class AmaEndpointTest {

    @Test
    public void testInitialize() throws Exception {
        String defaultHost = AmaEndpoint.getHost();
        String defaultPort = AmaEndpoint.getPort();
        String defaultCtxt = AmaEndpoint.getCtxt();
        verifyEndpoint(defaultHost, defaultPort, defaultCtxt);

        String host = "host";
        String port = "1234";
        String ctxt = "ctxt";
        AmaEndpoint.initialize(host, port, ctxt);
        verifyEndpoint(host, port, ctxt);
    }

    private void verifyEndpoint(String host, String port, String ctxt) {
        Assert.assertEquals(host, AmaEndpoint.getHost());
        Assert.assertEquals(port, AmaEndpoint.getPort());
        Assert.assertEquals(ctxt, AmaEndpoint.getCtxt());
    }
}
