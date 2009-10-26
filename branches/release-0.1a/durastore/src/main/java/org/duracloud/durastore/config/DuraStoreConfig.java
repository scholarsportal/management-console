package org.duracloud.durastore.config;

import java.util.Properties;

import org.duracloud.common.util.ApplicationConfig;

/**
 * This class provides configuration properties associated with the duracloud
 * durastore.
 *
 * @author awoods
 */
public class DuraStoreConfig
        extends ApplicationConfig {

    private static String DURASTORE_PROPERTIES_NAME =
            "durastore.properties";

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
            configFileName = DURASTORE_PROPERTIES_NAME;
        }
        return configFileName;
    }

}
