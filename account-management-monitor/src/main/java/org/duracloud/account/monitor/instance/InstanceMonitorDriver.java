/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.instance;

import org.apache.commons.io.IOUtils;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.amazonsimple.AmazonSimpleDBClientMgr;
import org.duracloud.account.db.amazonsimple.DuracloudAccountRepoImpl;
import org.duracloud.account.db.amazonsimple.DuracloudInstanceRepoImpl;
import org.duracloud.account.monitor.MonitorsDriver;
import org.duracloud.account.monitor.instance.domain.InstanceReport;
import org.duracloud.account.monitor.instance.util.InstanceUtilFactory;
import org.duracloud.account.monitor.instance.util.impl.InstanceUtilFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Properties;

/**
 * This class is the driver that collects the input configuration elements
 * necessary to then execute the InstanceMonitor.
 *
 * @author Andrew Woods
 *         Date: 7/18/11
 */
public class InstanceMonitorDriver extends MonitorsDriver implements Runnable {

    private Logger log = LoggerFactory.getLogger(InstanceMonitorDriver.class);

    private InstanceMonitor instanceMonitor;

    public InstanceMonitorDriver(Properties props) {
        super(props);

        AmazonSimpleDBClientMgr dbClientMgr = buildDBClientMgr(props);

        DuracloudAccountRepo acctRepo =
            new DuracloudAccountRepoImpl(dbClientMgr);
        DuracloudInstanceRepo instanceRepo = new DuracloudInstanceRepoImpl(
            dbClientMgr);

        InstanceUtilFactory instanceUtilFactory = new InstanceUtilFactoryImpl();

        instanceMonitor = new InstanceMonitor(acctRepo,
                                              instanceRepo,
                                              instanceUtilFactory);
    }

    @Override
    public void run() {
        log.info("starting monitor");
        InstanceReport report;
        try {
            report = instanceMonitor.monitorInstances();
            if (report.hasErrors()) {
                StringBuilder subject = new StringBuilder();
                subject.append("Management Console Instance Monitor");
                subject.append(", with Errors!");

                sendEmail(subject.toString(), report.toString());
            }

        } catch (Exception e) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintStream error = new PrintStream(out);

            error.println("Error in InstanceMonitor: " + e.getMessage());
            e.printStackTrace(error);

            error.flush();
            IOUtils.closeQuietly(out);

            String msg = new String(out.toByteArray());
            log.error(msg);
            sendEmail("Management Console Instance Monitor Error", msg);
        }
    }

}