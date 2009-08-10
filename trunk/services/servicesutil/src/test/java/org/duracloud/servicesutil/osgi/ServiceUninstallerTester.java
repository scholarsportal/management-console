
package org.duracloud.servicesutil.osgi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.duracloud.servicesutil.util.ServiceUninstaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertNotNull;

public class ServiceUninstallerTester
        extends ServiceInstallTestBase {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ServiceUninstaller uninstaller;

    public ServiceUninstallerTester(ServiceUninstaller uninstaller) {
        assertNotNull(uninstaller);
        this.uninstaller = uninstaller;
    }

    public void testServiceUninstaller() throws Exception {
        String home = uninstaller.getBundleHome();
        assertNotNull(home);

        createTestFile(home);
        verifyTestBundleInstalled(home, true);

        uninstaller.uninstall(TEST_BUNDLE_FILE_NAME);
        verifyTestBundleInstalled(home, false);
    }

    private void createTestFile(String home) throws Exception {
        File file = new File(home + File.separator + TEST_BUNDLE_FILE_NAME);
        BufferedWriter bw =
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));

        bw.write(BUNDLE_TEXT);
        bw.close();
    }

}
