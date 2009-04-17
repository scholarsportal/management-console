package org.duraspace.customerwebapp.rest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.common.web.RestHttpHelper;
import org.duraspace.common.web.RestHttpHelper.HttpResponse;
import org.duraspace.customerwebapp.config.CustomerWebAppConfig;
import org.duraspace.storage.StorageProvider;

import junit.framework.TestCase;

/**
 * Runtime test of content REST API. The instancewebapp
 * web application must be deployed and available at the
 * baseUrl location in order for these tests to pass.
 *
 * @author Bill Branan
 */
public class TestContentRest
        extends TestCase {

    private static String configFileName = "test-customerwebapp.properties";

    private static RestHttpHelper restHelper = new RestHttpHelper();
    private static String host = "http://localhost";
    private static String webapp = "customerwebapp";
    private static String baseUrl;
    private static final String CONTENT = "<content />";

    private final String nameTag = StorageProvider.METADATA_CONTENT_NAME;
    private final String mimeTag = StorageProvider.METADATA_CONTENT_MIMETYPE;
    private final String sizeTag = StorageProvider.METADATA_CONTENT_SIZE;
    private final String checksumTag = StorageProvider.METADATA_CONTENT_CHECKSUM;
    private final String modifiedTag = StorageProvider.METADATA_CONTENT_MODIFIED;

    @Override
    @Before
    protected void setUp() throws Exception {
        CustomerWebAppConfig.setConfigFileName(configFileName);
        String port = CustomerWebAppConfig.getPort();
        baseUrl = host + ":" + port + "/" + webapp;

        // Initialize the Instance
        String url = baseUrl + "/initialize";
        String formParams = "host=localhost&port="+port;
        HttpResponse response = restHelper.post(url, formParams, true);
        assertTrue(response.getStatusCode() == 200);

        // Add space1
        response = RestTestHelper.addSpace(baseUrl, "1", "space1");
        assertTrue(response.getStatusCode() == 201);

        // Add content1 to space1
        url = baseUrl + "/content/1/space1/content1";
        response = restHelper.put(url, CONTENT, false);
        assertTrue(response.getStatusCode() == 201);
    }

    @Override
    @After
    protected void tearDown() throws Exception {
        // Delete content1 from space1
        String url = baseUrl + "/content/1/space1/content1";
        HttpResponse response = restHelper.delete(url);

        assertTrue(response.getStatusCode() == 200);
        String responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains("content1"));
        assertTrue(responseText.contains("deleted"));

        // Delete space1
        response =
            RestTestHelper.deleteSpace(baseUrl, "1", "space1");
        assertTrue(response.getStatusCode() == 200);
    }

    @Test
    public void testGetContent() throws Exception {
        String url = baseUrl + "/content/1/space1/content1";
        HttpResponse response = restHelper.get(url);

        assertTrue(response.getStatusCode() == 200);
        String content = response.getResponseBody();
        assertNotNull(content);
        assertTrue(CONTENT.equals(content));
    }

    @Test
    public void testGetContentProperties() throws Exception {
        String url = baseUrl + "/content/1/space1/content1?properties=true";
        HttpResponse response = restHelper.get(url);

        assertTrue(response.getStatusCode() == 200);
        String propsXML = response.getResponseBody();
        assertNotNull(propsXML);
        assertTrue(propsXML.contains("<"+nameTag+">content1</"+nameTag+">"));
        assertTrue(propsXML.contains("<"+mimeTag+">text/xml"));
        assertTrue(propsXML.contains("<"+sizeTag+">"));
        assertTrue(propsXML.contains("<"+checksumTag+">"));
        assertTrue(propsXML.contains("<"+modifiedTag+">"));
    }

    @Test
    public void testUpdateContentProperties() throws Exception {
        String url = baseUrl + "/content/1/space1/content1";
        String formParams = "contentName=Test+Content&contentMimeType=text/plain";
        HttpResponse response = restHelper.post(url, formParams, true);

        assertTrue(response.getStatusCode() == 200);
        String responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains("content1"));
        assertTrue(responseText.contains("updated"));

        // Make sure the changes were saved
        url = baseUrl + "/content/1/space1/content1?properties=true";
        response = restHelper.get(url);

        assertTrue(response.getStatusCode() == 200);
        String propsXML = response.getResponseBody();
        assertNotNull(propsXML);
        assertTrue(propsXML.contains("<"+nameTag+">Test Content</"+nameTag+">"));
        assertTrue(propsXML.contains("<"+mimeTag+">text/plain</"+mimeTag+">"));
    }
}