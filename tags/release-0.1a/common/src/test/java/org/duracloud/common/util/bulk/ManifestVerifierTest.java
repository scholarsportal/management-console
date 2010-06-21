package org.duracloud.common.util.bulk;

import org.duracloud.common.util.error.ManifestVerifyException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: Oct 24, 2009
 */
public class ManifestVerifierTest {

    private ManifestVerifier verifier;

    private final String baseDir = "src/test/resources/";
    private String file0 = baseDir + "manifest-md5.txt";
    private String file1 = baseDir + "manifest-md5-sameContent.txt";
    private String file2 = baseDir + "manifest-md5-lessContent.txt";
    private String file3 = baseDir + "manifest-md5-mismatchCksum.txt";
    private String file4 = baseDir + "manifest-md5-mismatchName.txt";
    private String file5 = baseDir + "manifest-md5-lessContentMismatch.txt";
    private String file6 = baseDir + "log4j.properties";

    @Test
    public void testVerifyGood() {
        verifier = new ManifestVerifier(file0, file1);
        try {
            verifier.verify();
        } catch (Exception e) {
            fail("No exception should be thrown.");
        }
    }

    @Test
    public void testVerifyUneven() {
        verifier = new ManifestVerifier(file0, file2);
        try {
            verifier.verify();
            fail("Exception expected.");
        } catch (ManifestVerifyException e) {
            assertEquals(ManifestVerifyException.ErrorType.UNEQUAL_NUM_ENTRIES,
                         e.getErrorType());
        }
    }

    @Test
    public void testVerifyUnevenAgain() {
        verifier = new ManifestVerifier(file2, file0);
        try {
            verifier.verify();
            fail("Exception expected.");
        } catch (ManifestVerifyException e) {
            assertEquals(ManifestVerifyException.ErrorType.UNEQUAL_NUM_ENTRIES,
                         e.getErrorType());
        }
    }

    @Test
    public void testVerifyMismatchCksum() {
        verifier = new ManifestVerifier(file0, file3);
        try {
            verifier.verify();
            fail("Exception expected.");
        } catch (ManifestVerifyException e) {
            assertEquals(ManifestVerifyException.ErrorType.CHKSUM_MISMATCH,
                         e.getErrorType());
        }
    }

    @Test
    public void testVerifyMismatchName() {
        verifier = new ManifestVerifier(file0, file4);
        try {
            verifier.verify();
            fail("Exception expected.");
        } catch (ManifestVerifyException e) {
            assertEquals(ManifestVerifyException.ErrorType.CHKSUM_MISMATCH,
                         e.getErrorType());
        }
    }

    @Test
    public void testVerifyUnevenBad() {
        verifier = new ManifestVerifier(file0, file5);
        try {
            verifier.verify();
            fail("Exception expected.");
        } catch (ManifestVerifyException e) {
            assertEquals(ManifestVerifyException.ErrorType.UNEQUAL_NUM_ENTRIES,
                         e.getErrorType());
        }
    }

    @Test
    public void testVerifyBad() {
        boolean runtimeThrown = false;
        verifier = new ManifestVerifier(file0, file6);
        try {
            verifier.verify();
            fail("Exception expected.");
        } catch (ManifestVerifyException e) {
            fail("ManifestVerifyException not expected.");
        } catch (RuntimeException re) {
            runtimeThrown = true;
        }

        assertTrue(runtimeThrown);

    }
}
