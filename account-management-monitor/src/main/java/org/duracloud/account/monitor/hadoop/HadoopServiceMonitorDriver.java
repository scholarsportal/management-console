/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.hadoop;

import org.apache.commons.io.IOUtils;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudStorageProviderAccountRepo;
import org.duracloud.account.db.amazonsimple.AmazonSimpleDBClientMgr;
import org.duracloud.account.db.amazonsimple.DuracloudAccountRepoImpl;
import org.duracloud.account.db.amazonsimple.DuracloudStorageProviderAccountRepoImpl;
import org.duracloud.account.db.backup.util.EmailUtil;
import org.duracloud.account.db.backup.util.impl.EmailUtilImpl;
import org.duracloud.account.monitor.hadoop.domain.HadoopServiceReport;
import org.duracloud.account.monitor.hadoop.util.HadoopUtilFactory;
import org.duracloud.account.monitor.hadoop.util.impl.HadoopUtilFactoryImpl;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * This class is the command-line driver for running Hadoop service monitoring.
 *
 * @author Andrew Woods
 *         Date: 7/13/11
 */
public class HadoopServiceMonitorDriver {

    private Logger log =
        LoggerFactory.getLogger(HadoopServiceMonitorDriver.class);

    private static final String PREFIX = "monitor.";
    private static final String PREFIX_AWS = PREFIX + "aws.";

    private static final String AWS_USERNAME = PREFIX_AWS + "username";
    private static final String AWS_PASSWORD = PREFIX_AWS + "password";

    private static final String PREFIX_EMAIL = PREFIX + "email.";
    private static final String FROM_ADDRESS = PREFIX_EMAIL + "from";
    private static final String TO_ADDRESS = PREFIX_EMAIL + "to.";

    private static final String THRESHOLD_DAYS = PREFIX + "threshold";

    private int thresholdDays;
    private EmailUtil emailUtil;
    private HadoopServiceMonitor serviceMonitor;

    public HadoopServiceMonitorDriver(Properties props) {
        this.thresholdDays = getThresholdDays(props);
        this.emailUtil = buildEmailUtil(props);

        AmazonSimpleDBClientMgr dbClientMgr = buildDBClientMgr(props);

        DuracloudAccountRepo acctRepo =
            new DuracloudAccountRepoImpl(dbClientMgr);

        DuracloudStorageProviderAccountRepo storageProviderAcctRepo =
            new DuracloudStorageProviderAccountRepoImpl(dbClientMgr);

        HadoopUtilFactory hadoopUtilFactory = new HadoopUtilFactoryImpl();

        this.serviceMonitor = new HadoopServiceMonitor(acctRepo,
                                                       storageProviderAcctRepo,
                                                       hadoopUtilFactory);
    }

    public void monitor() {
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

    private void sendEmail(String subject, String body) {
        log.info("Sending email. subject: {}, body {}", subject, body);
        emailUtil.sendEmail(subject, body);
    }

    private EmailUtil buildEmailUtil(Properties props) {
        String username = getProperty(props, AWS_USERNAME);
        String password = getProperty(props, AWS_PASSWORD);
        String fromAddress = getProperty(props, FROM_ADDRESS);
        List<String> recipients = getEmailRecipients(props, TO_ADDRESS);

        return new EmailUtilImpl(username, password, fromAddress, recipients);
    }

    private List<String> getEmailRecipients(Properties props, String prefix) {
        List<String> recipients = new ArrayList<String>();
        String recipient = getProperty(props, prefix + 0);
        while (null != recipient) {
            recipients.add(recipient);
            try {
                recipient = getProperty(props, prefix + recipients.size());

            } catch (Exception e) {
                recipient = null;
            }
        }
        return recipients;
    }

    private AmazonSimpleDBClientMgr buildDBClientMgr(Properties props) {
        String awsUsername = getProperty(props, AWS_USERNAME);
        String awsPassword = getProperty(props, AWS_PASSWORD);

        return new AmazonSimpleDBClientMgr(awsUsername, awsPassword);
    }

    private int getThresholdDays(Properties props) {
        return Integer.parseInt(getProperty(props, THRESHOLD_DAYS));
    }

    private String getProperty(Properties props, String key) {
        String property = props.getProperty(key);
        if (null == property) {
            throw new DuraCloudRuntimeException("Property not found: " + key);
        }
        return property;
    }

    private static Properties parseProperties(InputStream stream)
        throws IOException {
        Properties props = new Properties();
        props.load(stream);
        return props;
    }

    /**
     * Main
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println(usage("Invalid number of args: " + args.length));
            System.exit(1);
        }

        File configFile = new File(args[0]);
        if (!configFile.exists()) {
            String msg = "File does not exist: " + configFile.getAbsolutePath();
            System.err.println(usage(msg));
            System.exit(1);
        }

        InputStream inputStream = null;
        Properties props = null;
        try {
            inputStream = new FileInputStream(configFile);
            props = parseProperties(inputStream);

        } catch (IOException e) {
            System.err.println("Error loading properties: " + e.getMessage());
            System.exit(1);

        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        HadoopServiceMonitorDriver driver =
            new HadoopServiceMonitorDriver(props);
        driver.monitor();
    }

    private static String usage(String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------------------\n");
        sb.append("Error: " + msg);
        sb.append("\n\n");
        sb.append("Usage: HadoopServiceMonitorDriver <properties-file>");
        sb.append("\n\t");
        sb.append("Where 'properties-file' contains the necessary ");
        sb.append("initialization config.");
        sb.append("\n\t");
        sb.append("See the example in this jar's 'resources' directory.");
        sb.append("\n");
        sb.append("-----------------------------------------\n");

        return sb.toString();
    }
}
