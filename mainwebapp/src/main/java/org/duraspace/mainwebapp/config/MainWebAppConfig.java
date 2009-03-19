
package org.duraspace.mainwebapp.config;

import java.util.Properties;

import org.duraspace.common.model.Credential;
import org.duraspace.common.util.ApplicationConfig;

/**
 * This class provides configuration properties associated with the duraspace
 * mainwebapp.
 *
 * @author awoods
 */
public class MainWebAppConfig
        extends ApplicationConfig {

    private static String MAIN_WEBAPP_PROPERTIES_NAME = "mainwebapp.properties";

    private static String portKey = "port";

    private static String dbHomeKey = "database.system.home";

    private static String dbNameKey = "database.name";

    private static String dbUsernameKey = "database.username";

    private static String dbPasswordKey = "database.password";

    private static String dbLoadTestDataKey = "database.load.test.data";

    private static Properties getProps() throws Exception {
        return getPropsFromResource(MAIN_WEBAPP_PROPERTIES_NAME);
    }

    public static String getPort() throws Exception {
        return getProps().getProperty(portKey);
    }

    public static String getDbHome() throws Exception {
        return getProps().getProperty(dbHomeKey);
    }

    public static String getDbName() throws Exception {
        return getProps().getProperty(dbNameKey);
    }

    public static Credential getDbCredential() throws Exception {
        return new Credential(getProps().getProperty(dbUsernameKey), getProps()
                .getProperty(dbPasswordKey));
    }

    public static String getDbPassword() throws Exception {
        return getProps().getProperty(dbPasswordKey);
    }

    public static boolean getDbLoadTestData() throws Exception {
        return ("true".equalsIgnoreCase(getProps()
                .getProperty(dbLoadTestDataKey)));
    }
}
