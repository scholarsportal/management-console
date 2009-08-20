package org.duracloud.duradmin.config;

import java.util.Properties;

import org.duracloud.common.util.ApplicationConfig;

/**
 * This class provides configuration properties associated with the duracloud
 * duradmin.
 *
 * @author awoods
 */
public class DuradminConfig
        extends ApplicationConfig {

    private static String DURADMIN_PROPERTIES_NAME =
            "duradmin.properties";

    private static String configFileName;

    private static String hostKey = "host";
    private static String portKey = "port";
    private static String contextKey = "context";

    private static Properties getProps() throws Exception {
        return getPropsFromResource(getConfigFileName());
    }

    public static String getHost() throws Exception {
        return getProps().getProperty(hostKey);
    }

    public static String getPort() throws Exception {
        return getProps().getProperty(portKey);
    }

    public static String getContext() throws Exception {
        return getProps().getProperty(contextKey);
    }

    public static void setConfigFileName(String name) {
        configFileName = name;
    }

    public static String getConfigFileName() {
        if (configFileName == null) {
            configFileName = DURADMIN_PROPERTIES_NAME;
        }
        return configFileName;
    }

}
