
package org.duracloud.servicesutil.osgi;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.assertEquals;

public class ServiceInstallTestBase {

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected final static String TEST_BUNDLE_FILE_NAME = "junk-bundle.txt";

    protected final static String BUNDLE_TEXT =
            "normally-bundle-is-a-jar-not-text";

    protected InputStream getTestBundle() throws FileNotFoundException {
        return new ByteArrayInputStream(BUNDLE_TEXT.getBytes());
    }

    protected void verifyTestBundleInstalled(String home, boolean exists)
            throws FileNotFoundException, IOException {
        File file = new File(home + File.separator + TEST_BUNDLE_FILE_NAME);
        assertEquals(exists, file.exists());

        if (exists) {
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = br.readLine();
            assertEquals(BUNDLE_TEXT, line);
            br.close();
        }
    }

    protected void deleteTestBundle(String home) {
        File file = new File(home + File.separator + TEST_BUNDLE_FILE_NAME);
        file.delete();
    }

}
