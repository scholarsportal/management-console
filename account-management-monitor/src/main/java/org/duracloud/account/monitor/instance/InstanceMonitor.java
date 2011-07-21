/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.instance;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.monitor.instance.domain.InstanceReport;
import org.duracloud.account.monitor.instance.util.InstanceUtil;
import org.duracloud.account.monitor.instance.util.InstanceUtilFactory;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.common.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class manages the actual monitoring of instances across all managed
 * DuraCloud accounts.
 *
 * @author Andrew Woods
 *         Date: 7/11/11
 */
public class InstanceMonitor {

    private Logger log = LoggerFactory.getLogger(InstanceMonitor.class);

    private DuracloudAccountRepo acctRepo;
    private DuracloudInstanceRepo instanceRepo;
    private InstanceUtilFactory instanceUtilFactory;

    public InstanceMonitor(DuracloudAccountRepo acctRepo,
                           DuracloudInstanceRepo instanceRepo,
                           InstanceUtilFactory factory) {
        this.acctRepo = acctRepo;
        this.instanceRepo = instanceRepo;
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

        List<AccountInfo> accts = getDuracloudAcctsHavingInstances();
        for (AccountInfo acct : accts) {
            doMonitorInstances(report, acct);
        }

        return report;
    }

    private void doMonitorInstances(InstanceReport report, AccountInfo acct) {
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

    private List<AccountInfo> getDuracloudAcctsHavingInstances() {
        List<AccountInfo> acctsHavingInstances = new ArrayList<AccountInfo>();
        List<AccountInfo> allAccts = getDuracloudAccts();

        for (AccountInfo acct : allAccts) {
            Set<Integer> result = null;
            try {
                result = instanceRepo.findByAccountId(acct.getId());

            } catch (DBNotFoundException e) {
                StringBuilder sb = new StringBuilder("No instance found ");
                sb.append("for account id ");
                sb.append(acct.getId());
                sb.append(" (");
                sb.append(acct.getSubdomain());
                sb.append(")");
                log.warn(sb.toString());
            }

            if (null != result && result.size() != 0) {
                acctsHavingInstances.add(acct);
            }
        }
        return acctsHavingInstances;
    }

    private List<AccountInfo> getDuracloudAccts() {
        List<AccountInfo> acctInfos = new ArrayList<AccountInfo>();

        Set<Integer> ids = acctRepo.getIds();
        for (int id : ids) {
            try {
                acctInfos.add(acctRepo.findById(id));

            } catch (DBNotFoundException e) {
                StringBuilder error = new StringBuilder("Error getting ");
                error.append("account with id ");
                error.append(id);
                log.error(error.toString());
                throw new DuraCloudRuntimeException(error.toString(), e);
            }
        }
        return acctInfos;
    }

}
