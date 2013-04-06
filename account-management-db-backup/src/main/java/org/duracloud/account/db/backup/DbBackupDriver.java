/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.backup;

import org.apache.commons.io.IOUtils;
import org.duracloud.account.db.backup.util.EmailUtil;
import org.duracloud.account.db.backup.util.FileSystemUtil;
import org.duracloud.account.db.backup.util.StoreUtil;
import org.duracloud.account.db.backup.util.impl.EmailUtilImpl;
import org.duracloud.account.db.backup.util.impl.FileSystemUtilImpl;
import org.duracloud.account.db.backup.util.impl.StoreUtilS3Impl;
import org.duracloud.account.db.util.DbUtil;
import org.duracloud.account.init.domain.AmaConfig;
import org.duracloud.common.error.DuraCloudRuntimeException;

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
 * This class is the command-line entry point for running the DbBackup utility.
 * <p/>
 * It takes as input a properties file as defined in:
 * - src/main/resources/backup.properties
 *
 * @author Andrew Woods
 *         Date: 7/1/11
 */
public class DbBackupDriver {

    private static final String PREFIX = "backup.";
    private static final String PREFIX_AWS = PREFIX + "aws.";
    private static final String PREFIX_STORE = PREFIX + "store.";
    private static final String PREFIX_EMAIL = PREFIX + "email.";
    private static final String TMP_DIR = PREFIX + "tmpdir";

    private static final String AWS_USERNAME = PREFIX_AWS + "username";
    private static final String AWS_PASSWORD = PREFIX_AWS + "password";

    private static final String STORE_BUCKET = PREFIX_STORE + "bucket";

    private static final String LDAP_PREFIX = PREFIX + "ldap.";
    private static final String LDAP_URL = LDAP_PREFIX + "url";
    private static final String LDAP_BASEDN = LDAP_PREFIX + "basedn";
    private static final String LDAP_USERDN = LDAP_PREFIX + "userdn";
    private static final String LDAP_PASSWORD = LDAP_PREFIX + "password";

    private static final String FROM_ADDRESS = PREFIX_EMAIL + "from";
    private static final String TO_ADDRESS = PREFIX_EMAIL + "to.";

    private DbBackup dbBackup;
    private EmailUtil emailUtil;

    public DbBackupDriver(Properties props) {
        DbUtil dbUtil = buildDbUtil(props);
        StoreUtil storeUtil = buildStoreUtil(props);
        FileSystemUtil fileSystemUtil = new FileSystemUtilImpl();
        File workDir = getWorkDir(props);
        File previousDir = getPreviousDir(props);

        this.emailUtil = buildEmailUtil(props);

        this.dbBackup = new DbBackup(dbUtil,
                                     storeUtil,
                                     fileSystemUtil,
                                     workDir,
                                     previousDir);
    }

    private DbUtil buildDbUtil(Properties props) {
        String dbUsername = getProperty(props, AWS_USERNAME);
        String dbPassword = getProperty(props, AWS_PASSWORD);
        String ldapUrl = getProperty(props, LDAP_URL);
        String ldapBaseDn = getProperty(props, LDAP_BASEDN);
        String ldapUserDn = getProperty(props, LDAP_USERDN);
        String ldapPassword = getProperty(props, LDAP_PASSWORD);

        AmaConfig amaConfig = new AmaConfig();
        amaConfig.setUsername(dbUsername);
        amaConfig.setPassword(dbPassword);
        amaConfig.setLdapUrl(ldapUrl);
        amaConfig.setLdapBaseDn(ldapBaseDn);
        amaConfig.setLdapUserDn(ldapUserDn);
        amaConfig.setLdapPassword(ldapPassword);

        // Not used below
        amaConfig.setIdUtilHost("example.org");
        amaConfig.setIdUtilPort("80");
        amaConfig.setIdUtilCtxt("not-used");
        amaConfig.setIdUtilUsername("not-used");
        amaConfig.setIdUtilPassword("not-used");

        return new DbUtil(amaConfig, getWorkDir(props));
    }

    private StoreUtil buildStoreUtil(Properties props) {
        String storeUsername = getProperty(props, AWS_USERNAME);
        String storePassword = getProperty(props, AWS_PASSWORD);
        String storeBucket = getProperty(props, STORE_BUCKET);

        String bucketId = "x." + storeUsername + "." + storeBucket;
        bucketId = bucketId.toLowerCase();

        return new StoreUtilS3Impl(storeUsername, storePassword, bucketId);
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

    /**
     * This method does the actual work of running a backup.
     * If a backup is performed, an email is sent to the recipient list.
     * Likewise, if an error occurs an email is sent.
     */
    public void backup() {
        try {
            if (dbBackup.backup()) {
                sendEmail("Management Console DB backup successful", "M.J.");
            }

        } catch (Exception e) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintStream error = new PrintStream(out);

            error.println("Error in DbBackupDriver: " + e.getMessage());
            e.printStackTrace(error);

            error.flush();
            IOUtils.closeQuietly(out);

            String msg = new String(out.toByteArray());
            System.err.println(msg);
            sendEmail("Management Console DB backup Error", msg);
        }
    }

    private void sendEmail(String subject, String body) {
        emailUtil.sendEmail(subject, body);
    }

    private File getWorkDir(Properties props) {
        return new File(getProperty(props, TMP_DIR), "work");
    }

    private File getPreviousDir(Properties props) {
        return new File(getProperty(props, TMP_DIR), "previous");
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

        DbBackupDriver driver = new DbBackupDriver(props);
        driver.backup();
    }

    private static String usage(String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------------------\n");
        sb.append("Error: " + msg);
        sb.append("\n\n");
        sb.append("Usage: DbBackupDriver <properties-file>");
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
