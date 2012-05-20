/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.storereporter;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.ServerImage;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.DuracloudServerImageRepo;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.monitor.storereporter.domain.StoreReporterReport;
import org.duracloud.account.monitor.storereporter.util.StoreReporterUtil;
import org.duracloud.account.monitor.storereporter.util.StoreReporterUtilFactory;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.common.model.Credential;
import org.duracloud.common.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class manages the actual monitoring of Storage Reporters across all
 * managed DuraCloud accounts.
 *
 * @author Andrew Woods
 *         Date: 5/18/12
 */
public class StoreReporterMonitor {

    private Logger log = LoggerFactory.getLogger(StoreReporterMonitor.class);

    private DuracloudAccountRepo acctRepo;
    private DuracloudInstanceRepo instanceRepo;
    private DuracloudServerImageRepo imageRepo;
    private StoreReporterUtilFactory reporterUtilFactory;

    public StoreReporterMonitor(DuracloudAccountRepo acctRepo,
                                DuracloudInstanceRepo instanceRepo,
                                DuracloudServerImageRepo imageRepo,
                                StoreReporterUtilFactory factory) {
        this.acctRepo = acctRepo;
        this.instanceRepo = instanceRepo;
        this.imageRepo = imageRepo;
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

        List<AccountInfo> accts = getDuracloudAcctsHavingInstances();
        for (AccountInfo acct : accts) {
            doMonitorStoreReporters(report, acct);
        }

        return report;
    }

    private void doMonitorStoreReporters(StoreReporterReport report,
                                         AccountInfo acct) {
        log.info("monitoring store-reporter: {} ({})",
                 acct.getAcctName(),
                 acct.getSubdomain());
        try {
            Credential credential = getRootCredential(acct);
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

    private Credential getRootCredential(AccountInfo acct)
        throws DBNotFoundException {
        ServerImage serverImage = findServerImage(acct);
        String rootPassword = serverImage.getDcRootPassword();
        return new Credential(ServerImage.DC_ROOT_USERNAME, rootPassword);
    }

    private ServerImage findServerImage(AccountInfo acct)
        throws DBNotFoundException {
        Set<Integer> instanceIds = instanceRepo.findByAccountId(acct.getId());
        int instanceId = instanceIds.iterator().next();
        DuracloudInstance instance = instanceRepo.findById(instanceId);

        return imageRepo.findById(instance.getImageId());
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
                log.info(sb.toString());
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
