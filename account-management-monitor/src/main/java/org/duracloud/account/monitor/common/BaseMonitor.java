/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.common.model.Credential;
import org.duracloud.common.util.EncryptionUtil;
import org.slf4j.Logger;

/**
 * @author Bill Branan
 * Date: 4/16/13
 */
public abstract class BaseMonitor {

    private static final String MONITOR_PASSWORD_KEY = "monitor.password";
    private static final String MONITOR_USERNAME_KEY = "monitor.username";
    protected Logger log;

    protected Credential getRootCredential() {
        try {
            EncryptionUtil encryptionUtil = new EncryptionUtil();

            File credentialsFile = new File(System.getProperty("user.home"), ".monitor-credentials");

            if (!credentialsFile.exists()) {
                generateEncryptedCredentialsFile(encryptionUtil, credentialsFile);
            }

            return decryptCredentialsFromFile(encryptionUtil, credentialsFile);

        } catch (Exception e) {
            throw new RuntimeException("failed to get credentials:  " + e.getMessage(), e);
        }

    }

    private Credential decryptCredentialsFromFile(EncryptionUtil encryptionUtil,
                                                  File credentialsFile) throws IOException, FileNotFoundException {

        log.info(
            "Reading credentials from {}. Delete this file in order to reset password.",
            credentialsFile.getAbsolutePath());
        Properties props = new Properties();
        try (FileReader reader = new FileReader(credentialsFile)) {
            props.load(reader);
        }
        String username = decryptValue(props, encryptionUtil, credentialsFile, MONITOR_USERNAME_KEY);
        String password = decryptValue(props, encryptionUtil, credentialsFile, MONITOR_PASSWORD_KEY);
        return new Credential(username, password);
    }

    private void generateEncryptedCredentialsFile(EncryptionUtil encryptionUtil,
                                                  File credentialsFile) throws IOException, FileNotFoundException {
        try (Scanner scan = new Scanner(System.in)) {
            System.out.println("Please enter your monitor username: ");
            String username = scan.nextLine();
            if (StringUtils.isBlank(username)) {
                throw new RuntimeException("username must not be blank");
            }
            System.out.println("Please enter your monitor password: ");
            String password = scan.nextLine();
            if (StringUtils.isBlank(password)) {
                throw new RuntimeException("password must not be blank");
            }

            Properties props = new Properties();
            props.put(MONITOR_USERNAME_KEY, encryptionUtil.encrypt(username));
            props.put(MONITOR_PASSWORD_KEY, encryptionUtil.encrypt(password));

            try (FileOutputStream fos = new FileOutputStream(credentialsFile)) {
                props.store(fos, null);
            }
        }
    }

    private String decryptValue(Properties props, EncryptionUtil encryptionUtil,
                                File credentialsFile, String key) {
        String value = props.getProperty(key);
        if (value != null) {
            try {
                return encryptionUtil.decrypt(value);
            } catch (DuraCloudRuntimeException e) {
                String message = MessageFormat.format("Failed to decrypted value stored in key {0} in {1}", key,
                                                      credentialsFile.getAbsolutePath());
                log.error(message);
                throw new RuntimeException(message);

            }
        } else {
            String message = MessageFormat.format("{0} is not present in credentials file {1}",
                                                  key, credentialsFile.getAbsolutePath());
            log.error(message);
            throw new RuntimeException(message);
        }
    }

}
