package org.duracloud.servicesutil.osgi;

import junit.framework.Assert;
import org.duracloud.services.ComputeService;
import org.duracloud.servicesutil.util.DuraConfigAdmin;
import org.duracloud.servicesutil.util.ServiceInstaller;
import org.duracloud.servicesutil.util.ServiceLister;
import org.duracloud.servicesutil.util.ServiceStarter;
import org.duracloud.servicesutil.util.ServiceStopper;
import org.duracloud.servicesutil.util.ServiceUninstaller;
import org.duracloud.servicesutil.util.catalog.BundleCatalog;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestServices
        extends AbstractDuracloudOSGiTestBasePax {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String HELLOSERVICE_FILTER =
            "(duraService=helloservice)";

    private final int MAX_TRIES = 10;

    private ServiceInstaller installer;

    private ServiceUninstaller uninstaller;

    private ServiceLister lister;

    private ServiceStarter starter;

    private ServiceStopper stopper;

    private DuraConfigAdmin configAdmin;

    private ComputeService helloService;

    @Before
    public void setUp() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
    }

    @After
    public void tearDown() {
        BundleCatalog.clearCatalog();
    }

    @Test
    public void testServiceInstaller() throws Exception {
        log.debug("testing ServiceInstaller");

        ServiceInstallerTester tester =
                new ServiceInstallerTester(getInstaller());
        tester.testServiceInstaller();
    }

    @Test
    public void testServiceUninstaller() throws Exception {
        log.debug("testing ServiceUninstaller");

        ServiceUninstallerTester tester =
                new ServiceUninstallerTester(getUninstaller());

        tester.testServiceUninstaller();
    }

    @Test
    public void testServiceInstallationCycle() throws Exception {
        log.debug("testing ServiceInstallationCycle");

        ServiceInstallationCycleTester tester = new ServiceInstallationCycleTester(
            getInstaller(),
            getUninstaller());

        tester.testServiceInstallationCycle();
    }

    @Test
    public void testServiceLister() throws Exception {
        log.debug("testing ServiceLister");

        ServiceListerTester tester = new ServiceListerTester(getLister());
        tester.testServiceLister();
    }

    @Test
    public void testServiceStarter() throws Exception {
        log.debug("testing ServiceStarter");

        ServiceStarterTester tester = new ServiceStarterTester(getStarter(),
                                                               getLister());
        tester.testServiceStarter();
    }

    @Test
    public void testServiceStopper() throws Exception {
        log.debug("testing ServiceStopper");

        ServiceStopperTester tester = new ServiceStopperTester(getStopper(),
                                                               getLister());
        tester.testServiceStopper();
    }

    @Test
    public void testConfigAdmin() throws Exception {
        log.debug("testing ConfigurationAdmin");

        ConfigAdminTester tester =
                new ConfigAdminTester(getConfigAdmin(), getHelloService());
        tester.testConfigAdmin();
    }

    protected Object getService(String serviceInterface) throws Exception {
        return getService(serviceInterface, null);
    }

    private Object getService(String serviceInterface, String filter)
            throws Exception {
        ServiceReference[] refs =
                getBundleContext().getServiceReferences(serviceInterface,
                                                        filter);

        int count = 0;
        while ((refs == null || refs.length == 0) && count < MAX_TRIES) {
            count++;
            log.debug("Trying to find service: '" + serviceInterface + "'");
            Thread.sleep(1000);
            refs =
                    getBundleContext().getServiceReferences(serviceInterface,
                                                            filter);
        }
        Assert.assertNotNull("service not found: " + serviceInterface, refs[0]);
        log.debug(getPropsText(refs[0]));
        return getBundleContext().getService(refs[0]);
    }

    private String getPropsText(ServiceReference ref) {
        StringBuilder sb = new StringBuilder("properties:");
        for (String key : ref.getPropertyKeys()) {
            sb.append("\tprop: [" + key);
            sb.append(":" + ref.getProperty(key) + "]\n");
        }
        return sb.toString();
    }

    public ServiceInstaller getInstaller() throws Exception {
        if (installer == null) {
            installer =
                    (ServiceInstaller) getService(ServiceInstaller.class
                            .getName());
        }
        Assert.assertNotNull(installer);
        return installer;
    }

    public ServiceUninstaller getUninstaller() throws Exception {
        if (uninstaller == null) {
            uninstaller =
                    (ServiceUninstaller) getService(ServiceUninstaller.class
                            .getName());
        }
        Assert.assertNotNull(uninstaller);
        return uninstaller;
    }

    public ServiceLister getLister() throws Exception {
        if (lister == null) {
            lister = (ServiceLister) getService(ServiceLister.class.getName());
        }
        Assert.assertNotNull(lister);
        return lister;
    }

    public ServiceStarter getStarter() throws Exception {
        if (starter == null) {
            starter =
                    (ServiceStarter) getService(ServiceStarter.class.getName());
        }
        Assert.assertNotNull(starter);
        return starter;
    }

    public ServiceStopper getStopper() throws Exception {
        if (stopper == null) {
            stopper =
                    (ServiceStopper) getService(ServiceStopper.class.getName());
        }
        Assert.assertNotNull(stopper);
        return stopper;
    }

    public DuraConfigAdmin getConfigAdmin() throws Exception {
        if (configAdmin == null) {
            configAdmin =
                    (DuraConfigAdmin) getService(DuraConfigAdmin.class
                            .getName());
        }
        Assert.assertNotNull(configAdmin);
        return configAdmin;
    }

    public ComputeService getHelloService() throws Exception {
        if (helloService == null) {
            helloService =
                    (ComputeService) getService(ComputeService.class.getName(),
                                                HELLOSERVICE_FILTER);
        }
        return helloService;
    }

}
