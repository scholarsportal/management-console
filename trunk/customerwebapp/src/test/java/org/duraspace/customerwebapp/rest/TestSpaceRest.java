package org.duraspace.customerwebapp.rest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.common.web.RestHttpHelper;
import org.duraspace.common.web.RestHttpHelper.HttpResponse;
import org.duraspace.storage.provider.StorageProvider;

import junit.framework.TestCase;

/**
 * Runtime test of space REST API. The instancewebapp
 * web application must be deployed and available at the
 * baseUrl location in order for these tests to pass.
 *
 * @author Bill Branan
 */
public class TestSpaceRest
        extends TestCase {

    private static RestHttpHelper restHelper = new RestHttpHelper();
    private static String baseUrl;

    private final String nameTag = StorageProvider.METADATA_SPACE_NAME;
    private final String accessTag = StorageProvider.METADATA_SPACE_ACCESS;

    @Override
    @Before
    protected void setUp() throws Exception {
        baseUrl = RestTestHelper.getBaseUrl();

        // Initialize the Instance
        HttpResponse response = RestTestHelper.initialize();
        assertTrue(response.getStatusCode() == 200);

        // Add space1
        response = RestTestHelper.addSpace("space1");
        assertTrue(response.getStatusCode() == 201);
    }

    @Override
    @After
    protected void tearDown() throws Exception {
        HttpResponse response =
            RestTestHelper.deleteSpace("space1");

        assertTrue(response.getStatusCode() == 200);
        String responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains("space1"));
        assertTrue(responseText.contains("deleted"));
    }

    @Test
    public void testGetSpaces() throws Exception {
        String url = baseUrl + "/spaces";
        HttpResponse response = restHelper.get(url);
        String responseText = response.getResponseBody();

        assertTrue(response.getStatusCode() == 200);
        assertTrue(responseText.contains("<spaces>"));
    }

    @Test
    public void testGetSpaceProperties() throws Exception {
        String url = baseUrl + "/space1?properties=true";
        HttpResponse response = restHelper.get(url);
        String responseText = response.getResponseBody();

        assertTrue(response.getStatusCode() == 200);
        assertTrue(responseText.contains("<"+nameTag+">Testing Space</"+nameTag+">"));
        assertTrue(responseText.contains("<"+accessTag+">OPEN</"+accessTag+">"));
    }

    @Test
    public void testGetSpaceContents() throws Exception {
        String url = baseUrl + "/space1";
        HttpResponse response = restHelper.get(url);
        String responseText = response.getResponseBody();

        assertTrue(response.getStatusCode() == 200);
        assertTrue(responseText.contains("<content />"));
    }

    @Test
    public void testUpdateSpaceProperties() throws Exception {
        String url = baseUrl + "/space1";
        String formParams = "spaceName=Test2&spaceAccess=closed";
        HttpResponse response = restHelper.post(url, formParams, true);

        assertTrue(response.getStatusCode() == 200);
        String responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains("space1"));
        assertTrue(responseText.contains("updated"));

        // Make sure the changes were saved
        url = baseUrl + "/space1?properties=true";
        response = restHelper.get(url);
        responseText = response.getResponseBody();

        assertTrue(response.getStatusCode() == 200);
        assertTrue(responseText.contains("<"+nameTag+">Test2</"+nameTag+">"));
        assertTrue(responseText.contains("<"+accessTag+">CLOSED</"+accessTag+">"));
    }

 }