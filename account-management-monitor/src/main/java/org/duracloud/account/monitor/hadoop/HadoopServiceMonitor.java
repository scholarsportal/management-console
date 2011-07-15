/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.hadoop;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudStorageProviderAccountRepo;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.monitor.error.HadoopMonitorException;
import org.duracloud.account.monitor.error.HadoopNotActivatedException;
import org.duracloud.account.monitor.error.UnsupportedStorageProviderException;
import org.duracloud.account.monitor.hadoop.domain.HadoopServiceInfo;
import org.duracloud.account.monitor.hadoop.domain.HadoopServiceReport;
import org.duracloud.account.monitor.hadoop.util.HadoopUtil;
import org.duracloud.account.monitor.hadoop.util.HadoopUtilFactory;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.common.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * This class manages the actual monitoring of Hadoop services across all
 * managed DuraCloud accounts.
 *
 * @author Andrew Woods
 *         Date: 7/11/11
 */
public class HadoopServiceMonitor {

    private Logger log = LoggerFactory.getLogger(HadoopServiceMonitor.class);

    private DuracloudAccountRepo acctRepo;
    private DuracloudStorageProviderAccountRepo storageProviderAcctRepo;
    private HadoopUtilFactory hadoopUtilFactory;


    public HadoopServiceMonitor(DuracloudAccountRepo acctRepo,
                                DuracloudStorageProviderAccountRepo storageProviderAcctRepo,
                                HadoopUtilFactory factory) {
        this.acctRepo = acctRepo;
        this.storageProviderAcctRepo = storageProviderAcctRepo;
        this.hadoopUtilFactory = factory;
    }

    /**
     * This method is the entry point for monitoring Hadoop services.
     *
     * @param thresholdDays limit over which services will be flagged as
     *                      "long-running"
     * @return report of all services and any errors
     */
    public HadoopServiceReport monitorServices(int thresholdDays) {
        log.info("starting monitor with threshold: {}", thresholdDays);
        HadoopServiceReport report = new HadoopServiceReport(thresholdDays);

        List<AccountInfo> accts = getDuracloudAccts();
        for (AccountInfo acct : accts) {
            doMonitorServices(report, acct);
        }

        return report;
    }

    private List<AccountInfo> getDuracloudAccts() {
        List<AccountInfo> acctInfos = new ArrayList<AccountInfo>();

        Set<Integer> ids = acctRepo.getIds();
        for (int id : ids) {
            try {
                acctInfos.add(acctRepo.findById(id));

            } catch (DBNotFoundException e) {
                StringBuilder error = new StringBuilder("Error getting ");
                error.append("with account id ");
                error.append(id);
                log.error(error.toString());
                throw new DuraCloudRuntimeException(error.toString(), e);
            }
        }
        return acctInfos;
    }

    private void doMonitorServices(HadoopServiceReport report,
                                   AccountInfo acct) {
        log.info("monitoring: {} ({})",
                 acct.getAcctName(),
                 acct.getSubdomain());

        HadoopUtil hadoopUtil;
        try {
            hadoopUtil = getHadoopUtil(acct);

        } catch (Exception e) {
            StringBuilder error = new StringBuilder();
            error.append(e.getMessage());
            log.error(error.toString());
            log.error("stacktrace:", e);

            report.addAcctError(acct, error.toString());
            return;
        }

        try {
            report.addAcctServices(acct, monitorServicesRunning(hadoopUtil));
            report.addAcctServices(acct, monitorServicesCompleted(hadoopUtil));

        } catch (HadoopMonitorException e) {
            StringBuilder error = new StringBuilder("Error ");
            error.append("retrieving services for account: ");
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

    private Set<HadoopServiceInfo> monitorServicesRunning(HadoopUtil hadoopUtil)
        throws HadoopMonitorException {
        Calendar twoMonthsAgo = Calendar.getInstance();
        twoMonthsAgo.roll(Calendar.MONTH, -2);
        twoMonthsAgo.roll(Calendar.DATE, 1);
        return monitorServicesCreatedAfter(twoMonthsAgo.getTime(),
                                           HadoopUtil.STATE.RUNNING,
                                           hadoopUtil);
    }

    private Set<HadoopServiceInfo> monitorServicesCompleted(HadoopUtil hadoopUtil)
        throws HadoopMonitorException {
        Calendar yesterday = Calendar.getInstance();
        yesterday.roll(Calendar.DATE, -1);

        return monitorServicesCreatedAfter(yesterday.getTime(),
                                           HadoopUtil.STATE.COMPLETED,
                                           hadoopUtil);
    }

    private Set<HadoopServiceInfo> monitorServicesCreatedAfter(Date after,
                                                               HadoopUtil.STATE state,
                                                               HadoopUtil hadoopUtil)
        throws HadoopMonitorException {
        try {
            return hadoopUtil.getServicesCreatedAfter(after, state);

        } catch (Exception e) {
            StringBuilder error = new StringBuilder();
            error.append("Error getting services, after: ");
            error.append(after);
            error.append(" with state: ");
            error.append(state);
            error.append(" with msg: ");
            error.append(e.getMessage());
            log.warn(error.toString());
            throw new HadoopMonitorException(error.toString(), e);
        }
    }

    private HadoopUtil getHadoopUtil(AccountInfo acct)
        throws HadoopMonitorException {
        int storageAcctId = acct.getPrimaryStorageProviderAccountId();
        StorageProviderAccount storageAcct;
        try {
            storageAcct = getAmazonStorageAcct(storageAcctId);

        } catch (DBNotFoundException e) {
            StringBuilder error = new StringBuilder();
            error.append("Error getting Amazon storage acct for ");
            error.append(acct.getSubdomain());
            error.append(", id ");
            error.append(storageAcctId);
            error.append(", msg: ");
            error.append(e.getMessage());
            throw new HadoopMonitorException(error.toString(), e);
        }

        HadoopUtil hadoopUtil;
        try {
            hadoopUtil = hadoopUtilFactory.getHadoopUtil(storageAcct);

        } catch (UnsupportedStorageProviderException e) {
            StringBuilder error = new StringBuilder();
            error.append("Unsupported storage provider for ");
            error.append(acct.getSubdomain());
            error.append(", msg: ");
            error.append(e.getMessage());
            throw new HadoopMonitorException(error.toString(), e);
        }

        try {
            hadoopUtil.verifyActivated();

        } catch (HadoopNotActivatedException e) {
            StringBuilder error = new StringBuilder();
            error.append("Hadoop capability not activated for ");
            error.append(acct.getSubdomain());
            error.append(", msg: ");
            error.append(e.getMessage());
            throw new HadoopMonitorException(error.toString(), e);
        }

        return hadoopUtil;
    }

    private StorageProviderAccount getAmazonStorageAcct(int storageAcctId)
        throws DBNotFoundException {
        return storageProviderAcctRepo.findById(storageAcctId);
    }

}
