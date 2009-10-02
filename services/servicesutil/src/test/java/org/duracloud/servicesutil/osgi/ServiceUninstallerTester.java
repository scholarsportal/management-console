package org.duracloud.servicesutil.osgi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.Enumeration;

import org.duracloud.servicesutil.util.ServiceUninstaller;
import org.duracloud.services.common.error.ServiceException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;
import static org.junit.Assert.assertTrue;

import static junit.framework.Assert.assertNotNull;

/**
 * @author Andrew Woods
 */
public class ServiceUninstallerTester
        extends ServiceInstallTestBase {

    private final ServiceUninstaller uninstaller;
    private final String home;

    public ServiceUninstallerTester(ServiceUninstaller uninstaller) {
        assertNotNull(uninstaller);
        this.uninstaller = uninstaller;

        home = uninstaller.getBundleHome();
        assertNotNull(home);
    }

    public void testServiceUninstaller() throws Exception {
        testDummyUninstall();
        testZipUninstall();
    }

    private void testDummyUninstall() throws Exception {
        String attic = getAttic(home).getPath();
        createDummyFile(home);
        createDummyFile(attic);

        verifyDummyBundleInstalled(home, true);
        verifyDummyBundleInstalled(attic, true);

        uninstaller.uninstall(DUMMY_BUNDLE_FILE_NAME);
        verifyDummyBundleInstalled(home, false);
        verifyDummyBundleInstalled(attic, false);
    }

    private void createDummyFile(String dir) throws Exception {
        File file = new File(dir + sep + DUMMY_BUNDLE_FILE_NAME);
        BufferedWriter bw =
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));

        bw.write(DUMMY_BUNDLE_TEXT);
        bw.close();
    }

    private void testZipUninstall() throws Exception {
        createZipBag(home);
        verifyZipBagInstalled(home, true);

        uninstaller.uninstall(ZIP_BAG_FILE_NAME);
        verifyZipBagInstalled(home, false);
    }

    private void createZipBag(String home) throws IOException {
        // Place zip in attic.
        File atticFile = getFromAttic(home, ZIP_BAG_FILE_NAME);
        FileOutputStream atticStream = FileUtils.openOutputStream(atticFile);
        IOUtils.copy(getZipBag(), atticStream);

        // Place contents of zip in home.
        ZipFile zip = new ZipFile(getFromAttic(home, ZIP_BAG_FILE_NAME));
        assertTrue(zip.size() > 0);

        Enumeration entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String entryName = entry.getName();
            InputStream entryInStream = zip.getInputStream(entry);

            File entryFile = new File(home + sep + entryName);
            FileOutputStream entryOutStream = FileUtils.openOutputStream(entryFile);

            entryOutStream.close();
            entryInStream.close();
        }

        atticStream.close();
    }

    private File getFromAttic(String home, String name) {
        return new File(getAttic(home) + sep + ZIP_BAG_FILE_NAME);
    }

}
