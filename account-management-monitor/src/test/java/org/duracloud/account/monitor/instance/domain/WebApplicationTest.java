/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.instance.domain;

import org.duracloud.account.monitor.error.UnexpectedResponseException;
import org.duracloud.common.web.RestHttpHelper;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.net.ssl.HostnameVerifier;

/**
 * @author Andrew Woods
 *         Date: 7/18/11
 */
public class WebApplicationTest {

    private WebApplication app;

    private static final String HOST = "host";
    private static final String PORT = "443";
    private static final String CTXT = "context";

    private RestHttpHelper restHelper;

    @Before
    public void setUp() throws Exception {
        restHelper = EasyMock.createMock("RestHttpHelper",
                                         RestHttpHelper.class);

        app = new WebApplication(HOST, PORT, CTXT, restHelper);
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(restHelper);
    }

    private void replayMocks() {
        EasyMock.replay(restHelper);
    }

    @Test
    public void testPing() throws Exception {
        int statusCode = 401;

        createMockExpectations(statusCode);
        replayMocks();

        String path = "path";
        app.ping(path, statusCode);
    }

    @Test
    public void testPingError() throws Exception {
        int statusCode = 401;
        int errorCode = 500;

        createMockExpectations(errorCode);
        replayMocks();

        boolean thrown = false;
        try {
            String path = "path";
            app.ping(path, statusCode);
            Assert.fail("exception expected");

        } catch (UnexpectedResponseException e) {
            thrown = true;
        }
        Assert.assertTrue(thrown);
    }

    private void createMockExpectations(int statusCode) throws Exception {
        RestHttpHelper.HttpResponse response = new RestHttpHelper.HttpResponse(
            statusCode,
            null,
            null,
            null);

        EasyMock.expect(restHelper.get(EasyMock.<String>anyObject())).andReturn(
            response);
    }
    
}
