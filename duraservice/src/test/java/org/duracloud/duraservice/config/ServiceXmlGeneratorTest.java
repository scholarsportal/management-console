package org.duracloud.duraservice.config;

import org.duracloud.common.util.ApplicationConfig;
import org.duracloud.serviceconfig.ServiceInfo;
import org.duracloud.serviceconfig.SystemConfig;
import org.duracloud.serviceconfig.user.UserConfig;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Properties;

public class ServiceXmlGeneratorTest {

    private final String VER = "1.0.0";

    @Test
    public void testBuildServiceList() {
        ServiceXmlGenerator serviceXmlGenerator = new ServiceXmlGenerator();
        List<ServiceInfo> serviceInfos = serviceXmlGenerator.buildServiceList();
        Assert.assertNotNull(serviceInfos);

        int NUM_SERVICES = 6;
        Assert.assertEquals(NUM_SERVICES, serviceInfos.size());

        boolean foundHello = false;
        boolean foundReplication = false;
        boolean foundImagemagick = false;
        boolean foundWebapputil = false;
        boolean foundHellowebappwrapper = false;
        boolean foundJ2k = false;
        boolean foundImageconversion = false;
        boolean foundMediaStreaming = false;

        for (ServiceInfo serviceInfo : serviceInfos) {
            String contentId = serviceInfo.getContentId();
            Assert.assertNotNull(contentId);
            if (contentId.equals("helloservice-" + VER + ".jar")) {
                foundHello = true;
                verifyHello();

            } else if (contentId.equals("replicationservice-" + VER + ".zip")) {
                foundReplication = true;
                verifyReplication(serviceInfo);

            } else if (contentId.equals("imagemagickservice-" + VER + ".zip")) {
                foundImagemagick = true;
                verifyImagemagick(serviceInfo);

            } else if (contentId.equals("webapputilservice-" + VER + ".zip")) {
                foundWebapputil = true;
                verifyWebapputil(serviceInfo);

            } else if (contentId.equals("hellowebappwrapper-" + VER + ".zip")) {
                foundHellowebappwrapper = true;
                verifyHellowebappwrapper(serviceInfo);

            } else if (contentId.equals("j2kservice-" + VER + ".zip")) {
                foundJ2k = true;
                verifyJ2k(serviceInfo);

            } else if (contentId.equals(
                "imageconversionservice-" + VER + ".zip")) {
                foundImageconversion = true;
                verifyImageconversion(serviceInfo);

            } else if (contentId.equals(
                "mediastreamingservice-" + VER + ".zip")) {
                foundMediaStreaming = true;
                verifyMediaStreaming(serviceInfo);

            } else {
                Assert.fail("unexpected contentId: " + contentId);
            }
        }

        //Assert.assertTrue(foundHello);
        Assert.assertTrue(foundReplication);
        Assert.assertTrue(foundImagemagick);
        Assert.assertTrue(foundWebapputil);
        //Assert.assertTrue(foundHellowebappwrapper);
        Assert.assertTrue(foundJ2k);
        Assert.assertTrue(foundImageconversion);
    }

    private void verifyHello() {
        Assert.assertTrue("I need an implementation", true);
    }

    private void verifyReplication(ServiceInfo serviceInfo) {
        List<SystemConfig> systemConfigs = serviceInfo.getSystemConfigs();
        Assert.assertNotNull(systemConfigs);
        Assert.assertEquals(6, systemConfigs.size());

        verifyDurastoreCredential(systemConfigs);
    }

    private void verifyImagemagick(ServiceInfo serviceInfo) {
        Assert.assertTrue("I need an implementation", true);
    }

    private void verifyWebapputil(ServiceInfo serviceInfo) {
        Assert.assertTrue("I need an implementation", true);
    }

    private void verifyHellowebappwrapper(ServiceInfo serviceInfo) {
        Assert.assertTrue("I need an implementation", true);
    }

    private void verifyJ2k(ServiceInfo serviceInfo) {
        Assert.assertTrue("I need an implementation", true);
    }

    private void verifyImageconversion(ServiceInfo serviceInfo) {
        List<SystemConfig> systemConfigs = serviceInfo.getSystemConfigs();
        Assert.assertNotNull(systemConfigs);
        Assert.assertEquals(6, systemConfigs.size());

        verifyDurastoreCredential(systemConfigs);
    }

    private void verifyMediaStreaming(ServiceInfo serviceInfo) {
        List<UserConfig> userConfigs = serviceInfo.getUserConfigs();
        Assert.assertNotNull(userConfigs);
        Assert.assertEquals(2, userConfigs.size());

        List<SystemConfig> systemConfigs = serviceInfo.getSystemConfigs();
        Assert.assertNotNull(systemConfigs);
        Assert.assertEquals(5, systemConfigs.size());

        verifyDurastoreCredential(systemConfigs);
    }

    private void verifyDurastoreCredential(List<SystemConfig> systemConfigs) {
        boolean foundUsername = false;
        boolean foundPassword = false;
        for (SystemConfig systemConfig : systemConfigs) {
            String name = systemConfig.getName();
            String value = systemConfig.getValue();
            Assert.assertNotNull(name);
            Assert.assertNotNull(value);

            if (name.equals("username")) {
                foundUsername = true;
                Assert.assertEquals("$DURASTORE-USERNAME", value);
            } else if (name.equals("password")) {
                foundPassword = true;
                Assert.assertEquals("$DURASTORE-PASSWORD", value);
            }
        }
        Assert.assertTrue(foundUsername);
        Assert.assertTrue(foundPassword);
    }

    @Test
    public void testGenerate() throws Exception {
        TestConfig config = new TestConfig();
        String targetDir = config.getTargetDir();
        URI targetDirUri = new URI(targetDir);
        File targetDirFile = new File(targetDirUri);

        ServiceXmlGenerator xmlGenerator = new ServiceXmlGenerator();
        xmlGenerator.generateServiceXml(targetDirFile.getAbsolutePath());
    }

    private class TestConfig extends ApplicationConfig {
        private String propName = "test-duraservice.properties";

        private Properties getProps() throws Exception {
            return getPropsFromResource(propName);
        }

        public String getTargetDir() throws Exception {
            return getProps().getProperty("targetdir");
        }
    }
}