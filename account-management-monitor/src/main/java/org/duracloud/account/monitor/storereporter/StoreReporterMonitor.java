/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.storereporter;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.DuracloudInstance;
import org.duracloud.account.db.repo.DuracloudAccountRepo;
import org.duracloud.account.db.repo.DuracloudInstanceRepo;
import org.duracloud.account.db.repo.DuracloudServerImageRepo;
import org.duracloud.account.monitor.common.BaseMonitor;
import org.duracloud.account.monitor.storereporter.domain.StoreReporterReport;
import org.duracloud.account.monitor.storereporter.util.StoreReporterUtil;
import org.duracloud.account.monitor.storereporter.util.StoreReporterUtilFactory;
import org.duracloud.common.model.Credential;
import org.duracloud.common.util.ExceptionUtil;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This class manages the actual monitoring of Storage Reporters across all
 * managed DuraCloud accounts.
 *
 * @author Andrew Woods
 *         Date: 5/18/12
 */
public class StoreReporterMonitor extends BaseMonitor {

    private StoreReporterUtilFactory reporterUtilFactory;

    public StoreReporterMonitor(DuracloudAccountRepo acctRepo,
                                DuracloudInstanceRepo instanceRepo,
                                DuracloudServerImageRepo imageRepo,
                                StoreReporterUtilFactory factory) {
        this.log = LoggerFactory.getLogger(StoreReporterMonitor.class);
        super.init(acctRepo, instanceRepo, imageRepo);
        this.reporterUtilFactory = factory;
    }

    /**
     * This method performs the monitoring.
     *
     * @return StoreReporterReport report
     */
    public StoreReporterReport monitorStoreReporters() {
        log.info("starting store-reporter monitor");
        StoreReporterReport report = new StoreReporterReport();

        List<DuracloudInstance> instances = getDuracloudInstances();
        for (DuracloudInstance instance : instances) {
            doMonitorStoreReporters(report, instance);
        }

        return report;
    }

    private void doMonitorStoreReporters(StoreReporterReport report,
                                         DuracloudInstance instance) {
        AccountInfo acct = instance.getAccount();
        log.info("monitoring store-reporter: {} ({})",
                 acct.getAcctName(),
                 acct.getSubdomain());
        try {
            Credential credential = getRootCredential(instance);
            StoreReporterUtil reporterUtil =
                reporterUtilFactory.getStoreReporterUtil(acct, credential);
            report.addAcctInfo(acct, reporterUtil.pingStorageReporter());

        } catch (Exception e) {
            StringBuilder error = new StringBuilder("Error ");
            error.append("monitoring store-reporter for account: ");
            error.append(acct.getSubdomain());
            error.append("\n");
            error.append("msg: \n");
            error.append(e.getMessage());
            error.append("\n");
            error.append("stack trace: \n");
            error.append(ExceptionUtil.getStackTraceAsString(e));
            log.error(error.toString());

            report.addAcctError(acct, error.toString());
        }
    }

}
