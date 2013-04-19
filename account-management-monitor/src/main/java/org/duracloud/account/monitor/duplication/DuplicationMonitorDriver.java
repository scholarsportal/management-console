/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.duplication;

import org.apache.commons.io.IOUtils;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.DuracloudServerImageRepo;
import org.duracloud.account.db.amazonsimple.AmazonSimpleDBClientMgr;
import org.duracloud.account.db.amazonsimple.DuracloudAccountRepoImpl;
import org.duracloud.account.db.amazonsimple.DuracloudInstanceRepoImpl;
import org.duracloud.account.db.amazonsimple.DuracloudServerImageRepoImpl;
import org.duracloud.account.monitor.MonitorsDriver;
import org.duracloud.account.monitor.duplication.domain.DuplicationReport;
import org.duracloud.account.monitor.duplication.util.DuplicationPropReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Properties;

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

        AmazonSimpleDBClientMgr dbClientMgr = buildDBClientMgr(props);

        DuracloudAccountRepo acctRepo =
            new DuracloudAccountRepoImpl(dbClientMgr);
        DuracloudInstanceRepo instanceRepo =
            new DuracloudInstanceRepoImpl(dbClientMgr);
        DuracloudServerImageRepo imageRepo =
            new DuracloudServerImageRepoImpl(dbClientMgr);

        DuplicationPropReader propReader = new DuplicationPropReader();
        Map<String, String> dupHosts = propReader.readDupProps(props);

        duplicationMonitor = new DuplicationMonitor(acctRepo,
                                                    instanceRepo,
                                                    imageRepo,
                                                    dupHosts);
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
