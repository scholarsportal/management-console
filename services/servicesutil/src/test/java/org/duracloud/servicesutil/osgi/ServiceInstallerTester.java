
package org.duracloud.servicesutil.osgi;

import org.duracloud.servicesutil.util.ServiceInstaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertNotNull;

public class ServiceInstallerTester
        extends ServiceInstallTestBase {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ServiceInstaller installer;

    public ServiceInstallerTester(ServiceInstaller installer) {
        assertNotNull(installer);
        this.installer = installer;
    }

    public void testServiceInstaller() throws Exception {
        // check test bundle not already installed
        String home = installer.getBundleHome();
        assertNotNull(home);

        verifyTestBundleInstalled(home, false);

        // install file
        installer.install(TEST_BUNDLE_FILE_NAME, getTestBundle());

        // check test bundle was installed
        verifyTestBundleInstalled(home, true);

        deleteTestBundle(home);
    }

}
