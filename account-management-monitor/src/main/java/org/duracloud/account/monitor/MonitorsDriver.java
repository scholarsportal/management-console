/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor;

import org.apache.commons.io.IOUtils;
import org.duracloud.account.db.amazonsimple.AmazonSimpleDBClientMgr;
import org.duracloud.account.db.backup.util.EmailUtil;
import org.duracloud.account.db.backup.util.impl.EmailUtilImpl;
import org.duracloud.account.monitor.hadoop.HadoopServiceMonitorDriver;
import org.duracloud.account.monitor.instance.InstanceMonitorDriver;
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

import static org.duracloud.account.monitor.MonitorsDriver.Monitor.HADOOP;
import static org.duracloud.account.monitor.MonitorsDriver.Monitor.INSTANCE;

/**
 * This class is the command-line driver for executing monitors for both
 * hadoop jobs, and instance health across all accounts managed by the
 * Management Console defined by the configuration credentials.
 *
 * @author Andrew Woods
 *         Date: 7/18/11
 */
public class MonitorsDriver {

    private Logger log = LoggerFactory.getLogger(MonitorsDriver.class);

    private static final String PREFIX = "monitor.";

    private static final String PREFIX_AWS = PREFIX + "aws.";
    private static final String AWS_USERNAME = PREFIX_AWS + "username";

    private static final String AWS_PASSWORD = PREFIX_AWS + "password";
    private static final String PREFIX_EMAIL = PREFIX + "email.";

    private static final String FROM_ADDRESS = PREFIX_EMAIL + "from";
    private static final String TO_ADDRESS = PREFIX_EMAIL + "to.";

    private Properties props;
    private EmailUtil emailUtil;

    /**
     * This enum defines the types of monitors available through this driver.
     */
    public enum Monitor {
        HADOOP, INSTANCE;

        public Runnable getMonitorDriver(Properties props) {
            if (this.equals(HADOOP)) {
                return new HadoopServiceMonitorDriver(props);

            } else if (this.equals(INSTANCE)) {
                return new InstanceMonitorDriver(props);

            } else {
                throw new DuraCloudRuntimeException("Unknown type: " + this);
            }
        }

        public String toString() {
            return name().toLowerCase();
        }
    }


    public MonitorsDriver(Properties props) {
        this.props = props;
        this.emailUtil = buildEmailUtil(props);
    }

    /**
     * This method invokes the .run() method on the provided arg target.
     *
     * @param target to monitor
     */
    public void monitor(Monitor target) {
        log.info("starting monitor: {}", target);

        try {
            target.getMonitorDriver(props).run();

        } catch (Exception e) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintStream error = new PrintStream(out);

            error.println("Error in MonitorsDriver: " + e.getMessage());
            e.printStackTrace(error);

            error.flush();
            IOUtils.closeQuietly(out);

            String msg = new String(out.toByteArray());
            log.error(msg);
            sendEmail("Management Console Monitors Error", msg);
        }
    }

    protected void sendEmail(String subject, String body) {
        log.info("Sending email.\nsubject: {} \nbody \n{}", subject, body);
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

    protected AmazonSimpleDBClientMgr buildDBClientMgr(Properties props) {
        String awsUsername = getProperty(props, AWS_USERNAME);
        String awsPassword = getProperty(props, AWS_PASSWORD);

        return new AmazonSimpleDBClientMgr(awsUsername, awsPassword);
    }

    protected String getProperty(Properties props, String key) {
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
        if (args.length != 2) {
            System.err.println(usage("Invalid number of args: " + args.length));
            System.exit(1);
        }

        String targetName = args[0].toLowerCase();
        Monitor target = null;
        try {
            target = Monitor.valueOf(targetName.toUpperCase());

        } catch (Exception e) {
            String msg = "Target must be '" + HADOOP + "' | '" + INSTANCE + "'";
            System.err.println(usage(msg));
            System.exit(1);
        }

        File configFile = new File(args[1]);
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

        MonitorsDriver driver = new MonitorsDriver(props);
        driver.monitor(target);
    }


    private static String usage(String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------------------\n");
        sb.append("Error: " + msg);
        sb.append("\n\n");
        sb.append("Usage: MonitorsDriver <hadoop|instance> <properties-file>");
        sb.append("\n\t");
        sb.append("Where either '");
        sb.append(HADOOP);
        sb.append("' or '");
        sb.append(INSTANCE);
        sb.append("' must be provided to indicate the monitoring target.");
        sb.append("\n\t");
        sb.append("And where 'properties-file' contains the necessary ");
        sb.append("initialization config.");
        sb.append("\n\t");
        sb.append("See the example in this jar's 'resources' directory.");
        sb.append("\n");
        sb.append("-----------------------------------------\n");

        return sb.toString();
    }

}
