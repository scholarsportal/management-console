
package org.duraspace.common.util;

import java.io.IOException;
import java.io.InputStream;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

public class ChecksumUtil {

    private final Logger log = Logger.getLogger(ChecksumUtil.class);

    private final MessageDigest digest;

    public ChecksumUtil(Algorithm alg) {
        try {
            digest = MessageDigest.getInstance(alg.toString());
        } catch (NoSuchAlgorithmException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This method generates checksum of content in arg stream.
     * @param inStream Content used as target of checksum.
     * @return string representation of the generated checksum.
     */
    public String generateChecksum(InputStream inStream) {

        byte[] buf = new byte[4096];
        int numRead = 0;
        while ((numRead = readFromStream(inStream, buf)) != -1) {
            digest.update(buf, 0, numRead);
        }
        return new String(digest.digest());
    }

    private int readFromStream(InputStream inStream, byte[] buf) {
        int numRead = -1;
        try {
            numRead = inStream.read(buf);
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
        return numRead;
    }

    /**
     * This class encapsulates the valid values for checksum algorithms. *
     */
    public enum Algorithm {
        MD2("MD2"), MD5("MD5"), SHA_1("SHA-1"), SHA_256("SHA-256"), SHA_384(
                "SHA-384"), SHA_512("SHA-512");

        private final String text;

        private Algorithm(String input) {
            text = input;
        }

        public static Algorithm fromString(String input) {
            for (Algorithm alg : values()) {
                if (alg.text.equalsIgnoreCase(input)) {
                    return alg;
                }
            }
            return MD5;
        }

        @Override
        public String toString() {
            return text;
        }
    }

}
