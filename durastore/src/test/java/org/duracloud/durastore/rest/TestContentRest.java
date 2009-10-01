package org.duracloud.durastore.rest;

import junit.framework.TestCase;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.HttpHeaders;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Runtime test of content REST API. The durastore web application must be
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
        assertEquals(200, statusCode);

        // Add space
        response = RestTestHelper.addSpace(spaceId);
        statusCode = response.getStatusCode();
        assertEquals(201, statusCode);

        // Add content1 to space
        String url = baseUrl + "/" + spaceId + "/content1";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(RestTestHelper.METADATA_NAME,
                    RestTestHelper.METADATA_VALUE);
        response = restHelper.put(url, CONTENT, headers);
        statusCode = response.getStatusCode();
        assertEquals(201, statusCode);
    }

    @Override
    @After
    protected void tearDown() throws Exception {
        // Delete content1 from space
        String url = baseUrl + "/" + spaceId + "/content1";
        HttpResponse response = restHelper.delete(url);

        assertEquals(200, response.getStatusCode());
        String responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains("content1"));
        assertTrue(responseText.contains("deleted"));

        // Delete space
        response = RestTestHelper.deleteSpace(spaceId);
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testGetContent() throws Exception {
        String url = baseUrl + "/" + spaceId + "/content1";
        HttpResponse response = restHelper.get(url);

        assertEquals(200, response.getStatusCode());
        String content = response.getResponseBody();
        assertNotNull(content);
        assertEquals(CONTENT, content);

        String contentType =
            response.getResponseHeader(HttpHeaders.CONTENT_TYPE).getValue();
        assertNotNull(contentType);
        assertTrue(contentType.contains("text/xml"));
    }

    @Test
    public void testGetContentMetadata() throws Exception {
        String url = baseUrl + "/" + spaceId + "/content1";
        HttpResponse response = restHelper.head(url);
        assertEquals(200, response.getStatusCode());

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
        String newContentMime = "text/plain";
        headers.put(HttpHeaders.CONTENT_TYPE, newContentMime);
        String newMetaName = BaseRest.HEADER_PREFIX + "new-metadata";
        String newMetaValue = "New Metadata Value";
        headers.put(newMetaName, newMetaValue);
        HttpResponse response = restHelper.post(url, null, headers);

        assertEquals(200, response.getStatusCode());
        String responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains("content1"));
        assertTrue(responseText.contains("updated"));

        // Make sure the changes were saved
        url = baseUrl + "/" + spaceId + "/content1";
        response = restHelper.head(url);

        assertEquals(200, response.getStatusCode());

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