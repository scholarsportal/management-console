package org.duraspace.rest;

import org.junit.Test;

import org.duraspace.common.web.RestHttpHelper;
import org.duraspace.common.web.RestHttpHelper.HttpResponse;

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

    private static RestHttpHelper restHelper = new RestHttpHelper();
    private static String baseUrl = "http://localhost:8080/instancewebapp";

    @Test
    public void testGetContent() throws Exception {
        String url = baseUrl + "/content/customer1/space1/content1";
        HttpResponse response = restHelper.get(url);

        assertTrue(response.getStatusCode() == 200);
        assertTrue("content".equals(response.getResponseBody()));
    }

    @Test
    public void testGetContentProperties() throws Exception {
        String url = baseUrl + "/content/customer1/space1/content1?properties=true";
        HttpResponse response = restHelper.get(url);

        assertTrue(response.getStatusCode() == 200);
        assertTrue("<content />".equals(response.getResponseBody()));
    }

    @Test
    public void testUpdateContentProperties() throws Exception {
        String url = baseUrl + "/content/customer1/space1/content1";
        String formParams = "contentName=test";
        HttpResponse response = restHelper.post(url, formParams, true);

        assertTrue(response.getStatusCode() == 200);
        String responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains("content1"));
        assertTrue(responseText.contains("updated"));
    }

    @Test
    public void testAddContent() throws Exception {
        String url = baseUrl + "/content/customer1/space1/content1";
        String content = "<content />";
        HttpResponse response = restHelper.put(url, content, false);

        assertTrue(response.getStatusCode() == 200);
        String responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains("content1"));
        assertTrue(responseText.contains("added"));
    }

    @Test
    public void testDeleteContent() throws Exception {
        String url = baseUrl + "/content/customer1/space1/content1";
        HttpResponse response = restHelper.delete(url);

        assertTrue(response.getStatusCode() == 200);
        String responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains("content1"));
        assertTrue(responseText.contains("deleted"));
    }
}