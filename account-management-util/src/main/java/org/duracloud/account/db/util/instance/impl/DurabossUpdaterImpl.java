/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.instance.impl;

import org.apache.commons.httpclient.HttpStatus;
import org.duracloud.account.db.model.ServicePlan;
import org.duracloud.account.db.util.instance.DurabossUpdater;
import org.duracloud.account.db.util.error.DurabossUpdateException;
import org.duracloud.appconfig.domain.DurabossConfig;
import org.duracloud.audit.Auditor;
import org.duracloud.audit.error.AuditLogNotFoundException;
import org.duracloud.client.exec.ExecutorImpl;
import org.duracloud.common.util.CalendarUtil;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.exec.Executor;
import org.duracloud.exec.error.ExecutorException;
import org.duracloud.exec.error.InvalidActionRequestException;
import org.duracloud.execdata.ExecConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.duracloud.common.util.CalendarUtil.DAY_OF_WEEK.SAT;

/**
 * This class manages updating the DuraBoss actions.
 *
 * @author Andrew Woods
 *         Date: 4/5/12
 */
public class DurabossUpdaterImpl implements DurabossUpdater {

    private Logger log = LoggerFactory.getLogger(DurabossUpdaterImpl.class);

    protected final static String port = "443";

    private Executor executor;
    private Auditor auditor;

    @Override
    public void startDuraboss(String host,
                              DurabossConfig durabossConfig,
                              ServicePlan servicePlan,
                              RestHttpHelper restHelper) {
        // Verify duraboss has been initialized.
        if (!isInitialized(host, durabossConfig, restHelper)) {
            throw new DurabossUpdateException(host, "not initialized");
        }

        Executor executor = getExecutor(host, restHelper);

        // Start BitIntegrity handler.
        startBitIntegrity(executor);

        // Start MediaStreaming handler.
        startMediaStreaming(executor);
    }

    private void startBitIntegrity(Executor executor) {
        long startTime = new CalendarUtil().getDateAtOneAmNext(SAT).getTime();
        long frequency = CalendarUtil.ONE_WEEK_MILLIS * 4;

        String action = ExecConstants.START_BIT_INTEGRITY;
        String params = startTime + "," + frequency;

        log.info("Starting bit-integrity at: {}, every: {} millis.",
                 startTime,
                 frequency);
        performAction(executor, action, params);
    }

    private void startMediaStreaming(Executor executor) {
        String action = ExecConstants.START_STREAMING;
        String params = null;

        log.info("Starting media-streaming.");
        performAction(executor, action, params);
    }

    @Override
    public void stopDuraboss(String host,
                             DurabossConfig durabossConfig,
                             ServicePlan servicePlan,
                             RestHttpHelper restHelper) {
        // Verify duraboss has been initialized.
        if (!isInitialized(host, durabossConfig, restHelper)) {
            throw new DurabossUpdateException(host, "not initialized");
        }

        Auditor auditor = getAuditor(host, restHelper);
        stopAuditor(auditor);

        Executor executor = getExecutor(host, restHelper);

        // Start BitIntegrity handler.
        stopBitIntegrity(executor);

        // Start MediaStreaming handler.
        stopMediaStreaming(executor);
    }

    private void stopAuditor(Auditor auditor) {
        log.info("Stopping auditor.");
        auditor.stop();
    }

    private void stopBitIntegrity(Executor executor) {
        String action = ExecConstants.CANCEL_BIT_INTEGRITY;
        String params = null;

        log.info("Stopping bit-integrity.");
        performAction(executor, action, params);
    }

    private void stopMediaStreaming(Executor executor) {
        String action = ExecConstants.STOP_STREAMING;
        String params = null;

        log.info("Stopping media-streaming.");
        performAction(executor, action, params);
    }

    private void performAction(Executor executor,
                               String action,
                               String params) {
        try {
            executor.performAction(action, params);

        } catch (ExecutorException e) {
            String err = "Error executing action: " + action;
            log.error(err, action, e);
            throw new DurabossUpdateException(err, e);

        } catch (InvalidActionRequestException e) {
            String err = "Unexpected invalid action: " + action;
            log.error(err, action, e);
            throw new DurabossUpdateException(err, e);
        }
    }

    private boolean isInitialized(String host,
                                  DurabossConfig config,
                                  RestHttpHelper restHelper) {
        String url = getInitUrl(host, config);
        try {
            return restHelper.get(url).getStatusCode() == HttpStatus.SC_OK;

        } catch (Exception e) {
            log.warn("Error checking host: " + host + ", " + e.getMessage());
            return false;
        }
    }

    private String getInitUrl(String host, DurabossConfig config) {
        StringBuilder url = new StringBuilder("https://");
        url.append(host);
        url.append(":");
        url.append(port);
        url.append("/");
        url.append(config.getDurabossContext());
        url.append(config.getInitResource());
        return url.toString();
    }

    private Executor getExecutor(String host, RestHttpHelper restHelper) {
        if (null == executor) {
            this.executor = new ExecutorImpl(host, port, restHelper);
        }
        return executor;
    }

    private Auditor getAuditor(final String host,
                               final RestHttpHelper restHelper) {
        if (null == auditor) {
            // FIXME: This should be replaced with a yet to be created Auditor
            //        client.
            this.auditor = new Auditor() {
                @Override
                public void createInitialAuditLogs(boolean async) {
                    throw new UnsupportedOperationException("createAuditLogs");
                }

                @Override
                public List<String> getAuditLogs(String spaceId)
                    throws AuditLogNotFoundException {
                    throw new UnsupportedOperationException("getAuditLogs");
                }

                @Override
                public void stop() {
                    String url = "https://" + host + "/duraboss/audit";
                    try {
                        restHelper.delete(url);

                    } catch (Exception e) {
                        log.warn("Error stopping the Auditor at: {}", url, e);
                    }
                }
            };
        }
        return auditor;
    }

}
