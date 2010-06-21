
package org.duracloud.servicesadminclient.webapp;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.servicesadminclient.ServicesAdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;

public class TestServiceAdminWepApp {

    private final static Logger log =
            LoggerFactory.getLogger(TestServiceAdminWepApp.class);

    private static final String FILE_INSTALL_PROP = "felix.fileinstall.dir";

    protected static final String BASE_DIR_PROP = "base.dir";

    // Port:8089 is defined in the 'tomcatconfig' project
    private final static String BASE_URL =
            "http://localhost:8089/org.duracloud.services.admin_1.0.0";

    private final static String TEST_BUNDLE_FILE_NAME =
            "helloservice-1.0.0.jar";

    private final static String TEST_SERVICE_NAME = "HelloService";

    private ServicesAdminClient client;

    @Before
    public void setUp() throws Exception {
        deleteInstalledBundle();
    }

    @After
    public void tearDown() throws Exception {
        deleteInstalledBundle();
    }

    private void deleteInstalledBundle() throws Exception {
        new File(getBundleHome() + TEST_BUNDLE_FILE_NAME).delete();
    }

    @Test
    public void testServiceInstallUninstallFlow() throws Exception {
        ServiceInstallUninstallFlowTester tester =
                new ServiceInstallUninstallFlowTester(getTestBundleFromResourceDir(),
                                                      getClient());
        tester.testNewServiceFlow();
    }

    @Test
    public void testServiceConfiguration() throws Exception {
        ServiceConfigurationTester tester =
                new ServiceConfigurationTester(getClient());
        tester.testServiceConfiguration();
    }

    private File getTestBundleFromResourceDir() throws Exception {
        String baseDir = System.getProperty(BASE_DIR_PROP);
        Assert.assertNotNull(baseDir);

        String resourceDir = baseDir + File.separator + "src/test/resources/";

        return new File(resourceDir + TEST_BUNDLE_FILE_NAME);
    }

    private String getBundleHome() throws Exception {
        String home = System.getProperty(FILE_INSTALL_PROP);
        Assert.assertNotNull(home);
        log.debug("serviceadmin bundle-home: '" + home + "'");
        return home + File.separator;
    }

    private ServicesAdminClient getClient() {
        if (client == null) {
            client = new ServicesAdminClient();
            client.setRester(new RestHttpHelper());
            client.setBaseURL(BASE_URL);
        }
        return client;
    }

    protected static boolean testServiceFound(String serviceName) {
        return serviceName.contains(TEST_SERVICE_NAME);
    }
}
