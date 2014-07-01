/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.instance.util.impl;

import org.duracloud.account.monitor.instance.domain.InstanceInfo;
import org.duracloud.account.monitor.instance.util.InstanceUtil;
import org.duracloud.common.web.RestHttpHelper;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: 7/15/11
 */
public class InstanceUtilImplTest {

    private InstanceUtil util;

    private String subdomain = "subdomain";

    private RestHttpHelper restHelper;

    @Before
    public void setUp() throws Exception {
        restHelper = EasyMock.createMock("RestHttpHelper",
                                         RestHttpHelper.class);

        util = new InstanceUtilImpl(subdomain, restHelper);
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(restHelper);
    }

    private void replayMocks() {
        EasyMock.replay(restHelper);
    }

    @Test
    public void testPingWebApps() throws Exception {
        doTestPingWebApps(true);
    }

    @Test
    public void testPingWebAppsError() throws Exception {
        doTestPingWebApps(false);
    }

    private void doTestPingWebApps(boolean valid) throws Exception {
        createMockExpectations(valid);
        replayMocks();

        InstanceInfo info = util.pingWebApps();
        Assert.assertNotNull(info);

        Assert.assertEquals(!valid, info.hasErrors());
    }

    private void createMockExpectations(boolean valid) throws Exception {
        for (PING ping : PING.values()) {
            EasyMock.expect(restHelper.get(ping.url))
                .andReturn(ping.getResponse(valid));
        }
    }

    private enum PING {
        STORE(200, 503, "https://subdomain.duracloud.org:443/durastore/init"),
        BOSS(200, 503, "https://subdomain.duracloud.org:443/duraboss/init"),
        ADMIN(200, 503, "https://subdomain.duracloud.org:443/duradmin/init");

        private int success;
        private int failure;
        private String url;

        private PING(int success, int failure, String url) {
            this.success = success;
            this.failure = failure;
            this.url = url;
        }

        public RestHttpHelper.HttpResponse getResponse(boolean valid) {
            int statusCode = valid ? success : failure;
            return new RestHttpHelper.HttpResponse(statusCode,
                                                   null,
                                                   null,
                                                   null);
        }
    }

}
