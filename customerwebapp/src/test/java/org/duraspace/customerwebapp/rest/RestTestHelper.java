
package org.duraspace.customerwebapp.rest;

import org.duraspace.common.web.RestHttpHelper;
import org.duraspace.common.web.RestHttpHelper.HttpResponse;
import org.duraspace.customerwebapp.config.CustomerWebAppConfig;

/**
 * @author Bill Branan
 */
public class RestTestHelper {

    private static String configFileName = "test-customerwebapp.properties";
    static {
        CustomerWebAppConfig.setConfigFileName(configFileName);
    }

    private static RestHttpHelper restHelper = new RestHttpHelper();

    private static String baseUrl;

    private static String host = "http://localhost";

    private static String port;

    private static String webapp = "customerwebapp";

    public static HttpResponse initialize() throws Exception {
        String url = getBaseUrl() + "/initialize";
        String formParams = "host=localhost&port=" + getPort();
        return restHelper.post(url, formParams, true);
    }

    public static HttpResponse addSpace(String accountID, String spaceID)
            throws Exception {
        String url = getBaseUrl() + "/space/" + accountID + "/" + spaceID;
        String formParams = "spaceName=Testing+Space&spaceAccess=OPEN";
        return restHelper.put(url, formParams, true);
    }

    public static HttpResponse deleteSpace(String accountID, String spaceID)
            throws Exception {
        String url = getBaseUrl() + "/space/" + accountID + "/" + spaceID;
        return restHelper.delete(url);
    }

    public static String getBaseUrl() throws Exception {
        if (baseUrl == null) {
            baseUrl = host + ":" + getPort() + "/" + webapp;
        }
        return baseUrl;
    }

    private static String getPort() throws Exception {
        if (port == null) {
            port = CustomerWebAppConfig.getPort();
        }
        return port;
    }

}
