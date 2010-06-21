package org.duracloud.sync;

import org.duracloud.common.util.ApplicationConfig;

import java.util.Properties;

/**
 * @author: Bill Branan
 * Date: Apr 9, 2010
 */
public class SyncToolTestConfig  extends ApplicationConfig {
    private String propName = "test-synctool.properties";

    private Properties getProps() throws Exception {
        return getPropsFromResource(propName);
    }

    public String getPort() throws Exception {
        return getProps().getProperty("port");
    }
}
