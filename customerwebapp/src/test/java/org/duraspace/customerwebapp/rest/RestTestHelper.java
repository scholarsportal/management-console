
package org.duraspace.customerwebapp.rest;

import java.util.HashMap;
import java.util.Map;

import org.duraspace.common.model.Credential;
import org.duraspace.common.util.EncryptionUtil;
import org.duraspace.common.web.RestHttpHelper;
import org.duraspace.common.web.RestHttpHelper.HttpResponse;
import org.duraspace.customerwebapp.config.CustomerWebAppConfig;
import org.duraspace.storage.domain.StorageProviderType;
import org.duraspace.storage.domain.test.db.UnitTestDatabaseUtil;

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
    private static final String defaultPort = "8080";

    private static String webapp = "customerwebapp";

    private static String accountXml = null;

    public static final String METADATA_NAME =
        BaseRest.HEADER_PREFIX + "test-metadata";

    public static final String METADATA_VALUE = "Test Metadata";

    public static final String SPACE_NAME = "Testing Space";

    public static final String SPACE_ACCESS = "OPEN";

    public static HttpResponse initialize() throws Exception {
        String url = getBaseUrl() + "/stores";
        if(accountXml == null) {
            accountXml = buildTestAccountXml();
        }
        return restHelper.post(url, accountXml, true, null);
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
        headers.put(BaseRest.SPACE_NAME_HEADER, SPACE_NAME);
        headers.put(BaseRest.SPACE_ACCESS_HEADER, SPACE_ACCESS);
        headers.put(METADATA_NAME, METADATA_VALUE);
        return restHelper.put(url, null, true, headers);
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
            port = CustomerWebAppConfig.getPort();
        }

        try { // Ensure the port is a valid port value
            Integer.parseInt(port);
        } catch (NumberFormatException e) {
            port = defaultPort;
        }

        return port;
    }

    private static String buildTestAccountXml() throws Exception {
        StringBuilder xml = new StringBuilder();
        xml.append("<storageProviderAccounts>");

        UnitTestDatabaseUtil dbUtil = new UnitTestDatabaseUtil();
        int acctId = 0;
        for(StorageProviderType type : StorageProviderType.values()) {
            Credential cred = null;
            try {
                 cred = dbUtil.findCredentialForProvider(type);
            } catch (Exception e) {
                // No credentials available for provider type - skip
                continue;
            }
            if(cred != null) {
                ++acctId;
                EncryptionUtil encryptUtil = new EncryptionUtil();
                String encUsername = encryptUtil.encrypt(cred.getUsername());
                String encPassword = encryptUtil.encrypt(cred.getPassword());

                xml.append("<storageAcct ownerId='0'");
                if(type.equals(StorageProviderType.AMAZON_S3)) {
                    xml.append(" isPrimary='1'");
                }
                xml.append(">");
                xml.append("<id>"+acctId+"</id>");
                xml.append("<storageProviderType>");
                xml.append(type.name());
                xml.append("</storageProviderType>");
                xml.append("<storageProviderCredential>");
                xml.append("<username>"+encUsername+"</username>");
                xml.append("<password>"+encPassword+"</password>");
                xml.append("</storageProviderCredential>");
                xml.append("</storageAcct>");
            }
        }

        xml.append("</storageProviderAccounts>");
        return xml.toString();
    }

}
