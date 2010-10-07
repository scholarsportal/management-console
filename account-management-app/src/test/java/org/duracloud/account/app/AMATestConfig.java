/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app;

import org.duracloud.common.util.ApplicationConfig;

import java.util.Properties;

/**
 * @author Andrew Woods
 *         Date: Oct 7, 2010
 */
public class AMATestConfig extends ApplicationConfig {

    private static String AMA_PROPERTIES_NAME = "test-ama.properties";
    private static String configFileName;
    private static String portKey = "port";

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

}
