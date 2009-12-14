package org.duracloud.services.hellowebappwrapper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.duracloud.services.ComputeService;
import org.duracloud.services.common.util.BundleHome;
import org.duracloud.services.webapputil.internal.WebAppUtilImpl;
import org.duracloud.services.webapputil.tomcat.TomcatUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author Andrew Woods
 *         Date: Dec 9, 2009
 */
public class HelloWebappWrapperTest {

    private String testDir = "target/wrapper-test/";

    private HelloWebappWrapper wrapper;
    private String serviceId = "howdywrapper";
    private String url = "http://example.org";
    private BundleHome bundleHome = new BundleHome(testDir);
    private String warName = "hellowebapp-1.0.0.war";

    private WebAppUtilImpl webappUtil;
    private String baseInstallDir = testDir + "tomcat";
    private int port = 38080;

    private TomcatUtil tomcatUtil;
    private String resourceDir = "src/test/resources";
    private String binariesZipName = "apache-tomcat-6.0.20.zip";


    @Before
    public void setUp() throws IOException {
        tomcatUtil = new TomcatUtil();
        tomcatUtil.setResourceDir(resourceDir);
        tomcatUtil.setBinariesZipName(binariesZipName);

        webappUtil = new WebAppUtilImpl();
        webappUtil.setBaseInstallDir(baseInstallDir);
        webappUtil.setNextPort(port);
        webappUtil.setTomcatUtil(tomcatUtil);

        wrapper = new HelloWebappWrapper();
        wrapper.setServiceStatus(ComputeService.ServiceStatus.INSTALLED);
        wrapper.setServiceId(serviceId);
        wrapper.setUrl(url);
        wrapper.setWebappWarName(warName);
        wrapper.setWebappUtil(webappUtil);
        wrapper.setBundleHomeDir(bundleHome.getBaseDir());

        File war = new File(resourceDir, warName);
        File serviceWorkDir = new File(bundleHome.getWork(), serviceId);
        serviceWorkDir.mkdirs();

        FileUtils.copyFileToDirectory(war, serviceWorkDir);
    }

    @After
    public void tearDown() {
        try {
            wrapper.stop();
        } catch (Exception e) {
            // do nothing
        }
        tomcatUtil = null;
        webappUtil = null;
        wrapper = null;
    }

    @Test
    public void testStartStopCycle() throws Exception {
        verifyURL(url);

        ComputeService.ServiceStatus status = wrapper.getServiceStatus();
        Assert.assertNotNull(status);
        Assert.assertEquals(ComputeService.ServiceStatus.INSTALLED, status);

        wrapper.start();
        status = wrapper.getServiceStatus();
        Assert.assertEquals(ComputeService.ServiceStatus.STARTED, status);

        String context = FilenameUtils.getBaseName(warName);
        String newURL = "http://127.\\d.\\d.1:" + port + "/" + context;
        verifyURL(newURL);

        wrapper.stop();
        status = wrapper.getServiceStatus();
        Assert.assertEquals(ComputeService.ServiceStatus.STOPPED, status);

        verifyURL(url);
    }

    private void verifyURL(String expectedURL) {
        Map<String, String> props = wrapper.getServiceProps();
        Assert.assertNotNull(props);

        String urlProp = props.get("url");
        Assert.assertNotNull(urlProp);
        Assert.assertTrue(urlProp.matches(expectedURL));
    }

}
