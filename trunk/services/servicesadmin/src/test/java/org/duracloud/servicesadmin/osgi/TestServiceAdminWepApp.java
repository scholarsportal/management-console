
package org.duracloud.servicesadmin.osgi;

import java.io.File;

import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.services.ComputeService;
import org.duracloud.servicesutil.beans.ComputeServiceBean;
import org.duracloud.servicesutil.client.ServiceUploadClient;
import org.duracloud.servicesutil.util.ServiceInstaller;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;

public class TestServiceAdminWepApp
        extends AbstractServicesAdminOSGiTestBase {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private ServiceInstaller installer;

    @SuppressWarnings("unused")
    private ServiceUploadClient clientForManifest;

    private RestHttpHelper helperForManifest;

    private ComputeServiceBean beanForManifest;

    private ComputeService hello;

    private final static String TEST_BUNDLE_FILE_NAME =
            "helloservice-1.0.0.jar";

    @Override
    protected void onSetUp() {
        try {
            deleteTestBundle(getBundleHome());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onTearDown() {
        try {
            deleteTestBundle(getBundleHome());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteTestBundle(String home) {
        File file = new File(home + File.separator + TEST_BUNDLE_FILE_NAME);
        file.delete();
    }

    public void testServiceInstallUninstallFlow() throws Exception {
        ServiceInstallUninstallFlowTester tester =
                new ServiceInstallUninstallFlowTester(bundleContext);
        tester.testNewServiceFlow();
    }

    public void testServiceConfiguration() throws Exception {
        // Make sure helloService has been installed.
        new ServiceInstallUninstallFlowTester(bundleContext)
                .installTestBundle();

        // Allow test-service to come up.
        Thread.sleep(5000);

        ServiceConfigurationTester tester =
                new ServiceConfigurationTester(getHelloService());
        tester.testServiceConfiguration();
    }

    private String getBundleHome() throws Exception {
        String home = getInstaller().getBundleHome();
        Assert.assertNotNull(home);
        log.debug("serviceadmin bundle-home: '" + home + "'");
        return home;
    }

    private ServiceInstaller getInstaller() throws Exception {
        if (installer == null) {
            installer =
                    (ServiceInstaller) getService(ServiceInstaller.class
                            .getName());
        }
        assertNotNull(installer);
        return installer;
    }

    private ComputeService getHelloService() throws Exception {
        ComputeService hello =
                (ComputeService) getService(ComputeService.class.getName());
        //                                            "(duraKey=helloVal)");
        assertNotNull(hello);
        return hello;
    }

    private Object getService(String serviceInterface) throws Exception {
        return getService(serviceInterface, null);
    }

    private Object getService(String serviceInterface, String filter)
            throws Exception {
        ServiceReference[] refs =
                bundleContext.getServiceReferences(serviceInterface, filter);

        if (refs == null || refs.length == 0) {
            String msg = "Unable to find service: " + serviceInterface;
            log.warn(msg);
            throw new Exception(msg);
        }

        return bundleContext.getService(refs[0]);
    }
}
