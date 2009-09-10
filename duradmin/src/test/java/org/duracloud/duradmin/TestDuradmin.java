package org.duracloud.duradmin;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.duradmin.config.DuradminConfig;

import junit.framework.TestCase;

/**
 * Runtime test of Duradmin. The durastore
 * web application must be deployed and available
 * in order for these tests to pass.
 *
 * @author Bill Branan
 */
public class TestDuradmin
        extends TestCase {

    private static RestHttpHelper restHelper = new RestHttpHelper();
    private static String baseUrl;

    private static String configFileName = "test-duradmin.properties";
    static {
        DuradminConfig.setConfigFileName(configFileName);
    }

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
        assertEquals(200, response.getStatusCode());

        String responseText = response.getResponseBody();
        assertNotNull(responseText);

        url = baseUrl;
        response = restHelper.get(url);
        assertEquals(200, response.getStatusCode());

        String forwardedResponseText = response.getResponseBody();
        assertNotNull(forwardedResponseText);

        assertEquals(responseText, forwardedResponseText);
    }

 }