package org.duracloud.duradmin;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.duradmin.config.DuradminConfig;

import junit.framework.TestCase;

/**
 * Runtime test of Duradmin. The customerwebapp
 * web application must be deployed and available
 * in order for these tests to pass.
 *
 * @author Bill Branan
 */
public class TestDuradmin
        extends TestCase {

    private static RestHttpHelper restHelper = new RestHttpHelper();
    private static String baseUrl;

    @Override
    @Before
    protected void setUp() throws Exception {
        String host = DuradminConfig.getHost();
        String port = DuradminConfig.getPort();
        baseUrl = "http://" + host + ":" + port + "/duradmin";
    }

    @Override
    @After
    protected void tearDown() throws Exception {
    }

    @Test
    public void testSpaces() throws Exception {
        String url = baseUrl + "/spaces.htm";
        HttpResponse response = restHelper.get(url);
        assertTrue(response.getStatusCode() == 200);

        String responseText = response.getResponseBody();
        assertNotNull(responseText);

        url = baseUrl + "/spaces.htm";
        response = restHelper.get(url);
        assertTrue(response.getStatusCode() == 200);

        String forwardedResponseText = response.getResponseBody();
        assertNotNull(forwardedResponseText);

        assertEquals(responseText, forwardedResponseText);
    }

 }