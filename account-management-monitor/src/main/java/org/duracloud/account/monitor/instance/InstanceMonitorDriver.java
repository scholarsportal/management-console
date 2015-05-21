/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.instance;

import org.apache.commons.io.IOUtils;
import org.duracloud.account.db.repo.DuracloudAccountRepo;
import org.duracloud.account.db.repo.DuracloudInstanceRepo;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
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

        DuracloudRepoMgr repoMgr = getRepoMgr();

        DuracloudAccountRepo acctRepo = repoMgr.getAccountRepo();
        DuracloudInstanceRepo instanceRepo = repoMgr.getInstanceRepo();

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
