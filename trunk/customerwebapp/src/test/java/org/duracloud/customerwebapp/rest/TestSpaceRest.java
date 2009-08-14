package org.duracloud.customerwebapp.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;

import junit.framework.TestCase;

/**
 * Runtime test of space REST API. The customerwebapp
 * web application must be deployed and available at the
 * baseUrl location in order for these tests to pass.
 *
 * @author Bill Branan
 */
public class TestSpaceRest
        extends TestCase {

    private static RestHttpHelper restHelper = new RestHttpHelper();
    private static String baseUrl;

    private static String spaceId;

    static {
        String random = String.valueOf(new Random().nextInt(99999));
        spaceId = "space" + random;
    }

    @Override
    @Before
    protected void setUp() throws Exception {
        baseUrl = RestTestHelper.getBaseUrl();

        // Initialize the Instance
        HttpResponse response = RestTestHelper.initialize();
        assertTrue(response.getStatusCode() == 200);

        // Add space
        response = RestTestHelper.addSpace(spaceId);
        assertTrue(response.getStatusCode() == 201);
    }

    @Override
    @After
    protected void tearDown() throws Exception {
        HttpResponse response =
            RestTestHelper.deleteSpace(spaceId);

        assertTrue(response.getStatusCode() == 200);
        String responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains(spaceId));
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
    public void testGetSpaceMetadata() throws Exception {
        String url = baseUrl + "/" + spaceId;
        HttpResponse response = restHelper.head(url);
        assertTrue(response.getStatusCode() == 200);

        testMetadata(response,
                     BaseRest.SPACE_NAME_HEADER,
                     RestTestHelper.SPACE_NAME);
        testMetadata(response,
                     BaseRest.SPACE_ACCESS_HEADER,
                     RestTestHelper.SPACE_ACCESS);
        testMetadata(response,
                     RestTestHelper.METADATA_NAME,
                     RestTestHelper.METADATA_VALUE);
    }

    @Test
    public void testGetSpaceContents() throws Exception {
        String url = baseUrl + "/" + spaceId;
        HttpResponse response = restHelper.get(url);
        String responseText = response.getResponseBody();

        assertTrue(response.getStatusCode() == 200);
        assertTrue(responseText.contains("<space"));
    }

    @Test
    public void testUpdateSpaceMetadata() throws Exception {
        String url = baseUrl + "/" + spaceId;
        Map<String, String> headers = new HashMap<String, String>();
        String newSpaceName = "Updated Space Name";
        headers.put(BaseRest.SPACE_NAME_HEADER, newSpaceName);
        String newSpaceAccess = "CLOSED";
        headers.put(BaseRest.SPACE_ACCESS_HEADER, newSpaceAccess);
        String newSpaceMetadata = "Updated Space Metadata";
        headers.put(RestTestHelper.METADATA_NAME, newSpaceMetadata);
        HttpResponse response = restHelper.post(url, null, headers);

        assertTrue(response.getStatusCode() == 200);
        String responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains(spaceId));
        assertTrue(responseText.contains("updated"));

        // Make sure the changes were saved
        url = baseUrl + "/" + spaceId;
        response = restHelper.head(url);

        testMetadata(response, BaseRest.SPACE_NAME_HEADER, newSpaceName);
        testMetadata(response, BaseRest.SPACE_ACCESS_HEADER, newSpaceAccess);
        testMetadata(response, RestTestHelper.METADATA_NAME, newSpaceMetadata);
    }

    private void testMetadata(HttpResponse response, String name, String value)
            throws Exception {
        String metadata = response.getResponseHeader(name).getValue();
        assertNotNull(metadata);
        assertEquals(metadata, value);
    }

 }