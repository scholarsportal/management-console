
package org.duracloud.servicesutil.osgi;

import org.duracloud.services.ComputeService;
import org.duracloud.servicesutil.util.ServiceInstaller;
import org.duracloud.servicesutil.util.ServiceLister;
import org.duracloud.servicesutil.util.ServiceStarter;
import org.duracloud.servicesutil.util.ServiceStopper;
import org.duracloud.servicesutil.util.ServiceUninstaller;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;

public class TestServices
        extends AbstractDuracloudOSGiTestBase {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final String SERVICE_INSTALLER_INTERFACE =
            "org.duracloud.servicesutil.util.ServiceInstaller";

    private final String SERVICE_UNINSTALLER_INTERFACE =
            "org.duracloud.servicesutil.util.ServiceUninstaller";

    private final String SERVICE_LISTER_INTERFACE =
            "org.duracloud.servicesutil.util.ServiceLister";

    private final String SERVICE_STARTER_INTERFACE =
            "org.duracloud.servicesutil.util.ServiceStarter";

    private final String SERVICE_STOPPER_INTERFACE =
            "org.duracloud.servicesutil.util.ServiceStopper";

    private ServiceInstaller installer;

    private ServiceUninstaller uninstaller;

    private ServiceLister lister;

    private ServiceStarter starter;

    private ServiceStopper stopper;

    @SuppressWarnings("unused")
    private ComputeService addedToForceManifestTypeImport;

    public void testServiceInstaller() throws Exception {
        log.debug("testing ServiceInstaller");

        ServiceInstallerTester tester =
                new ServiceInstallerTester(getInstaller());
        tester.testServiceInstaller();
    }

    public void testServiceUninstaller() throws Exception {
        log.debug("testing ServiceUninstaller");

        ServiceUninstallerTester tester =
                new ServiceUninstallerTester(getUninstaller());

        tester.testServiceUninstaller();
    }

    public void testServiceLister() throws Exception {
        log.debug("testing ServiceLister");

        ServiceListerTester tester = new ServiceListerTester(getLister());
        tester.testServiceLister();
    }

    public void testServiceStarter() {
        log.debug("testing ServiceStarter");
    }

    public void testServiceStopper() {
        log.debug("testing ServiceStopper");
    }

    protected Object getService(String serviceInterface) {
        ServiceReference ref =
                bundleContext.getServiceReference(serviceInterface);

        Assert.assertNotNull("service not found: " + serviceInterface, ref);
        log.debug(getPropsText(ref));

        return bundleContext.getService(ref);
    }

    private String getPropsText(ServiceReference ref) {
        StringBuilder sb = new StringBuilder("properties:");
        for (String key : ref.getPropertyKeys()) {
            sb.append("\tprop: [" + key);
            sb.append(":" + ref.getProperty(key) + "]\n");
        }
        return sb.toString();
    }

    public ServiceInstaller getInstaller() {
        if (installer == null) {
            installer =
                    (ServiceInstaller) getService(SERVICE_INSTALLER_INTERFACE);
        }
        assertNotNull(installer);
        return installer;
    }

    public ServiceUninstaller getUninstaller() {
        if (uninstaller == null) {
            uninstaller =
                    (ServiceUninstaller) getService(SERVICE_UNINSTALLER_INTERFACE);
        }
        assertNotNull(uninstaller);
        return uninstaller;
    }

    public ServiceLister getLister() {
        if (lister == null) {
            lister = (ServiceLister) getService(SERVICE_LISTER_INTERFACE);
        }
        assertNotNull(lister);
        return lister;
    }

    public ServiceStarter getStarter() {
        if (starter == null) {
            starter = (ServiceStarter) getService(SERVICE_STARTER_INTERFACE);
        }
        assertNotNull(starter);
        return starter;
    }

    public ServiceStopper getStoppper() {
        if (stopper == null) {
            stopper = (ServiceStopper) getService(SERVICE_STOPPER_INTERFACE);
        }
        assertNotNull(stopper);
        return stopper;
    }

}
