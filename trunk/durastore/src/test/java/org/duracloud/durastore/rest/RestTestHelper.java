package org.duracloud.durastore.rest;

import org.duracloud.common.model.Credential;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.durastore.config.DuraStoreConfig;
import org.duracloud.common.model.DuraCloudUserType;
import org.duracloud.unittestdb.UnitTestDatabaseUtil;
import org.duracloud.unittestdb.domain.ResourceType;
import org.duracloud.unittestdb.util.StorageAccountTestUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bill Branan
 */
public class RestTestHelper {

    private static String configFileName = "test-durastore.properties";
    static {
        DuraStoreConfig.setConfigFileName(configFileName);
    }

    private static RestHttpHelper restHelper = getAuthorizedRestHelper();

    private static String baseUrl;

    private static String host = "http://localhost";

    private static String port;
    private static final String defaultPort = "8080";

    private static String webapp = "durastore";

    private static String accountXml = null;

    public static final String METADATA_NAME =
        BaseRest.HEADER_PREFIX + "test-metadata";

    public static final String METADATA_VALUE = "Test Metadata";

    public static final String SPACE_ACCESS = "OPEN";

    public static HttpResponse initialize() throws Exception {
        String url = getBaseUrl() + "/stores";
        if(accountXml == null) {
            accountXml = StorageAccountTestUtil.buildTestAccountXml();
        }
        return restHelper.post(url, accountXml, null);
    }

    public static HttpResponse addSpace(String spaceID)
            throws Exception {
        String url = getBaseUrl() + "/" + spaceID;
        return addSpaceWithHeaders(url);
    }

    public static HttpResponse addSpace(String spaceID, String storeID)
            throws Exception {
        String url = getBaseUrl() + "/" + spaceID + "?storeID=" + storeID;
        return addSpaceWithHeaders(url);
    }

    private static HttpResponse addSpaceWithHeaders(String url)
            throws Exception {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(BaseRest.SPACE_ACCESS_HEADER, SPACE_ACCESS);
        headers.put(METADATA_NAME, METADATA_VALUE);
        return restHelper.put(url, null, headers);
    }

    public static HttpResponse deleteSpace(String spaceID)
            throws Exception {
        String url = getBaseUrl() + "/" + spaceID;
        return restHelper.delete(url);
    }

    public static HttpResponse deleteSpace(String spaceID, String storeID)
            throws Exception {
        String url = getBaseUrl() + "/" + spaceID + "?storeID=" + storeID;
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
            port = DuraStoreConfig.getPort();
        }

        try { // Ensure the port is a valid port value
            Integer.parseInt(port);
        } catch (NumberFormatException e) {
            port = defaultPort;
        }

        return port;
    }

    public static RestHttpHelper getAuthorizedRestHelper() {
        return new RestHttpHelper(getRootCredential());
    }

    private static Credential getRootCredential() {
        UnitTestDatabaseUtil dbUtil = null;
        try {
            dbUtil = new UnitTestDatabaseUtil();
        } catch (Exception e) {
            System.err.println("ERROR from unitTestDB: " + e.getMessage());
        }

        Credential rootCredential = null;
        try {
            ResourceType rootUser = ResourceType.fromDuraCloudUserType(
                DuraCloudUserType.ROOT);
            rootCredential = dbUtil.findCredentialForResource(rootUser);
        } catch (Exception e) {
            System.err.print("ERROR getting credential: " + e.getMessage());

        }
        return rootCredential;
    }

}
