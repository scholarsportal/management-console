package org.duracloud.services.webapputil.osgi;

import org.apache.commons.io.IOUtils;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.services.webapputil.WebAppUtil;
import org.junit.Assert;

import java.io.InputStream;
import java.net.URL;

/**
 * This class aggregates some test methods used in both the unit and
 * osgi-integration tests for WebAppUtil.
 *
 * @author Andrew Woods
 *         Date: Dec 7, 2009
 */
public class WebAppUtilTestBase {

    protected InputStream war;
    protected URL url;

    protected RestHttpHelper httpHelper = new RestHttpHelper();

    protected void doTearDown(WebAppUtil util) {
        try {
            util.unDeploy(url);
        } catch (Exception e) {
        }
        util = null;
        IOUtils.closeQuietly(war);
    }

    protected void verifyDeployment(URL url, boolean success) throws Exception {
        Assert.assertNotNull(url);

        RestHttpHelper.HttpResponse response = null;
        try {
            response = httpHelper.get(url.toString());
            Assert.assertTrue(success);
        } catch (Exception e) {
            Assert.assertTrue(!success);
        }

        if (success) {
            Assert.assertNotNull(response);

            int maxTries = 5;
            int tries = 0;
            while (response.getStatusCode() != 200 && tries++ < maxTries) {
                Thread.sleep(1000);
                response = httpHelper.get(url.toString());
            }
            Assert.assertEquals(200, response.getStatusCode());

            String body = response.getResponseBody();
            Assert.assertNotNull(body);
            Assert.assertTrue(body.contains("Hello from DuraCloud"));
        }
    }
}
