package org.duracloud.durastore.rest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.util.Iterator;
import java.util.Random;

import org.apache.log4j.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.storage.domain.StorageAccount;
import org.duracloud.storage.domain.StorageAccountManager;
import org.duracloud.storage.domain.StorageProviderType;

import junit.framework.TestCase;

/**
 * Runtime test of store REST API. The durastore web application must be
 * deployed and available at the baseUrl location in order for these tests to
 * pass.
 *
 * @author Bill Branan
 */
public class TestStoreRest
        extends TestCase {

    protected static final Logger log =
        Logger.getLogger(TestStoreRest.class);

    private static RestHttpHelper restHelper = new RestHttpHelper();

    private static String baseUrl;

    private static final String CONTENT = "<content />";

    private String storesXML;

    private static String spaceId;

    static {
        String random = String.valueOf(new Random().nextInt(99999));
        spaceId = "space" + random;
    }

    @Override
    @Before
    protected void setUp() throws Exception {
        baseUrl = RestTestHelper.getBaseUrl();

        // Initialize the stores listing
        HttpResponse response = RestTestHelper.initialize();
        int statusCode = response.getStatusCode();
        assertTrue("status: " + statusCode, statusCode == 200);

        // Retrieve the stores listing
        String url = baseUrl + "/stores";
        response = restHelper.get(url);
        assertTrue(response.getStatusCode() == 200);
        storesXML = response.getResponseBody();
        assertNotNull(storesXML);
        assertTrue(storesXML.contains("<storageProviderAccounts>"));
    }

    @Override
    @After
    protected void tearDown() throws Exception {
    }

    @Test
    public void testGetStores() throws Exception {
        StorageAccountManager manager = createStorageAccountManager();
        StorageAccount primaryAcct = manager.getPrimaryStorageAccount();
        assertNotNull(primaryAcct);
        assertEquals(StorageProviderType.AMAZON_S3, primaryAcct.getType());
    }

    @Test
    public void testStores() throws Exception {
        StorageAccountManager manager = createStorageAccountManager();
        Iterator<String> acctIds = manager.getStorageAccountIds();
        while(acctIds.hasNext()) {
            String acctId = acctIds.next();
            StorageProviderType type =
                manager.getStorageAccount(acctId).getType();
            if(!type.equals(StorageProviderType.EMC_SECONDARY) &&
               // TODO: Enable this when EMC provider is working
               !type.equals(StorageProviderType.EMC) &&
               // TODO: Enable this when Sun provider is working
               !type.equals(StorageProviderType.SUN)) {
                log.info("Testing storage account with id " +
                         acctId + " and type " + type.name());
                testStore(acctId);
            }
        }
    }

    private StorageAccountManager createStorageAccountManager()
            throws Exception {
        InputStream is = new ByteArrayInputStream(storesXML.getBytes());
        StorageAccountManager manager = new StorageAccountManager(is, true);
        assertNotNull(manager);
        assertNotNull(manager.getStorageAccountIds());
        return manager;
    }

    /**
     * Used to run the same set of tests over all configured storage providers
     */
    private void testStore(String acctId) throws Exception {
        // Add space1
        HttpResponse response = RestTestHelper.addSpace(spaceId, acctId);
        int statusCode = response.getStatusCode();
        assertTrue("status: " + statusCode, statusCode == 201);

        // Add content1 to space1
        String url = baseUrl + "/" + spaceId + "/content1?storeID=" + acctId;
        response = restHelper.put(url, CONTENT, null);
        statusCode = response.getStatusCode();
        assertTrue("status: " + statusCode, statusCode == 201);

        // Delete content1 from space1
        url = baseUrl + "/" + spaceId + "/content1?storeID=" + acctId;
        response = restHelper.delete(url);
        assertTrue(response.getStatusCode() == 200);
        String responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains("content1"));
        assertTrue(responseText.contains("deleted"));

        // Delete space1
        response = RestTestHelper.deleteSpace(spaceId, acctId);
        assertTrue(response.getStatusCode() == 200);
    }
}