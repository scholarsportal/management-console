/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.instance.impl;

import junit.framework.Assert;
import org.duracloud.account.util.error.DurabossUpdateException;
import org.duracloud.appconfig.domain.DurabossConfig;
import org.duracloud.common.web.RestHttpHelper;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.apache.commons.httpclient.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.commons.httpclient.HttpStatus.SC_OK;

/**
 * @author Andrew Woods
 *         Date: 4/5/12
 */
public class DurabossUpdaterImplTest {

    private DurabossUpdaterImpl updater;

    private DurabossConfig durabossConfig;
    private RestHttpHelper restHelper;

    private RestHttpHelper.HttpResponse initResponse;

    private static final String host = "host";
    private static final String context = "duraboss";

    @Before
    public void setUp() throws Exception {
        durabossConfig = new DurabossConfig();
        durabossConfig.setDurabossContext(context);
        restHelper = EasyMock.createMock("RestHttpHelper",
                                         RestHttpHelper.class);
        initResponse = EasyMock.createMock("InitHttpResponse",
                                           RestHttpHelper.HttpResponse.class);

        updater = new DurabossUpdaterImpl();
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(restHelper,
                        initResponse);
    }

    private void replayMocks() {
        EasyMock.replay(restHelper,
                        initResponse);
    }

    @Test
    public void testStartDuraboss() throws Exception {
        setExpectations(MODE.START);
        updater.startDuraboss(host, durabossConfig, restHelper);
    }

    @Test
    public void testStartDurabossErrorInit() throws Exception {
        setExpectations(MODE.START_ERROR_INIT);
        boolean threw = false;
        try {
            updater.startDuraboss(host,
                                  durabossConfig,
                                  restHelper);
            Assert.fail("exception expected");

        } catch (DurabossUpdateException e) {
            threw = true;
        }
        Assert.assertTrue(threw);
    }

    @Test
    public void testStartDurabossErrorAction() throws Exception {
        setExpectations(MODE.START_ERROR_ACTION);
        boolean threw = false;
        try {
            updater.startDuraboss(host,
                                  durabossConfig,
                                  restHelper);
            Assert.fail("exception expected");

        } catch (DurabossUpdateException e) {
            threw = true;
        }
        Assert.assertTrue(threw);
    }

    @Test
    public void testStopDuraboss() throws Exception {
        setExpectations(MODE.STOP);
        updater.stopDuraboss(host, durabossConfig, restHelper);
    }

    @Test
    public void testStopDurabossErrorInit() throws Exception {
        setExpectations(MODE.STOP_ERROR_INIT);
        boolean threw = false;
        try {
            updater.stopDuraboss(host, durabossConfig, restHelper);
            Assert.fail("exception expected");

        } catch (DurabossUpdateException e) {
            threw = true;
        }
        Assert.assertTrue(threw);
    }

    @Test
    public void testStopDurabossErrorAction() throws Exception {
        setExpectations(MODE.STOP_ERROR_ACTION);
        boolean threw = false;
        try {
            updater.stopDuraboss(host, durabossConfig, restHelper);
            Assert.fail("exception expected");

        } catch (DurabossUpdateException e) {
            threw = true;
        }
        Assert.assertTrue(threw);
    }

    private void setExpectations(MODE mode) throws Exception {
        String url = getInitUrl();

        EasyMock.expect(restHelper.get(url)).andReturn(initResponse);

        switch (mode) {
            case START:
                EasyMock.expect(initResponse.getStatusCode()).andReturn(
                        SC_OK);
            	break;
            case STOP:
                EasyMock.expect(initResponse.getStatusCode()).andReturn(
                        SC_OK);
            	break;
            case START_ERROR_ACTION:
            case STOP_ERROR_ACTION:
            case START_ERROR_INIT:
            case STOP_ERROR_INIT:
                EasyMock.expect(initResponse.getStatusCode()).andReturn(
                    SC_INTERNAL_SERVER_ERROR);
                break;

            default:
                Assert.fail("Unknown mode: " + mode);
        }

        replayMocks();
    }

    private String getInitUrl() {
        return "https://" + host + ":" + DurabossUpdaterImpl.port + "/" +
            context + durabossConfig.getInitResource();
    }

    private String getActionUrl(String action) {
        return "https://" + host + "/" + context + "/exec/" + action;
    }


    private enum MODE {
        START,
        START_ERROR_ACTION,
        START_ERROR_INIT,
        STOP,
        STOP_ERROR_ACTION,
        STOP_ERROR_INIT;
    }

}
