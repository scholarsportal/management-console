package org.duracloud.durastore.rest;

import junit.framework.TestCase;
import org.apache.commons.httpclient.HttpStatus;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Runtime test of space REST API. The durastore
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
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        // Add space
        response = RestTestHelper.addSpace(spaceId);
        assertEquals(HttpStatus.SC_CREATED, response.getStatusCode());
    }

    @Override
    @After
    protected void tearDown() throws Exception {
        HttpResponse response =
            RestTestHelper.deleteSpace(spaceId);

        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
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

        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        assertTrue(responseText.contains("<spaces>"));
    }

    @Test
    public void testGetSpaceMetadata() throws Exception {
        String url = baseUrl + "/" + spaceId;
        HttpResponse response = restHelper.head(url);
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        testMetadata(response,
                     BaseRest.SPACE_ACCESS_HEADER,
                     RestTestHelper.SPACE_ACCESS);
        testMetadata(response,
                     RestTestHelper.METADATA_NAME,
                     RestTestHelper.METADATA_VALUE);
    }

    @Test
    public void testGetSpaceContents() throws Exception {
        addContent();

        String spaceUrl = baseUrl + "/" + spaceId;

        // Get complete list
        String url = spaceUrl;
        HttpResponse response = restHelper.get(url);
        String responseText = response.getResponseBody();
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        List<String> contentIds = parseContentList(responseText);
        assertEquals(3, contentIds.size());

        // MaxResults + Marker test
        url = spaceUrl + "?maxResults=2";
        response = restHelper.get(url);
        responseText = response.getResponseBody();
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        contentIds = parseContentList(responseText);
        assertEquals(2, contentIds.size());

        String lastContentId = contentIds.get(contentIds.size() - 1);
        url = spaceUrl + "?maxResults=2&marker=" + lastContentId;
        response = restHelper.get(url);
        responseText = response.getResponseBody();
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        contentIds = parseContentList(responseText);
        assertEquals(1, contentIds.size());

        // Prefix test
        url = spaceUrl + "?prefix=test";
        response = restHelper.get(url);
        responseText = response.getResponseBody();
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        contentIds = parseContentList(responseText);
        assertEquals(2, contentIds.size());
        for(String contentId : contentIds) {
            assertTrue(contentId.startsWith("test"));
        }        
    }

    private void addContent() throws Exception {
        String content = "<content />";
        String[] contentIds = {"test1", "test2", "check3"};

        // Add content to space
        for(String contentId : contentIds) {
            String url = baseUrl + "/" + spaceId + "/" + contentId;
            HttpResponse response = restHelper.put(url, content, null);
            int statusCode = response.getStatusCode();
            assertEquals(HttpStatus.SC_CREATED, statusCode);
        }
    }

    private List<String> parseContentList(String responseText)
        throws Exception {
        InputStream is =
            new ByteArrayInputStream(responseText.getBytes());
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(is);
        Element spaceElem = doc.getRootElement();

        List<String> contentItems = new ArrayList<String>();
        Iterator<?> spaceContents = spaceElem.getChildren().iterator();
        while (spaceContents.hasNext()) {
            Element contentElem = (Element) spaceContents.next();
            contentItems.add(contentElem.getTextTrim());
        }
        return contentItems;
    }

    @Test
    public void testUpdateSpaceMetadata() throws Exception {
        String url = baseUrl + "/" + spaceId;

        // Add metadata
        Map<String, String> headers = new HashMap<String, String>();
        String newSpaceAccess = "CLOSED";
        headers.put(BaseRest.SPACE_ACCESS_HEADER, newSpaceAccess);
        String newSpaceMetadata = "Updated Space Metadata";
        headers.put(RestTestHelper.METADATA_NAME, newSpaceMetadata);
        HttpResponse response = restHelper.post(url, null, headers);

        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        String responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains(spaceId));
        assertTrue(responseText.contains("updated"));

        // Make sure the changes were saved
        response = restHelper.head(url);
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        testMetadata(response, BaseRest.SPACE_ACCESS_HEADER, newSpaceAccess);
        testMetadata(response, RestTestHelper.METADATA_NAME, newSpaceMetadata);

        // Remove metadata
        headers.remove(RestTestHelper.METADATA_NAME);
        response = restHelper.post(url, null, headers);

        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains(spaceId));
        assertTrue(responseText.contains("updated"));

        response = restHelper.head(url);
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());

        testNoMetadata(response, RestTestHelper.METADATA_NAME);
    }

    private void testMetadata(HttpResponse response, String name, String value)
            throws Exception {
        String metadata = response.getResponseHeader(name).getValue();
        assertNotNull(metadata);
        assertEquals(metadata, value);
    }

    private void testNoMetadata(HttpResponse response,
                                String name) throws Exception {
        assertNull(response.getResponseHeader(name));
    }

    @Test
    public void testNotFound() throws Exception {
        String invalidSpaceId = "non-existant-space";
        String url = baseUrl + "/" + invalidSpaceId;

        // Get Space
        HttpResponse response = restHelper.get(url);
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusCode());

        // Get Space Metadata
        response = restHelper.head(url);
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusCode());

        // Set Space Metadata
        response = restHelper.post(url, null, null);
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusCode());

        // Delete Space
        response = restHelper.delete(url);
        assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusCode());        
    }

 }