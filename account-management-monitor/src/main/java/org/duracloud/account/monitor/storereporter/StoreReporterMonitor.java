/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.storereporter;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.AccountInfo.AccountStatus;
import org.duracloud.account.db.repo.DuracloudAccountRepo;
import org.duracloud.account.db.util.GlobalPropertiesConfigService;
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
                                GlobalPropertiesConfigService configService,
                                StoreReporterUtilFactory factory) {
        this.log = LoggerFactory.getLogger(StoreReporterMonitor.class);
        super.init(acctRepo, configService);
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

        List<AccountInfo> accounts = this.acctRepo.findAll();
        for (AccountInfo info : accounts) {
            if(info.getStatus().equals(AccountStatus.ACTIVE)){
                doMonitorStoreReporters(report, info);
            }
        }

        return report;
    }

    private void doMonitorStoreReporters(StoreReporterReport report, AccountInfo accountInfo) {
        log.info("monitoring store-reporter: {} ({})",
                accountInfo.getAcctName(),
                accountInfo.getSubdomain());
        try {
            Credential credential = getRootCredential();
            StoreReporterUtil reporterUtil =
                reporterUtilFactory.getStoreReporterUtil(accountInfo, credential);
            report.addAcctInfo(accountInfo, reporterUtil.pingStorageReporter());

        } catch (Exception e) {
            StringBuilder error = new StringBuilder("Error ");
            error.append("monitoring store-reporter for account: ");
            error.append(accountInfo.getSubdomain());
            error.append("\n");
            error.append("msg: \n");
            error.append(e.getMessage());
            error.append("\n");
            error.append("stack trace: \n");
            error.append(ExceptionUtil.getStackTraceAsString(e));
            log.error(error.toString());

            report.addAcctError(accountInfo, error.toString());
        }
    }

}
