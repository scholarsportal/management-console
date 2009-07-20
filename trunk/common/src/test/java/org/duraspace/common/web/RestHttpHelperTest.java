
package org.duraspace.common.web;

import java.io.File;
import java.io.IOException;

import java.net.HttpURLConnection;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.duraspace.common.web.RestHttpHelper.HttpResponse;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RestHttpHelperTest {

    private static Server server;

    private static String host = "localhost";

    private static int port = 8088;

    private static String context = "/test";

    private RestHttpHelper helper;

    private Map<String, String> headers;

    @BeforeClass
    public static void startServer() throws Exception {
        server = new Server(port);
        Context root = new Context(server, "/", Context.SESSIONS);
        root.addServlet(new ServletHolder(new MockServlet()), context);
        server.start();
    }

    @Before
    public void setUp() throws Exception {
        helper = new RestHttpHelper();
        headers = new HashMap<String, String>();
        headers.put("header-key0", "header-value0");
    }

    @After
    public void tearDown() throws Exception {
        helper = null;
        headers = null;
    }

    @AfterClass
    public static void stopServer() throws Exception {
        server.stop();
        server = null;
    }

    @Test
    public void testGet() throws Exception {
        HttpResponse response = helper.get(getUrl());
        verifyResponse(response);

    }

    @Test
    public void testDelete() throws Exception {
        HttpResponse response = helper.delete(getUrl());
        verifyResponse(response);
    }

    @Test
    public void testPost() throws Exception {
        String requestContent = "<x>junk</x>";

        HttpResponse response = helper.post(getUrl(), requestContent, headers);
        verifyResponse(response);
    }

    @Test
    public void testPut() throws Exception {
        String requestContent = "<x>junk</x>";

        HttpResponse response = helper.put(getUrl(), requestContent, headers);
        verifyResponse(response);
    }

    @Test
    public void testMultipartPost() throws Exception {
        File file = createTmpFile();

        Part[] parts =
                {new StringPart("param_name", "value"),
                        new FilePart(file.getName(), file)};

        HttpResponse response = helper.multipartPost(getUrl(), parts);
        verifyResponse(response);
    }

    private String getUrl() {
        return "http://" + host + ":" + port + context;
    }

    private File createTmpFile() throws IOException {
        return File.createTempFile("test-file", ".tmp");
    }

    private void verifyResponse(HttpResponse response) {
        assertNotNull(response);
        assertEquals(HttpURLConnection.HTTP_OK, response.getStatusCode());
    }
}
