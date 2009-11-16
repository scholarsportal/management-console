package org.duracloud.duraservice.config;

import org.duracloud.common.util.ApplicationConfig;

import java.util.Properties;

/**
 * This class provides configuration properties associated with the duracloud
 * duraservice.
 *
 * @author Bill Branan
 */
public class DuraServiceConfig
        extends ApplicationConfig {

    private static final String DURASERVICE_PROPERTIES_NAME =
        "duraservice.properties";
    
    private static final String HOST_KEY = "host";
    private static final String PORT_KEY = "port";
    private static final String SERVICES_ADMIN_URL_KEY = "servicesAdminURL";

    private String configFileName = null;

    private Properties getProps() throws Exception {
        return getPropsFromResource(getConfigFileName());
    }

    public String getHost() throws Exception {
        return getProps().getProperty(HOST_KEY);
    }

    public String getPort() throws Exception {
        return getProps().getProperty(PORT_KEY);
    }

    public String getServicesAdminUrl() throws Exception {
        return getProps().getProperty(SERVICES_ADMIN_URL_KEY);
    }

    public void setConfigFileName(String name) {
        configFileName = name;
    }

    public String getConfigFileName() {
        if (configFileName == null) {
            configFileName = DURASERVICE_PROPERTIES_NAME;
        }
        return configFileName;
    }

}
