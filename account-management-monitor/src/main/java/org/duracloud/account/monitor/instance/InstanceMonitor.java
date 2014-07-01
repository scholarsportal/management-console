/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.instance;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.DuracloudInstance;
import org.duracloud.account.db.repo.DuracloudAccountRepo;
import org.duracloud.account.db.repo.DuracloudInstanceRepo;
import org.duracloud.account.monitor.common.BaseMonitor;
import org.duracloud.account.monitor.instance.domain.InstanceReport;
import org.duracloud.account.monitor.instance.util.InstanceUtil;
import org.duracloud.account.monitor.instance.util.InstanceUtilFactory;
import org.duracloud.common.util.ExceptionUtil;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This class manages the actual monitoring of instances across all managed
 * DuraCloud accounts.
 *
 * @author Andrew Woods
 *         Date: 7/11/11
 */
public class InstanceMonitor extends BaseMonitor {

    private InstanceUtilFactory instanceUtilFactory;

    public InstanceMonitor(DuracloudAccountRepo acctRepo,
                           DuracloudInstanceRepo instanceRepo,
                           InstanceUtilFactory factory) {
        this.log = LoggerFactory.getLogger(InstanceMonitor.class);
        super.init(acctRepo, instanceRepo, null);
        this.instanceUtilFactory = factory;
    }

    /**
     * This method performs the monitoring.
     *
     * @return instance report
     */
    public InstanceReport monitorInstances() {
        log.info("starting monitor");
        InstanceReport report = new InstanceReport();

        List<DuracloudInstance> instances = getDuracloudInstances();
        for (DuracloudInstance instance : instances) {
            doMonitorInstances(report, instance);
        }

        return report;
    }

    private void doMonitorInstances(InstanceReport report,
                                    DuracloudInstance instance) {
        AccountInfo acct = instance.getAccount();
        log.info("monitoring instance: {} ({})",
                 acct.getAcctName(),
                 acct.getSubdomain());

        InstanceUtil instanceUtil = instanceUtilFactory.getInstanceUtil(acct);
        try {
            report.addAcctInstance(acct, instanceUtil.pingWebApps());

        } catch (Exception e) {
            StringBuilder error = new StringBuilder("Error ");
            error.append("monitoring instance for account: ");
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
