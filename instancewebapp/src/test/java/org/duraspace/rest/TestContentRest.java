package org.duraspace.rest;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.common.web.RestHttpHelper;
import org.duraspace.common.web.RestHttpHelper.HttpResponse;
import org.duraspace.storage.StorageProvider;

/**
 * Runtime test of content REST API. The instancewebapp
 * web application must be deployed and available at the
 * baseUrl location in order for these tests to pass.
 *
 * @author Bill Branan
 */
public class TestContentRest
        extends TestCase {

    private static RestHttpHelper restHelper = new RestHttpHelper();
    private static String baseUrl = "http://localhost:8080/instancewebapp";
    private static final String CONTENT = "<content />";

    private String nameTag = StorageProvider.METADATA_CONTENT_NAME;
    private String mimeTag = StorageProvider.METADATA_CONTENT_MIMETYPE;

    @Override
    @Before
    protected void setUp() throws Exception {
        // Initialize the Instance
        String url = baseUrl + "/initialize";
        String formParams = "host=localhost&port=8080";
        HttpResponse response = restHelper.post(url, formParams, true);
        assertTrue(response.getStatusCode() == 200);

        // Add space1
        response = RestTestHelper.addSpace(baseUrl, "owner0", "space1");
        assertTrue(response.getStatusCode() == 201);

        // Add content1 to space1
        url = baseUrl + "/content/owner0/space1/content1";
        response = restHelper.put(url, CONTENT, false);
        assertTrue(response.getStatusCode() == 201);
    }

    @Override
    @After
    protected void tearDown() throws Exception {
        // Delete content1 from space1
        String url = baseUrl + "/content/owner0/space1/content1";
        HttpResponse response = restHelper.delete(url);

        assertTrue(response.getStatusCode() == 200);
        String responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains("content1"));
        assertTrue(responseText.contains("deleted"));

        // Delete space1
        response =
            RestTestHelper.deleteSpace(baseUrl, "owner0", "space1");
        assertTrue(response.getStatusCode() == 200);
    }

    @Test
    public void testGetContent() throws Exception {
        String url = baseUrl + "/content/owner0/space1/content1";
        HttpResponse response = restHelper.get(url);

        assertTrue(response.getStatusCode() == 200);
        String content = response.getResponseBody();
        assertNotNull(content);
        assertTrue(CONTENT.equals(content));
    }

    @Test
    public void testGetContentProperties() throws Exception {
        String url = baseUrl + "/content/owner0/space1/content1?properties=true";
        HttpResponse response = restHelper.get(url);

        assertTrue(response.getStatusCode() == 200);
        String propsXML = response.getResponseBody();
        assertNotNull(propsXML);
        assertTrue(propsXML.contains("<"+nameTag+">content1</"+nameTag+">"));
        assertTrue(propsXML.contains("<"+mimeTag+">text/xml"));
    }

    @Test
    public void testUpdateContentProperties() throws Exception {
        String url = baseUrl + "/content/owner0/space1/content1";
        String formParams = "contentName=Test+Content&contentMimeType=text/plain";
        HttpResponse response = restHelper.post(url, formParams, true);

        assertTrue(response.getStatusCode() == 200);
        String responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains("content1"));
        assertTrue(responseText.contains("updated"));

        // Make sure the changes were saved
        url = baseUrl + "/content/owner0/space1/content1?properties=true";
        response = restHelper.get(url);

        assertTrue(response.getStatusCode() == 200);
        String propsXML = response.getResponseBody();
        assertNotNull(propsXML);
        assertTrue(propsXML.contains("<"+nameTag+">Test Content</"+nameTag+">"));
        assertTrue(propsXML.contains("<"+mimeTag+">text/plain</"+mimeTag+">"));
    }
}