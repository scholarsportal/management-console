
package org.duraspace.common.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.common.util.ChecksumUtil.Algorithm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ChecksumUtilTest {

    private String content;

    private InputStream stream;

    @Before
    public void setUp() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8000; ++i) {
            sb.append("1234567890123456");
            sb.append("abcdefghijklmnop");
        }
        content = sb.toString();
    }

    @After
    public void tearDown() throws Exception {
        content = null;
        if (stream != null) {
            stream.close();
            stream = null;
        }
    }

    @Test
    public void testGenerateChecksum() {
        ChecksumUtil util;

        util = new ChecksumUtil(Algorithm.MD2);
        String md2 = util.generateChecksum(getStream(content));

        util = new ChecksumUtil(Algorithm.MD5);
        String md5 = util.generateChecksum(getStream(content));

        util = new ChecksumUtil(Algorithm.SHA_1);
        String sha1 = util.generateChecksum(getStream(content));

        util = new ChecksumUtil(Algorithm.SHA_256);
        String sha256 = util.generateChecksum(getStream(content));

        util = new ChecksumUtil(Algorithm.SHA_384);
        String sha384 = util.generateChecksum(getStream(content));

        util = new ChecksumUtil(Algorithm.SHA_512);
        String sha512 = util.generateChecksum(getStream(content));

        assertNotNull(md2);
        assertNotNull(md5);
        assertNotNull(sha1);
        assertNotNull(sha256);
        assertNotNull(sha384);
        assertNotNull(sha512);

        assertEquals(md2.length(), 14);
        assertEquals(md5.length(), 14);
        assertEquals(sha1.length(), 19);
        assertEquals(sha256.length(), 30);
        assertEquals(sha384.length(), 46);
        assertEquals(sha512.length(), 61);

        boolean diff0 =
                (md2 != md5 && md2 != sha1 && md2 != sha256 && md2 != sha384 && md2 != sha512);

        boolean diff1 =
                (md5 != sha1 && md5 != sha256 && md5 != sha384 && md5 != sha256);

        boolean diff2 = (sha1 != sha256 && sha1 != sha384 && sha1 != sha512);

        boolean diff3 = (sha256 != sha384 && sha256 != sha512);

        boolean diff4 = (sha384 != sha512);

        assertTrue(diff0);
        assertTrue(diff1);
        assertTrue(diff2);
        assertTrue(diff3);
        assertTrue(diff4);

    }

    private InputStream getStream(String data) {
        stream = new ByteArrayInputStream(data.getBytes());
        return stream;
    }

}
