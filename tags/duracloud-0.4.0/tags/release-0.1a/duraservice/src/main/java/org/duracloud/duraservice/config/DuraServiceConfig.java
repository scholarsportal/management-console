package org.duracloud.duraservice.config;

import java.util.Properties;

import org.duracloud.common.util.ApplicationConfig;

/**
 * This class provides configuration properties associated with the duracloud
 * duraservice.
 *
 * @author bbranan
 */
public class DuraServiceConfig
        extends ApplicationConfig {

    private static String DURASERVICE_PROPERTIES_NAME =
            "duraservice.properties";

    private static String configFileName;

    private static String portKey = "port";
    private static String servicesAdminUrlKey = "servicesAdminURL";

    private static Properties getProps() throws Exception {
        return getPropsFromResource(getConfigFileName());
    }

    public static String getPort() throws Exception {
        return getProps().getProperty(portKey);
    }

    public static String getServicesAdminUrl() throws Exception {
        return getProps().getProperty(servicesAdminUrlKey);
    }

    public static void setConfigFileName(String name) {
        configFileName = name;
    }

    public static String getConfigFileName() {
        if (configFileName == null) {
            configFileName = DURASERVICE_PROPERTIES_NAME;
        }
        return configFileName;
    }

}
