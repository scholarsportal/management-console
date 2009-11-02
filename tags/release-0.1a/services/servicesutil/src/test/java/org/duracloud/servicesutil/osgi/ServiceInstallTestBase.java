package org.duracloud.servicesutil.osgi;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;

import static junit.framework.Assert.assertEquals;

/**
 * @author Andrew Woods
 * Date: Oct 1, 2009
 */
public class ServiceInstallTestBase {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final static String BASE_DIR_PROP = "base.dir";

    protected final static String DUMMY_BUNDLE_FILE_NAME = "junk-bundle.jar";

    protected final static String DUMMY_BUNDLE_TEXT =
            "normally-bundle-is-a-jar-not-text";

    protected final static String ZIP_BAG_FILE_NAME = "replicationservice-1.0.0.zip";

    protected final static String sep = File.separator;

    protected InputStream getDummyBundle() throws FileNotFoundException {
        return new ByteArrayInputStream(DUMMY_BUNDLE_TEXT.getBytes());
    }

    protected InputStream getZipBag() throws FileNotFoundException {
        String baseDir = System.getProperty(BASE_DIR_PROP);
        Assert.assertNotNull(baseDir);

        String resourceDir = baseDir + sep + "src/test/resources/";
        File zipBagFile = new File(resourceDir + ZIP_BAG_FILE_NAME);

        return new FileInputStream(zipBagFile);
    }

    protected void verifyDummyBundleInstalled(String home, boolean exists)
            throws FileNotFoundException, IOException {
        File file = new File(home + sep + DUMMY_BUNDLE_FILE_NAME);
        assertEquals(exists, file.exists());

        if (exists) {
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = br.readLine();
            assertEquals(DUMMY_BUNDLE_TEXT, line);
            br.close();
        }
    }

    protected void verifyZipBagInstalled(String home, boolean exists)
            throws FileNotFoundException, IOException {
        File file = new File(getAttic(home) + sep + ZIP_BAG_FILE_NAME);
        assertEquals("file: " + file.getAbsoluteFile(), exists, file.exists());

        if (exists) {
            ZipFile zip = new ZipFile(file);
            assertTrue(zip.size() > 0);

            Enumeration entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                File entryBundle = new File(home + sep + entry.getName());
                assertTrue(entryBundle.exists());
            }
        }
    }

    protected void deleteDummyBundle(String home) {
        File file = new File(home + sep + DUMMY_BUNDLE_FILE_NAME);
        file.delete();

        File atticFile = new File(getAttic(home) + sep + DUMMY_BUNDLE_FILE_NAME);
        atticFile.delete();
    }

    protected void deleteZipBagBundles(String home) throws IOException {
        File file = new File(getAttic(home) + sep + ZIP_BAG_FILE_NAME);

        if (file.exists()) {
            ZipFile zip = new ZipFile(file);

            Enumeration entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                File entryBundle = new File(home + sep + entry.getName());
                entryBundle.delete();
            }
            file.delete();
        }
    }

    protected File getAttic(String home) {
        return new File(home + sep + "attic");
    }
}
