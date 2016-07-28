/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.duplication;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.duracloud.account.monitor.MonitorsDriver;
import org.duracloud.account.monitor.duplication.domain.DuplicationReport;
import org.duracloud.account.monitor.duplication.util.DuplicationPropReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is the driver that collects the input configuration elements
 * necessary to then execute the DuplicationMonitor.
 *
 * @author Bill Branan
 *         Date: 4/17/13
 */
public class DuplicationMonitorDriver extends MonitorsDriver implements Runnable {

    private Logger log =
        LoggerFactory.getLogger(DuplicationMonitorDriver.class);

    private DuplicationMonitor duplicationMonitor;

    public DuplicationMonitorDriver(Properties props) {
        super(props);

        DuplicationPropReader propReader = new DuplicationPropReader();
        Map<String, String> dupHosts = propReader.readDupProps(props);

       
        duplicationMonitor = new DuplicationMonitor(dupHosts);
    }

    @Override
    public void run() {
        log.info("starting monitor");
        DuplicationReport report;
        try {
            report = duplicationMonitor.monitorDuplication();
            if (report.hasIssues()) {
                sendEmail("DuraCloud Duplication Monitor discovered issues!",
                          report.toString());
            }
        } catch (Exception e) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintStream error = new PrintStream(out);

            error.println("Error in DuplicationMonitor: " + e.getMessage());
            e.printStackTrace(error);

            error.flush();
            IOUtils.closeQuietly(out);

            String msg = new String(out.toByteArray());
            log.error(msg);
            sendEmail("Duplication Monitor Error", msg);
        }
    }

}
