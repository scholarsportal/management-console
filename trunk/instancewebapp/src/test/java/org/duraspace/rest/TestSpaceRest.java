package org.duraspace.rest;

import org.junit.Test;

import org.duraspace.common.web.RestHttpHelper;
import org.duraspace.common.web.RestHttpHelper.HttpResponse;

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
    private static String baseUrl = "http://localhost:8080/instancewebapp";

    @Test
    public void testGetSpaces() throws Exception {
        String url = baseUrl + "/space/customer1";
        HttpResponse response = restHelper.get(url);

        assertTrue(response.getStatusCode() == 200);
        assertTrue("<spaces />".equals(response.getResponseBody()));
    }

    @Test
    public void testGetSpaceProperties() throws Exception {
        String url = baseUrl + "/space/customer1/space1?properties=true";
        HttpResponse response = restHelper.get(url);

        assertTrue(response.getStatusCode() == 200);
        assertTrue("<space />".equals(response.getResponseBody()));
    }

    @Test
    public void testGetSpaceContents() throws Exception {
        String url = baseUrl + "/space/customer1/space1";
        HttpResponse response = restHelper.get(url);

        assertTrue(response.getStatusCode() == 200);
        assertTrue("<contents />".equals(response.getResponseBody()));
    }

    @Test
    public void testAddSpace() throws Exception {
        String url = baseUrl + "/space/customer1/space1";
        String formParams = "spaceName=test&spaceAccess=open";
        HttpResponse response = restHelper.put(url, formParams, true);

        assertTrue(response.getStatusCode() == 201);
    }

    @Test
    public void testUpdateSpaceProperties() throws Exception {
        String url = baseUrl + "/space/customer1/space1";
        String formParams = "spaceName=test2&spaceAccess=closed";
        HttpResponse response = restHelper.post(url, formParams, true);

        assertTrue(response.getStatusCode() == 200);
        String responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains("space1"));
        assertTrue(responseText.contains("updated"));
    }

    @Test
    public void testDeleteSpace() throws Exception {
        String url = baseUrl + "/space/customer1/space1";
        HttpResponse response = restHelper.delete(url);

        assertTrue(response.getStatusCode() == 200);
        String responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains("space1"));
        assertTrue(responseText.contains("deleted"));
    }
 }