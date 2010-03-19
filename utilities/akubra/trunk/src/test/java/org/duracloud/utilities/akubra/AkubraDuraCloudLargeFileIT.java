package org.duracloud.utilities.akubra;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import java.util.HashMap;
import java.util.Map;

import org.akubraproject.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Akubra-DuraCloud Integration Tests for large files.
 *
 * @author Chris Wilper
 */
public class AkubraDuraCloudLargeFileIT extends AkubraDuraCloudITBase {

    private static final byte[] ONE_KB;

    private static final Logger logger =
            LoggerFactory.getLogger(AkubraDuraCloudLargeFileIT.class);

    static {
        StringBuffer s = new StringBuffer(1024);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 127; j++) {
                s.append(i);
            }
            s.append('\n');
        }
        try {
            ONE_KB = s.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException wontHappen) {
            throw new Error(wontHappen);
        }
    }

    @Test
    public void twentyMBUploadWithoutLength() throws Exception {
        uploadTest("twentyMBUploadWithoutLength", 20 * 1024, false);
    }

    @Test
    public void twentyMBUploadWithLength() throws Exception {
        uploadTest("twentyMBUploadWithLength", 20 * 1024, true);
    }

    private void uploadTest(String name, int kbytes, boolean withLength) throws Exception {
        logger.info("Starting uploadTest " + name);
        long bytes = 1024 * kbytes;
        Map<String, String> hints = new HashMap<String, String>();
        hints.put(DuraCloudBlob.CONTENT_TYPE, "text/plain");
        if (withLength) {
            hints.put(DuraCloudBlob.CONTENT_LENGTH, "" + bytes);
        }
        Blob blob = getBlob(name, hints);
        OutputStream out = blob.openOutputStream(-1, false);
        try {
            for (int i = 0; i < kbytes; i++) {
                out.write(ONE_KB);
            }
            logger.info(bytes + " bytes written; closing");
            out.close();
            assertEquals(blob.getSize(), bytes);
            logger.info("Successfully completed uploadTest " + name);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                logger.warn("Error closing output stream", e);
            }
            blob.delete();
        }
    }

}
