/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app;

import java.util.Properties;

import org.duracloud.common.util.ApplicationConfig;

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
