
package org.duracloud.servicesadmin.osgi;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.services.ComputeService;
import org.duracloud.services.common.error.ServiceException;
import org.duracloud.servicesutil.client.ServiceUploadClient;
import org.duracloud.servicesutil.util.ServiceInstaller;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;

public class TestServiceAdminWepApp
        extends AbstractServicesAdminOSGiTestBasePax {

    private final static Logger log =
            LoggerFactory.getLogger(TestServiceAdminWepApp.class);

    private final static int MAX_TRIES = 10;

    // Port:8089 is defined in the 'tomcatconfig' project
    private final static String BASE_URL =
            "http://localhost:8089/org.duracloud.services.admin_1.0.0";

    private final static String TEST_BUNDLE_FILE_NAME =
            "replicationservice-1.0.0.jar";

    private final static String TEST_SERVICE_FILTER =
            "(duraService=replication)";

    private final static String TEST_SERVICE_NAME = "ReplicationService";

    private ServiceInstaller installer;

    private ServiceUploadClient client;

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
                new ServiceInstallUninstallFlowTester(getBundleContext(),
                                                      getTestBundleFromResourceDir(),
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
        String home = getInstaller().getBundleHome();
        Assert.assertNotNull(home);
        log.debug("serviceadmin bundle-home: '" + home + "'");
        return home + File.separator;
    }

    private ServiceUploadClient getClient() {
        if (client == null) {
            client = new ServiceUploadClient();
            client.setRester(new RestHttpHelper());
            client.setBaseURL(BASE_URL);
        }
        return client;
    }

    private ServiceInstaller getInstaller() throws Exception {
        if (installer == null) {
            installer =
                    (ServiceInstaller) getService(ServiceInstaller.class
                            .getName());
        }
        Assert.assertNotNull(installer);
        return installer;
    }

    private Object getService(String serviceInterface) throws Exception {
        return getService(serviceInterface, null, getBundleContext());
    }

    private static Object getService(String serviceInterface,
                                     String filter,
                                     BundleContext ctxt) throws Exception {
        Assert.assertNotNull(ctxt);

        ServiceReference[] refs =
                ctxt.getServiceReferences(serviceInterface, filter);

        int count = 0;
        while ((refs == null || refs.length == 0) && count < MAX_TRIES) {
            count++;
            log.debug("Trying to find service: '" + serviceInterface + "'");
            Thread.sleep(1000);
            refs = ctxt.getServiceReferences(serviceInterface, filter);
        }

        if (refs == null || refs.length == 0) {
            throw new ServiceException("service not found: " + serviceInterface);
        }
        log.debug(getPropsText(refs[0]));
        return ctxt.getService(refs[0]);
    }

    protected static ComputeService getTestService(BundleContext ctxt)
            throws Exception {
        return (ComputeService) getService(ComputeService.class.getName(),
                                           TEST_SERVICE_FILTER,
                                           ctxt);
    }

    protected static boolean testServiceFound(String serviceName) {
        return serviceName.contains(TEST_SERVICE_NAME);
    }
}
