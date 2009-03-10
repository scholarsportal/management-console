
package org.duraspace.common.util;

import java.util.Properties;

import org.apache.commons.io.input.AutoCloseInputStream;

import org.apache.log4j.Logger;

/**
 * @author Andrew Woods
 */
public class ApplicationConfig {

    protected static final Logger log =
            Logger.getLogger(ApplicationConfig.class);

    private static String MAIN_WEBAPP_PROPERTIES_NAME = "mainwebapp.properties";

    /**
     * This method returns a configuration properties associated with the
     * duraspace mainwebapp.
     *
     * @return properties of mainwebapp
     * @throws Exception
     */
    public static Properties getMainWebAppProps() throws Exception {
        return getProps(MAIN_WEBAPP_PROPERTIES_NAME);
    }

    protected static Properties getProps(String resourceName) throws Exception {
        Properties props = new Properties();
        AutoCloseInputStream in =
                new AutoCloseInputStream(ApplicationConfig.class
                        .getClassLoader().getResourceAsStream(resourceName));
        try {
            props.load(in);
        } catch (Exception e) {
            log.warn("Unable to find resource: '" + resourceName + "'");
        }
        return props;
    }

}
