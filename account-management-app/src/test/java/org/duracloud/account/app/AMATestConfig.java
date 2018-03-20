/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app;

import java.io.File;
import java.util.Properties;

import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.common.util.ApplicationConfig;

/**
 * @author Andrew Woods
 * Date: Oct 7, 2010
 */
public class AMATestConfig extends ApplicationConfig {

    private static String AMA_PROPERTIES_NAME = "test-ama.properties";
    private static String configFileName;
    private static String portKey = "port";
    private static String credentialsFilePath = "credentials.file.path";

    private static Properties getProps() throws Exception {
        return getPropsFromResource(getConfigFileName());
    }

    public static String getPort() throws Exception {
        return getProps().getProperty(portKey);
    }

    public static void setConfigFileName(String name) {
        configFileName = name;
    }

    public static String getConfigFileName() {
        if (configFileName == null) {
            configFileName = AMA_PROPERTIES_NAME;
        }
        return configFileName;
    }

    public static File getCredentialsFile() throws Exception {
        return new File(getProps().getProperty(credentialsFilePath));
    }

    public static String getTestEmail() {
        String email = System.getProperty("test.email");

        if (email == null) {
            try {
                email = getProps().getProperty("test.email");
            } catch (Exception e) {
                throw new DuraCloudRuntimeException(e);
            }
        }

        if (email == null) {
            throw new DuraCloudRuntimeException("Missing test.email parameter. " +
                                                "Make sure it is set either in the " + getConfigFileName() +
                                                " or on the command line (-Dtest.email)");
        }
        return email;
    }
}
