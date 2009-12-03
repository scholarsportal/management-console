package org.duracloud.services.webapputil;

import org.apache.commons.io.IOUtils;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.services.webapputil.tomcat.TomcatUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Andrew Woods
 *         Date: Nov 30, 2009
 */
public class WebAppUtilTest {

    private WebAppUtil webappUtil;
    private String serviceId = "hello";
    private int port = 18080;
    private InputStream war;
    private URL url;

    private RestHttpHelper httpHelper = new RestHttpHelper();

    @Before
    public void setUp() throws FileNotFoundException {
        File resourceDir = new File("src/test/resources");

        TomcatUtil tomcatUtil = new TomcatUtil();
        tomcatUtil.setBinariesZipName("apache-tomcat-6.0.20.zip");
        tomcatUtil.setResourceDir(resourceDir.getAbsolutePath());

        webappUtil = new WebAppUtil();
        webappUtil.setBaseInstallDir(System.getProperty("java.io.tmpdir"));
        webappUtil.setNextPort(port);
        webappUtil.setTomcatUtil(tomcatUtil);

        war = new FileInputStream(new File(resourceDir,
                                           "hellowebapp-1.0.0.war"));
    }

    @After
    public void tearDown() {
        try {
            webappUtil.unDeploy(url);
        } catch (Exception e) {
        }
        webappUtil = null;
        IOUtils.closeQuietly(war);
    }

    @Test
    public void testDeploy() throws Exception {
        url = webappUtil.deploy(serviceId, war);
        Thread.sleep(3000);

        verifyDeployment(url, true);
    }

    private void verifyDeployment(URL url, boolean success) throws Exception {
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

    @Test
    public void testUnDeploy() throws Exception {
        url = webappUtil.deploy(serviceId, war);
        Thread.sleep(3000);
        verifyDeployment(url, true);

        webappUtil.unDeploy(url);
        verifyDeployment(url, false);
    }

}
