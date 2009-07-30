
package org.duracloud.servicesadmin.client;

import java.io.File;

import org.apache.commons.httpclient.util.HttpURLConnection;

import org.duracloud.servicesadmin.AbstractServicesAdminOSGiTestBase;
import org.duracloud.servicesutil.client.ServiceUploadClient;
import org.duraspace.common.web.RestHttpHelper;
import org.duraspace.common.web.RestHttpHelper.HttpResponse;

import junit.framework.Assert;

public class TestServiceUploadClient
        extends AbstractServicesAdminOSGiTestBase {

    public void testPostServiceBundle() throws Exception {
        // Allow tomcat to come up.
        Thread.sleep(10000);

        placeholderTest();
        // Use the 'realTest' below instead of 'placeholderTest' when it is ready.
        // realTest();
    }

    private void placeholderTest() throws Exception {
        String url = "http://localhost:8089/servicesadmin-1.0.0";

        RestHttpHelper helper = new RestHttpHelper();
        HttpResponse response = helper.get(url);
        Assert.assertNotNull(response);

        int statusCode = response.getStatusCode();
        assertEquals(HttpURLConnection.HTTP_OK, statusCode);
    }

    private void realTest() throws Exception {
        String url =
                "http://localhost:8089/servicesadmin-1.0.0/services/install";

        ServiceUploadClient client = new ServiceUploadClient();

        File jarFile = new File("/home/awoods/monday-crossword.jpeg");
        HttpResponse response = client.postServiceBundle(url, jarFile);
        Assert.assertNotNull(response);
        assertEquals(HttpURLConnection.HTTP_OK, response.getStatusCode());
    }

}
