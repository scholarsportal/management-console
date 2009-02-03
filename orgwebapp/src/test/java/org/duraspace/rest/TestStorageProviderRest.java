package org.duraspace.rest;

import junit.framework.TestCase;

import org.junit.Test;

import org.duraspace.rest.RestTestHelper.HttpResponse;

/**
 * Runtime test of storage provider REST API. The orgwebapp
 * web application must be deployed and available at the
 * baseUrl location in order for these tests to pass.
 *
 * @author Bill Branan
 */
public class TestStorageProviderRest
        extends TestCase {

    private static RestTestHelper restHelper = new RestTestHelper();
    private static String baseUrl = "http://localhost:8080/orgwebapp";

    @Test
    public void testGetStorageProviders() throws Exception {
        String url = baseUrl + "/storage";
        HttpResponse response = restHelper.get(url);

        assertTrue(response.getStatusCode() == 200);
        assertTrue("<storageProviders />".equals(response.getResponseBody()));
    }

    @Test
    public void testGetStorageProviderAccounts() throws Exception {
        String url = baseUrl + "/storage/customer1";
        HttpResponse response = restHelper.get(url);

        assertTrue(response.getStatusCode() == 200);
        assertTrue("<storageProviderAccounts />".equals(response.getResponseBody()));
    }

    @Test
    public void testGetStorageProviderAccount() throws Exception {
        String url = baseUrl + "/storage/customer1/provider1";
        HttpResponse response = restHelper.get(url);

        assertTrue(response.getStatusCode() == 200);
        assertTrue("<storageProviderAccount />".equals(response.getResponseBody()));
    }

    @Test
    public void testAddStorageProviderAccount() throws Exception {
        String url = baseUrl + "/storage/customer1/provider1";
        HttpResponse response = restHelper.put(url, null);

        assertTrue(response.getStatusCode() == 201);
    }

    @Test
    public void testCloseStorageProviderAccount() throws Exception {
        String url = baseUrl + "/storage/customer1/provider1";
        HttpResponse response = restHelper.delete(url);

        assertTrue(response.getStatusCode() == 200);
        String responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains("provider1"));
        assertTrue(responseText.contains("closed"));
    }

    @Test
    public void testSetPrimaryStorageProvider() throws Exception {
        String url = baseUrl + "/storage/customer1/provider1";
        HttpResponse response = restHelper.post(url, null);

        assertTrue(response.getStatusCode() == 200);
        String responseText = response.getResponseBody();
        assertNotNull(responseText);
        assertTrue(responseText.contains("provider1"));
        assertTrue(responseText.contains("primary"));
    }
}