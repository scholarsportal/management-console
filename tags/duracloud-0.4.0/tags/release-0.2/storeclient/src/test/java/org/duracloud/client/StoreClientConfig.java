package org.duracloud.client;

import org.duracloud.common.util.ApplicationConfig;

import java.util.Properties;

/**
 * @author: Bill Branan
 * Date: Nov 23, 2009
 */
public class StoreClientConfig extends ApplicationConfig {
    private String propName = "test-storeclient.properties";

    private Properties getProps() throws Exception {
        return getPropsFromResource(propName);
    }

    public String getPort() throws Exception {
        return getProps().getProperty("port");
    }
}
