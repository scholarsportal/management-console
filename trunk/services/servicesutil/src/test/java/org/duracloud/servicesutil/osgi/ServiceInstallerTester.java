package org.duracloud.servicesutil.osgi;

import org.duracloud.servicesutil.util.ServiceInstaller;
import org.duracloud.services.common.error.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertNotNull;

import java.io.IOException;

/**
 * @author Andrew Woods
 */
public class ServiceInstallerTester
        extends ServiceInstallTestBase {

    private final ServiceInstaller installer;
    
    private final String home;

    public ServiceInstallerTester(ServiceInstaller installer) {
        assertNotNull(installer);
        this.installer = installer;

        home = installer.getBundleHome();
        assertNotNull(home);
    }

    public void testServiceInstaller() throws Exception {
        testDummyInstall();
        testJarInstall();
        testZipInstall();
    }

    private void testDummyInstall() throws Exception {
        verifyDummyBundleInstalled(home, false);

        installer.install(DUMMY_BUNDLE_FILE_NAME, getDummyBundle());
        verifyDummyBundleInstalled(home, true);

        deleteDummyBundle(home);
    }

    private void testJarInstall() {
        // not yet implemented.
    }

    private void testZipInstall() throws IOException, ServiceException {
        verifyZipBagInstalled(home, false);

        installer.install(ZIP_BAG_FILE_NAME, getZipBag());
        verifyZipBagInstalled(home, true);

        deleteZipBagBundles(home);
    }

}
