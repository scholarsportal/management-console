package org.duracloud.durastore.aop;

import junit.framework.TestCase;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.durastore.rest.RestTestHelper;
import org.junit.Before;
import org.junit.Test;
import org.apache.commons.httpclient.HttpStatus;

/**
 * Tests the Retry AOP over storage providers
 *
 * @author Bill Branan
 */
public class TestRetryAdvice extends TestCase {

    private static RestHttpHelper restHelper = new RestHttpHelper();
    private static String baseUrl;

    // Should be set to the same value as maxRetries in RetryAdvice
    private static final int MAX_RETRIES = 3;

    @Override
    @Before
    public void setUp() throws Exception {
        baseUrl = RestTestHelper.getBaseUrl();

        // Initialize the Instance
        HttpResponse response = RestTestHelper.initialize();
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
    }

    @Test
    public void testRetry() throws Exception {
        // Tests for retries up to the maximum
        for(int i=0; i<=MAX_RETRIES; i++) {
            String url = baseUrl + "/" + i + "?storeID=6";
            HttpResponse response = restHelper.get(url);
            String responseText = response.getResponseBody();

            String errText = "Response code was " + response.getStatusCode() +
                             " but the expected code was 200. On retry " + i +
                             ". Response text: " + responseText;
            assertEquals(errText, HttpStatus.SC_OK, response.getStatusCode());
            // The item number signifies the number of retries before success
            errText = "Response Text (" + responseText +
                      ") did not contain <item>"+i+"</item>";
            assertTrue(errText, responseText.contains("<item>"+i+"</item>"));
        }

        // Tests the retries limit
        String url = baseUrl + "/" + MAX_RETRIES+1 + "?storeID=6";
        HttpResponse response = restHelper.get(url);
        String responseText = response.getResponseBody();
        int statusCode = response.getStatusCode();
        String errMsg = "Expected 500 response, instead response code was " +
                        statusCode + ". Response text: " + responseText;
        assertEquals(errMsg, HttpStatus.SC_INTERNAL_SERVER_ERROR, statusCode);
    }

    @Test
    public void testNoRetry() throws Exception {
        String url = baseUrl + "/spaces?storeID=6";
        HttpResponse response = restHelper.get(url);
        String responseText = response.getResponseBody();
        int statusCode = response.getStatusCode();
        String errMsg = "Expected 500 response, instead response code was " +
                        statusCode + ". Response text: " + responseText;
        assertEquals(errMsg, HttpStatus.SC_INTERNAL_SERVER_ERROR, statusCode);
    }



}