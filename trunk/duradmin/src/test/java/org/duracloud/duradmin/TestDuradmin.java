
package org.duracloud.duradmin;

import junit.framework.TestCase;
import org.duracloud.common.model.Credential;
import org.duracloud.common.model.DuraCloudUserType;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.duradmin.config.DuradminConfig;
import org.duracloud.unittestdb.UnitTestDatabaseUtil;
import org.duracloud.unittestdb.domain.ResourceType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Runtime test of Duradmin. The durastore web application must be deployed and
 * available in order for these tests to pass.
 * 
 * @author Bill Branan
 */
public class TestDuradmin
        extends TestCase {

    private static RestHttpHelper restHelper = getAuthorizedRestHelper();

    private static String baseUrl;

    private static String configFileName = "test-duradmin.properties";
    static {
        DuradminConfig.setConfigFileName(configFileName);
    }

    @Override
    @Before
    protected void setUp() throws Exception {
        String host = DuradminConfig.getPropsHost();
        String port = DuradminConfig.getPropsPort();
        baseUrl = "http://" + host + ":" + port + "/duradmin";
    }

    @Override
    @After
    protected void tearDown() throws Exception {
    }

    @Test
    public void testSpaces() throws Exception {
        String url = baseUrl + "/spaces.htm";
        HttpResponse response = restHelper.get(url);
        assertEquals(200, response.getStatusCode());

        String responseText = response.getResponseBody();
        assertNotNull(responseText);
    }

    private static RestHttpHelper getAuthorizedRestHelper() {
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