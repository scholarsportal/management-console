/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.instance.impl;

import junit.framework.Assert;
import org.duracloud.account.common.domain.ServicePlan;
import org.duracloud.account.util.error.DurabossUpdateException;
import org.duracloud.appconfig.domain.DurabossConfig;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.exec.error.InvalidActionRequestException;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.apache.commons.httpclient.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.commons.httpclient.HttpStatus.SC_OK;
import static org.duracloud.execdata.ExecConstants.CANCEL_BIT_INTEGRITY;
import static org.duracloud.execdata.ExecConstants.START_BIT_INTEGRITY;
import static org.duracloud.execdata.ExecConstants.START_STREAMING;
import static org.duracloud.execdata.ExecConstants.STOP_STREAMING;

/**
 * @author Andrew Woods
 *         Date: 4/5/12
 */
public class DurabossUpdaterImplTest {

    private DurabossUpdaterImpl updater;

    private DurabossConfig durabossConfig;
    private ServicePlan servicePlan;
    private RestHttpHelper restHelper;

    private RestHttpHelper.HttpResponse initResponse;
    private RestHttpHelper.HttpResponse bitIntegrityResponse;
    private RestHttpHelper.HttpResponse streamingResponse;

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
        bitIntegrityResponse = EasyMock.createMock("StartHttpResponse",
                                                   RestHttpHelper.HttpResponse.class);
        streamingResponse = EasyMock.createMock("StreamingHttpResponse",
                                                RestHttpHelper.HttpResponse.class);

        updater = new DurabossUpdaterImpl();
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(restHelper,
                        initResponse,
                        bitIntegrityResponse,
                        streamingResponse);
    }

    private void replayMocks() {
        EasyMock.replay(restHelper,
                        initResponse,
                        bitIntegrityResponse,
                        streamingResponse);
    }

    @Test
    public void testStartDuraboss() throws Exception {
        setExpectations(MODE.START);
        updater.startDuraboss(host, durabossConfig, servicePlan, restHelper);
    }

    @Test
    public void testStartDurabossErrorInit() throws Exception {
        setExpectations(MODE.START_ERROR_INIT);
        boolean threw = false;
        try {
            updater.startDuraboss(host,
                                  durabossConfig,
                                  servicePlan,
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
                                  servicePlan,
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
        updater.stopDuraboss(host, durabossConfig, servicePlan, restHelper);
    }

    @Test
    public void testStopDurabossErrorInit() throws Exception {
        setExpectations(MODE.STOP_ERROR_INIT);
        boolean threw = false;
        try {
            updater.stopDuraboss(host, durabossConfig, servicePlan, restHelper);
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
            updater.stopDuraboss(host, durabossConfig, servicePlan, restHelper);
            Assert.fail("exception expected");

        } catch (DurabossUpdateException e) {
            threw = true;
        }
        Assert.assertTrue(threw);
    }

    private void setExpectations(MODE mode) throws Exception {
        String url = getInitUrl();
        String auditUrl = getAuditUrl();
        String bitIntegrityUrl;
        String streamingUrl;

        servicePlan = ServicePlan.PROFESSIONAL;

        EasyMock.expect(restHelper.get(url)).andReturn(initResponse);

        switch (mode) {
            case START:
                EasyMock.expect(initResponse.getStatusCode()).andReturn(SC_OK);

                bitIntegrityUrl = getActionUrl(START_BIT_INTEGRITY);
                streamingUrl = getActionUrl(START_STREAMING);

                EasyMock.expect(restHelper.post(EasyMock.eq(bitIntegrityUrl),
                                                EasyMock.<String>anyObject(),
                                                EasyMock.<Map<String, String>>isNull()))
                        .andReturn(bitIntegrityResponse);

                EasyMock.expect(restHelper.post(EasyMock.eq(streamingUrl),
                                                EasyMock.<String>isNull(),
                                                EasyMock.<Map<String, String>>isNull()))
                        .andReturn(streamingResponse);

                EasyMock.expect(bitIntegrityResponse.getStatusCode()).andReturn(
                    SC_OK);
                EasyMock.expect(streamingResponse.getStatusCode()).andReturn(
                    SC_OK);
                break;

            case STOP:
                EasyMock.expect(initResponse.getStatusCode()).andReturn(SC_OK);

                bitIntegrityUrl = getActionUrl(CANCEL_BIT_INTEGRITY);
                streamingUrl = getActionUrl(STOP_STREAMING);

                EasyMock.expect(restHelper.delete(auditUrl)).andReturn(null);
                EasyMock.expect(restHelper.post(EasyMock.eq(bitIntegrityUrl),
                                                EasyMock.<String>anyObject(),
                                                EasyMock.<Map<String, String>>isNull()))
                        .andReturn(bitIntegrityResponse);

                EasyMock.expect(restHelper.post(EasyMock.eq(streamingUrl),
                                                EasyMock.<String>isNull(),
                                                EasyMock.<Map<String, String>>isNull()))
                        .andReturn(streamingResponse);

                EasyMock.expect(bitIntegrityResponse.getStatusCode()).andReturn(
                    SC_OK);
                EasyMock.expect(streamingResponse.getStatusCode()).andReturn(
                    SC_OK);
                break;

            case START_ERROR_ACTION:
                bitIntegrityUrl = getActionUrl(START_BIT_INTEGRITY);

                EasyMock.expect(initResponse.getStatusCode()).andReturn(SC_OK);

                EasyMock.expect(restHelper.post(EasyMock.eq(bitIntegrityUrl),
                                                EasyMock.<String>anyObject(),
                                                EasyMock.<Map<String, String>>isNull()))
                        .andThrow(new InvalidActionRequestException(
                            "canned exception"));

                break;

            case STOP_ERROR_ACTION:
                bitIntegrityUrl = getActionUrl(CANCEL_BIT_INTEGRITY);

                EasyMock.expect(initResponse.getStatusCode()).andReturn(SC_OK);
                EasyMock.expect(restHelper.delete(auditUrl)).andReturn(null);
                EasyMock.expect(restHelper.post(EasyMock.eq(bitIntegrityUrl),
                                                EasyMock.<String>anyObject(),
                                                EasyMock.<Map<String, String>>isNull()))
                        .andThrow(new InvalidActionRequestException(
                            "canned exception"));
                break;

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

    private String getAuditUrl() {
        return "https://" + host + "/" + context + "/audit";
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
