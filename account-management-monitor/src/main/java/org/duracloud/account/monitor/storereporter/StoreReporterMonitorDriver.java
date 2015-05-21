/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.storereporter;

import org.apache.commons.io.IOUtils;
import org.duracloud.account.db.repo.DuracloudAccountRepo;
import org.duracloud.account.db.repo.DuracloudInstanceRepo;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.repo.DuracloudServerImageRepo;
import org.duracloud.account.monitor.MonitorsDriver;
import org.duracloud.account.monitor.storereporter.domain.StoreReporterReport;
import org.duracloud.account.monitor.storereporter.util.StoreReporterUtilFactory;
import org.duracloud.account.monitor.storereporter.util.impl.StoreReporterUtilFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Properties;

/**
 * This class is the driver that collects the input configuration elements
 * necessary to then execute the StoreReporterMonitor.
 *
 * @author Andrew Woods
 *         Date: 5/17/12
 */
public class StoreReporterMonitorDriver extends MonitorsDriver implements Runnable {

    private Logger log =
        LoggerFactory.getLogger(StoreReporterMonitorDriver.class);

    private static final String PREFIX = "storereporter.";
    private static final String THRESHOLD_DAYS = PREFIX + "threshold";

    private StoreReporterMonitor reporterMonitor;

    public StoreReporterMonitorDriver(Properties props) {
        super(props);

        DuracloudRepoMgr repoMgr = getRepoMgr();

        DuracloudAccountRepo acctRepo = repoMgr.getAccountRepo();
        DuracloudInstanceRepo instanceRepo = repoMgr.getInstanceRepo();
        DuracloudServerImageRepo imageRepo = repoMgr.getServerImageRepo();

        int thresholdDays = getThresholdDays(props);
        StoreReporterUtilFactory storeReporterUtilFactory =
            new StoreReporterUtilFactoryImpl(thresholdDays);

        reporterMonitor = new StoreReporterMonitor(acctRepo,
                                                   instanceRepo,
                                                   imageRepo,
                                                   storeReporterUtilFactory);
    }

    private int getThresholdDays(Properties props) {
        return Integer.parseInt(getProperty(props, THRESHOLD_DAYS));
    }

    @Override
    public void run() {
        log.info("starting monitor");
        StoreReporterReport report;
        try {
            report = reporterMonitor.monitorStoreReporters();
            if (report.hasErrors()) {
                StringBuilder subject = new StringBuilder();
                subject.append("Management Console Storage-Reporter Monitor");
                subject.append(", with Errors!");

                sendEmail(subject.toString(), report.toString());
            }

        } catch (Exception e) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintStream error = new PrintStream(out);

            error.println("Error in StoreReporterMonitor: " + e.getMessage());
            e.printStackTrace(error);

            error.flush();
            IOUtils.closeQuietly(out);

            String msg = new String(out.toByteArray());
            log.error(msg);
            sendEmail("Management Console Storage-Reporter Monitor Error", msg);
        }
    }

}
