
package org.duraspace.customerwebapp.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.core.HttpHeaders;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.common.web.RestHttpHelper;
import org.duraspace.common.web.RestHttpHelper.HttpResponse;

import junit.framework.TestCase;

/**
 * Runtime test of content REST API. The customerwebapp web application must be
 * deployed and available at the baseUrl location in order for these tests to
 * pass.
 *
 * @author Bill Branan
 */
public class TestContentRest
        extends TestCase {

    private static RestHttpHelper restHelper = new RestHttpHelper();

    private static String baseUrl;

    private static final String CONTENT = "<content />";

    private final String nameHeader = BaseRest.CONTENT_NAME_HEADER;

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
        int statusCode = response.getStatusCode();
        assertTrue("status: " + statusCode, statusCode == 200);

        // Add space
        response = RestTestHelper.addSpace(spaceId);
        statusCode = response.getStatusCode();
        assertTrue("status: " + statusCode, statusCode == 201);

        // Add content1 to space
        String url = baseUrl + "/" + spaceId + "/content1";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(RestTestHelper.METADATA_NAME,
                    RestTestHelper.METADATA_VALUE);
        response = restHelper.put(url, CONTENT, headers);
        statusCode = response.getStatusCode();
        assertTrue("status: " + statusCode, statusCode == 201);
    }

    @Override
    @After
    protected void tearDown() throws Exception {
        // Delete content1 from space
        String url = baseUrl + "/" + spaceId + "/content1";
        HttpResponse response = restHelper.delete(url);

        assertTrue(response.getStatusCode() == 200);
        String responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains("content1"));
        assertTrue(responseText.contains("deleted"));

        // Delete space
        response = RestTestHelper.deleteSpace(spaceId);
        assertTrue(response.getStatusCode() == 200);
    }

    @Test
    public void testGetContent() throws Exception {
        String url = baseUrl + "/" + spaceId + "/content1";
        HttpResponse response = restHelper.get(url);

        assertTrue(response.getStatusCode() == 200);
        String content = response.getResponseBody();
        assertNotNull(content);
        assertTrue(CONTENT.equals(content));

        String contentType =
            response.getResponseHeader(HttpHeaders.CONTENT_TYPE).getValue();
        assertNotNull(contentType);
        assertTrue(contentType.contains("text/xml"));
    }

    @Test
    public void testGetContentMetadata() throws Exception {
        String url = baseUrl + "/" + spaceId + "/content1";
        HttpResponse response = restHelper.head(url);
        assertTrue(response.getStatusCode() == 200);

        testMetadata(response, nameHeader, "content1");
        testMetadata(response, HttpHeaders.CONTENT_LENGTH, "11");

        String contentType =
            response.getResponseHeader(HttpHeaders.CONTENT_TYPE).getValue();
        assertNotNull(contentType);
        assertTrue(contentType.contains("text/xml"));

        String contentChecksum =
            response.getResponseHeader("Content-MD5").getValue();
        assertNotNull(contentChecksum);

        String contentETag =
            response.getResponseHeader(HttpHeaders.ETAG).getValue();
        assertNotNull(contentETag);
        assertEquals(contentChecksum, contentETag);

        String contentModified =
            response.getResponseHeader(HttpHeaders.LAST_MODIFIED).getValue();
        assertNotNull(contentModified);

        testMetadata(response,
                     RestTestHelper.METADATA_NAME,
                     RestTestHelper.METADATA_VALUE);
    }

    @Test
    public void testUpdateContentMetadata() throws Exception {
        String url = baseUrl + "/" + spaceId + "/content1";
        Map<String, String> headers = new HashMap<String, String>();
        String newContentName = "Test Content";
        headers.put(nameHeader, newContentName);
        String newContentMime = "text/plain";
        headers.put(HttpHeaders.CONTENT_TYPE, newContentMime);
        String newMetaName = BaseRest.HEADER_PREFIX + "new-metadata";
        String newMetaValue = "New Metadata Value";
        headers.put(newMetaName, newMetaValue);
        HttpResponse response = restHelper.post(url, null, headers);

        assertTrue(response.getStatusCode() == 200);
        String responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains("content1"));
        assertTrue(responseText.contains("updated"));

        // Make sure the changes were saved
        url = baseUrl + "/" + spaceId + "/content1";
        response = restHelper.head(url);

        assertTrue(response.getStatusCode() == 200);

        testMetadata(response, nameHeader, newContentName);
        testMetadata(response, HttpHeaders.CONTENT_TYPE, newContentMime);
        testMetadata(response, newMetaName, newMetaValue);
    }

    private void testMetadata(HttpResponse response, String name, String value)
            throws Exception {
        String metadata = response.getResponseHeader(name).getValue();
        assertNotNull(metadata);
        assertEquals(metadata, value);
    }

}