package org.duracloud.durastore.rest;

import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
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
public class TestContentRest {

    private static RestHttpHelper restHelper = new RestHttpHelper();

    private static String baseUrl;

    private static final String CONTENT = "<content />";

    private static String spaceId;

    private static String[] contentIds = {"content1",
                                          "dir0/dir1/content2",
                                          "dir0/dir1/content3?storeID=0"};

    static {
        String random = String.valueOf(new Random().nextInt(99999));
        spaceId = "space" + random;
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        baseUrl = RestTestHelper.getBaseUrl();

        // Initialize the Instance
        HttpResponse response = RestTestHelper.initialize();
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Add space
        response = RestTestHelper.addSpace(spaceId);
        statusCode = response.getStatusCode();
        assertEquals(201, statusCode);
    }

    @Before
    public void setUp() throws Exception {

        for (String contentId : contentIds) {
            setUpContent(contentId);
        }
    }

    private void setUpContent(String contentId) throws Exception {
        HttpResponse response;
        int statusCode;

        // Add content to space
        String url = baseUrl + "/" + spaceId + "/" + contentId;
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(RestTestHelper.METADATA_NAME,
                    RestTestHelper.METADATA_VALUE);
        response = restHelper.put(url, CONTENT, headers);
        statusCode = response.getStatusCode();
        assertEquals(201, statusCode);
    }

    @After
    public void tearDown() throws Exception {

        for (String contentId : contentIds) {
            tearDownContent(contentId);
        }
    }

    private void tearDownContent(String contentId) throws Exception {
        // Delete content from space
        String url = baseUrl + "/" + spaceId + "/" + contentId;
        HttpResponse response = restHelper.delete(url);

        assertEquals(200, response.getStatusCode());
        String responseText = response.getResponseBody();
        assertNotNull(responseText);

        assertTrue(responseText.contains(removeParams(contentId)));
        assertTrue(responseText.contains("deleted"));
    }

    private String removeParams(String contentId) {
        int paramStart = contentId.indexOf('?');
        int endIndex = paramStart > 0 ? paramStart : contentId.length();
        return contentId.substring(0, endIndex);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        // Delete space
        HttpResponse response = RestTestHelper.deleteSpace(spaceId);
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testGetContent() throws Exception {
        for (String contentId : contentIds) {
            doTestGetContent(contentId);
        }
    }

    private void doTestGetContent(String contentId) throws Exception {
        String url = baseUrl + "/" + spaceId + "/" + contentId;
        HttpResponse response = restHelper.get(url);

        assertEquals(200, response.getStatusCode());
        String content = response.getResponseBody();
        assertNotNull(content);
        assertEquals(CONTENT, content);

        String contentType = response.getResponseHeader(HttpHeaders.CONTENT_TYPE)
            .getValue();
        assertNotNull(contentType);
        assertTrue(contentType.contains("text/xml"));
    }

    @Test
    public void testGetContentMetadata() throws Exception {
        for (String contentId : contentIds) {
            doTestGetContentMetadata(contentId);
        }
    }

    private void doTestGetContentMetadata(String contentId) throws Exception {
        String url = baseUrl + "/" + spaceId + "/" + contentId;
        HttpResponse response = restHelper.head(url);
        assertEquals(200, response.getStatusCode());

        verifyMetadata(response, HttpHeaders.CONTENT_LENGTH, "11");

        String contentType = response.getResponseHeader(HttpHeaders.CONTENT_TYPE)
            .getValue();
        assertNotNull(contentType);
        assertTrue(contentType.contains("text/xml"));

        String contentChecksum = response.getResponseHeader("Content-MD5")
            .getValue();
        assertNotNull(contentChecksum);

        String contentETag = response.getResponseHeader(HttpHeaders.ETAG)
            .getValue();
        assertNotNull(contentETag);
        assertEquals(contentChecksum, contentETag);

        String contentModified = response.getResponseHeader(HttpHeaders.LAST_MODIFIED)
            .getValue();
        assertNotNull(contentModified);

        verifyMetadata(response,
                       RestTestHelper.METADATA_NAME,
                       RestTestHelper.METADATA_VALUE);
    }

    @Test
    public void testUpdateContentMetadata() throws Exception {
        for (String contentId : contentIds) {
            doTestUpdateContentMetadata(contentId);
        }
    }

    private void doTestUpdateContentMetadata(String contentId)
        throws Exception {
        String url = baseUrl + "/" + spaceId + "/" + contentId;
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
        assertTrue(responseText.contains(removeParams(contentId)));
        assertTrue(responseText.contains("updated"));

        // Make sure the changes were saved
        url = baseUrl + "/" + spaceId + "/" + contentId;
        response = restHelper.head(url);

        assertEquals(200, response.getStatusCode());

        verifyMetadata(response, HttpHeaders.CONTENT_TYPE, newContentMime);
        verifyMetadata(response, newMetaName, newMetaValue);
    }

    private void verifyMetadata(HttpResponse response,
                                String name,
                                String value) throws Exception {
        String metadata = response.getResponseHeader(name).getValue();
        assertNotNull(metadata);
        assertEquals(metadata, value);
    }

}