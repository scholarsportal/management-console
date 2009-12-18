package org.duracloud.services.webapputil;

import org.apache.commons.io.FileUtils;
import org.duracloud.services.common.util.BundleHome;
import org.duracloud.services.webapputil.internal.WebAppUtilImpl;
import org.duracloud.services.webapputil.osgi.WebAppUtilTestBase;
import org.duracloud.services.webapputil.tomcat.TomcatUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Andrew Woods
 *         Date: Nov 30, 2009
 */
public class WebAppUtilTest extends WebAppUtilTestBase {

    private WebAppUtilImpl webappUtil;
    private String bundleHomePath = "target/webapputil-test";
    private String testResourcesPath = "src/test/resources";
    private String serviceId = "hello";
    private String binariesName = "apache-tomcat-6.0.20.zip";
    private String warName = "hellowebapp-1.0.0.war";
    private int port = 18080;

    @Before
    public void setUp() throws IOException {
        BundleHome bundleHome = populateBundleHome();

        TomcatUtil tomcatUtil = new TomcatUtil();
        tomcatUtil.setBinariesZipName(binariesName);

        webappUtil = new WebAppUtilImpl();
        webappUtil.setServiceId(serviceId);
        webappUtil.setBaseInstallDir(System.getProperty("java.io.tmpdir"));
        webappUtil.setNextPort(port);
        webappUtil.setBundleHome(bundleHome);
        webappUtil.setTomcatUtil(tomcatUtil);

        File resourceDir = bundleHome.getServiceWork(serviceId);
        war = new FileInputStream(new File(resourceDir, warName));
    }

    private BundleHome populateBundleHome() throws IOException {
        BundleHome bundleHome = new BundleHome(bundleHomePath);

        File testResources = new File(testResourcesPath);
        File binaries = new File(testResources, binariesName);
        File warFile = new File(testResources, warName);

        File resourceDir = bundleHome.getServiceWork(serviceId);
        FileUtils.copyFileToDirectory(binaries, resourceDir);
        FileUtils.copyFileToDirectory(warFile, resourceDir);

        return bundleHome;
    }

    @After
    public void tearDown() {
        doTearDown(webappUtil);
    }

    @Test
    public void testDeploy() throws Exception {
        url = webappUtil.deploy(serviceId, war);
        Thread.sleep(3000);

        verifyDeployment(url, true);
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
