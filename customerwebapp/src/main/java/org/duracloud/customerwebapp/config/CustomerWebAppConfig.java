package org.duracloud.customerwebapp.config;

import java.util.Properties;

import org.duracloud.common.util.ApplicationConfig;

/**
 * This class provides configuration properties associated with the duracloud
 * customerwebapp.
 *
 * @author awoods
 */
public class CustomerWebAppConfig
        extends ApplicationConfig {

    private static String CUSTOMER_WEBAPP_PROPERTIES_NAME =
            "customerwebapp.properties";

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
            configFileName = CUSTOMER_WEBAPP_PROPERTIES_NAME;
        }
        return configFileName;
    }

}
