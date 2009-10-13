package org.duracloud.durastore.aop;

import org.junit.Before;
import org.junit.Test;

import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.durastore.rest.RestTestHelper;

import junit.framework.TestCase;

/**
 * Tests the AOP to verify creation storage provider calls
 *
 * @author Bill Branan
 */
public class TestVerifyCreationAdvice extends TestCase {

    private static RestHttpHelper restHelper = new RestHttpHelper();
    private static String baseUrl;

    // Should be set to the same value as maxRetries in Verify*Advice
    private static final int MAX_RETRIES = 3;

    @Override
    @Before
    public void setUp() throws Exception {
        baseUrl = RestTestHelper.getBaseUrl();

        // Initialize the Instance
        HttpResponse response = RestTestHelper.initialize();
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testVerifySpaceCreation() throws Exception {
        // Tests for verification checks up to the maximum number of failures
        for(int i=0; i<MAX_RETRIES; i++) {
            String url = baseUrl + "/" + i + "?storeID=7";
            HttpResponse response = restHelper.put(url, null, null);
            assertEquals(201, response.getStatusCode());
        }

        // Not testing at max retries because that begins to overlap with retry advice
    }

}