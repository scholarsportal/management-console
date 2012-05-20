/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.storereporter;

import org.apache.commons.io.IOUtils;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.DuracloudServerImageRepo;
import org.duracloud.account.db.amazonsimple.AmazonSimpleDBClientMgr;
import org.duracloud.account.db.amazonsimple.DuracloudAccountRepoImpl;
import org.duracloud.account.db.amazonsimple.DuracloudInstanceRepoImpl;
import org.duracloud.account.db.amazonsimple.DuracloudServerImageRepoImpl;
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

        AmazonSimpleDBClientMgr dbClientMgr = buildDBClientMgr(props);

        DuracloudAccountRepo acctRepo =
            new DuracloudAccountRepoImpl(dbClientMgr);
        DuracloudInstanceRepo instanceRepo = new DuracloudInstanceRepoImpl(
            dbClientMgr);
        DuracloudServerImageRepo imageRepo = new DuracloudServerImageRepoImpl(
            dbClientMgr);

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
