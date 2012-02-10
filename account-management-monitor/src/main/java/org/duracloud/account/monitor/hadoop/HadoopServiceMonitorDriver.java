/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.hadoop;

import org.apache.commons.io.IOUtils;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudServerDetailsRepo;
import org.duracloud.account.db.DuracloudStorageProviderAccountRepo;
import org.duracloud.account.db.amazonsimple.AmazonSimpleDBClientMgr;
import org.duracloud.account.db.amazonsimple.DuracloudAccountRepoImpl;
import org.duracloud.account.db.amazonsimple.DuracloudServerDetailsRepoImpl;
import org.duracloud.account.db.amazonsimple.DuracloudStorageProviderAccountRepoImpl;
import org.duracloud.account.monitor.MonitorsDriver;
import org.duracloud.account.monitor.hadoop.domain.HadoopServiceReport;
import org.duracloud.account.monitor.hadoop.util.HadoopUtilFactory;
import org.duracloud.account.monitor.hadoop.util.impl.HadoopUtilFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Properties;

/**
 * This class is the command-line driver for running Hadoop service monitoring.
 *
 * @author Andrew Woods
 *         Date: 7/13/11
 */
public class HadoopServiceMonitorDriver extends MonitorsDriver implements Runnable {

    private Logger log =
        LoggerFactory.getLogger(HadoopServiceMonitorDriver.class);

    private static final String PREFIX = "monitor.";
    private static final String THRESHOLD_DAYS = PREFIX + "threshold";

    private int thresholdDays;
    private HadoopServiceMonitor serviceMonitor;

    public HadoopServiceMonitorDriver(Properties props) {
        super(props);

        this.thresholdDays = getThresholdDays(props);

        AmazonSimpleDBClientMgr dbClientMgr = buildDBClientMgr(props);

        DuracloudAccountRepo acctRepo =
            new DuracloudAccountRepoImpl(dbClientMgr);

        DuracloudServerDetailsRepo serverDetailsRepo =
            new DuracloudServerDetailsRepoImpl(dbClientMgr);

        DuracloudStorageProviderAccountRepo storageProviderAcctRepo =
            new DuracloudStorageProviderAccountRepoImpl(dbClientMgr);

        HadoopUtilFactory hadoopUtilFactory = new HadoopUtilFactoryImpl();

        this.serviceMonitor = new HadoopServiceMonitor(acctRepo,
                                                       serverDetailsRepo,
                                                       storageProviderAcctRepo,
                                                       hadoopUtilFactory);
    }

    @Override
    public void run() {
        log.info("starting monitor");
        HadoopServiceReport report;
        try {
            report = serviceMonitor.monitorServices(thresholdDays);
            if (report.hasServices() || report.hasErrors()) {

                StringBuilder subject = new StringBuilder();
                subject.append("Management Console Hadoop Monitor");
                if (report.hasErrors()) {
                    subject.append(", with Errors!");
                }

                sendEmail(subject.toString(), report.toString());
            }

        } catch (Exception e) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintStream error = new PrintStream(out);

            error.println("Error in HadoopServiceMonitor: " + e.getMessage());
            e.printStackTrace(error);

            error.flush();
            IOUtils.closeQuietly(out);

            String msg = new String(out.toByteArray());
            log.error(msg);
            sendEmail("Management Console Hadoop Monitor Error", msg);
        }
    }

    private int getThresholdDays(Properties props) {
        return Integer.parseInt(getProperty(props, THRESHOLD_DAYS));
    }

}
