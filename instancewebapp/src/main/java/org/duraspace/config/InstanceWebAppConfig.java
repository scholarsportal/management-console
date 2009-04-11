
package org.duraspace.config;

import java.util.Properties;

import org.duraspace.common.util.ApplicationConfig;

/**
 * This class provides configuration properties associated with the duraspace
 * instancewebapp.
 *
 * @author awoods
 */
public class InstanceWebAppConfig
        extends ApplicationConfig {

    private static String INSTANCE_WEBAPP_PROPERTIES_NAME =
            "instancewebapp.properties";

    private static String portKey = "port";

    private static Properties getProps() throws Exception {
        return getPropsFromResource(INSTANCE_WEBAPP_PROPERTIES_NAME);
    }

    public static String getPort() throws Exception {
        return getProps().getProperty(portKey);
    }

}
