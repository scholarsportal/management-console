package org.duracloud.durastore.config;

import org.duracloud.common.util.ApplicationConfig;

import java.util.Properties;

/**
 * This class provides configuration properties associated with the duracloud
 * durastore.
 *
 * @author awoods
 */
public class DuraStoreConfig
        extends ApplicationConfig {

    private static String DURASTORE_PROPERTIES_NAME =
            "test-durastore.properties";

    private static String configFileName;

    private static String portKey = "port";

    private static Properties getProps() {
        return getPropsFromResource(getConfigFileName());
    }

    public static String getPort() {
        return getProps().getProperty(portKey);
    }

    public static void setConfigFileName(String name) {
        configFileName = name;
    }

    public static String getConfigFileName() {
        if (configFileName == null) {
            configFileName = DURASTORE_PROPERTIES_NAME;
        }
        return configFileName;
    }

}
