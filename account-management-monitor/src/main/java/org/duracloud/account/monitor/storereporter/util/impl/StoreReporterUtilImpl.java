/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.storereporter.util.impl;

import org.duracloud.account.monitor.storereporter.domain.StoreReporterInfo;
import org.duracloud.account.monitor.storereporter.util.StoreReporterUtil;
import org.duracloud.client.report.StorageReportManager;
import org.duracloud.client.report.StorageReportManagerImpl;
import org.duracloud.client.report.error.ReportException;
import org.duracloud.common.model.Credential;
import org.duracloud.common.util.ExceptionUtil;
import org.duracloud.reportdata.storage.StorageReportInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * @author Andrew Woods
 *         Date: 5/18/12
 */
public class StoreReporterUtilImpl implements StoreReporterUtil {

    private Logger log = LoggerFactory.getLogger(StoreReporterUtilImpl.class);

    private StorageReportManager reportManager;

    private String subdomain;
    private int thresholdDays;

    private static final String DOMAIN = ".duracloud.org";
    private static final String PORT = "443";
    private static final String CTXT_BOSS = "duraboss";

    public StoreReporterUtilImpl(String subdomain,
                                 Credential credential,
                                 int thresholdDays) {
        this(subdomain, credential, thresholdDays, null);
    }

    public StoreReporterUtilImpl(String subdomain,
                                 Credential credential,
                                 int thresholdDays,
                                 StorageReportManager reportManager) {
        if (null == reportManager) {
            String host = subdomain + DOMAIN;
            reportManager = new StorageReportManagerImpl(host, PORT, CTXT_BOSS);
            reportManager.login(credential);
        }

        this.subdomain = subdomain;
        this.thresholdDays = thresholdDays;
        this.reportManager = reportManager;
    }

    @Override
    public StoreReporterInfo pingStorageReporter() {
        StorageReportInfo reportInfo = null;
        StringBuilder error = new StringBuilder();

        // Get current status of Storage Reporter.
        try {
            reportInfo = reportManager.getStorageReportInfo();

        } catch (ReportException e) {
            error.append(e.getMessage());
            error.append("\n");
            error.append(ExceptionUtil.getStackTraceAsString(e));
            log.error("Error StoreReportUtil.pingStorageReporter: {}", error);
        }

        // Was any status returned?
        if (null == reportInfo) {
            error.append("Unable to collect StorageReportInfo: null");

        } else {
            // Determine if the Storage Reporter has hung.
            long nextStart = reportInfo.getNextScheduledStartTime();
            if (isThresholdDaysAgo(nextStart)) {
                error.append("Next scheduled storage report is more than ");
                error.append(thresholdDays);
                error.append(" days AGO.");
                error.append("\n");
            }
        }

        // Collect result.
        StoreReporterInfo result = new StoreReporterInfo(subdomain);
        if (error.length() > 0) {
            result.setError(error.toString());

        } else {
            result.setSuccess();
        }

        return result;
    }

    /**
     * @param timeMillis of date in question
     * @return true if arg timeMillis is greater than threshold days ago
     */
    private boolean isThresholdDaysAgo(long timeMillis) {
        Calendar nextDate = Calendar.getInstance();
        nextDate.setTimeInMillis(timeMillis);

        Calendar thresholdDaysAgo = Calendar.getInstance();
        thresholdDaysAgo.add(Calendar.DATE, -thresholdDays);

        return nextDate.before(thresholdDaysAgo);
    }

}
