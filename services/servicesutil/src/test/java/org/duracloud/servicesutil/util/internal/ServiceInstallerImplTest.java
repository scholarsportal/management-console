
package org.duracloud.servicesutil.util.internal;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duracloud.services.common.error.ServiceException;

import junit.framework.Assert;

public class ServiceInstallerImplTest {

    private ServiceInstallerImpl installer;

    private final String ATTIC = "attic" + File.separator;

    private final String name = "service-x";

    private final String nameTxt = name + ".txt";

    private final String nameJar = name + ".jar";

    private final String nameZip = name + ".zip";

    private final String serviceContent0 = "not-much-content";

    private final String serviceContent1 = "not-much-more-content";

    private final String entryName0 = "entry-name.jar";

    private final String entryName1 = "entry-name.txt";

    private final InputStream bagTxt =
            new ByteArrayInputStream(serviceContent0.getBytes());

    private InputStream bagJar;

    private InputStream bagZip;

    @Before
    public void setUp() throws Exception {
        String tmpHome = createBundleHome();

        installer = new ServiceInstallerImpl();
        installer.setBundleHome(tmpHome);

        bagJar = createBagJar();
        bagZip = createBagZip();
    }

    private String createBundleHome() {
        String tmp = System.getProperty("java.io.tmpdir");
        Assert.assertNotNull(tmp);

        String tmpHome = tmp + File.separator + "bundle-home" + File.separator;
        return tmpHome;
    }

    private InputStream createBagZip() throws Exception {
        return createBagArchive(File.createTempFile(name, ".zip"));
    }

    private InputStream createBagJar() throws Exception {
        return createBagArchive(File.createTempFile(name, ".jar"));
    }

    private InputStream createBagArchive(File file) throws Exception {
        file.deleteOnExit();

        ZipOutputStream zipOutput =
                new ZipOutputStream(new FileOutputStream(file));
        addEntry(zipOutput, entryName0, serviceContent0);
        addEntry(zipOutput, entryName1, serviceContent1);

        zipOutput.close();

        return new FileInputStream(file.getPath());
    }

    private void addEntry(ZipOutputStream zipOutput,
                          String entryName,
                          String content) throws IOException {
        ZipEntry entry = new ZipEntry(entryName);
        zipOutput.putNextEntry(entry);
        zipOutput.write(content.getBytes());
        zipOutput.closeEntry();
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteQuietly(new File(installer.getBundleHome()));

        if (bagTxt != null) {
            bagTxt.close();
        }
        if (bagJar != null) {
            bagJar.close();
        }
        if (bagZip != null) {
            bagZip.close();
        }
    }

    @Test
    public void testInit() throws Exception {
        File attic = new File(installer.getBundleHome() + ATTIC);
        installer.init();
        Assert.assertTrue(attic.exists());
    }

    @Test
    public void testTxtInstall() throws Exception {
        try {
            installer.install(nameTxt, bagTxt);
            Assert.fail("Should throw exception for unsupported file type");
        } catch (ServiceException e) {
        }

        verifyExists(false, new File(installer.getBundleHome() + nameTxt));
    }

    @Test
    public void testZipInstall() throws Exception {
        installer.install(nameZip, bagZip);

        String atticNameZip = installer.getBundleHome() + ATTIC + nameZip;
        File atticBag = new File(atticNameZip);
        verifyBag(atticBag);

        File bundle = new File(installer.getBundleHome() + entryName0);
        verifyBundle(bundle, serviceContent0);

        File noBundle = new File(installer.getBundleHome() + entryName1);
        verifyExists(false, noBundle);
    }

    @Test
    public void testJarInstall() throws Exception {
        installer.install(nameJar, bagJar);

        String atticNameJar = installer.getBundleHome() + ATTIC + nameJar;
        File installedBundle = new File(atticNameJar);
        verifyBag(installedBundle);

        File bundle = new File(installer.getBundleHome() + nameJar);
        verifyExists(true, bundle);
    }

    private void verifyExists(boolean exists, File file) {
        Assert.assertNotNull(file);
        Assert.assertEquals(exists, file.exists());
    }

    private void verifyBag(File bundle) throws Exception {
        verifyExists(true, bundle);

        ZipFile file = new ZipFile(bundle);
        verifyEntry(file, entryName0, serviceContent0);
        verifyEntry(file, entryName1, serviceContent1);
    }

    private void verifyEntry(ZipFile file, String entryName, String content)
            throws Exception, IOException {
        ZipEntry entry = file.getEntry(entryName);
        Assert.assertNotNull(entry);
        Assert.assertEquals(entryName, entry.getName());

        verifyContent(file.getInputStream(entry), content);
    }

    private void verifyBundle(File bundle, String content) throws Exception {
        verifyExists(true, bundle);
        verifyContent(new FileInputStream(bundle), content);
    }

    private void verifyContent(InputStream inStream, String content)
            throws Exception {
        InputStreamReader sr = new InputStreamReader(inStream);
        BufferedReader br = new BufferedReader(sr);

        String line = br.readLine();
        StringBuilder contentRead = new StringBuilder();
        while (line != null) {
            contentRead.append(line);
            line = br.readLine();
        }

        Assert.assertEquals(content, contentRead.toString());
    }
}
