package org.duracloud.common.util;

import static junit.framework.Assert.assertEquals;
import org.junit.Test;

import java.io.File;

/**
 * @author: Bill Branan
 * Date: May 7, 2010
 */
public class MimetypeUtilTest {

    @Test
    public void testMimetypeUtil() {
        MimetypeUtil util = new MimetypeUtil();

        String mimetype = util.getMimeType("test.txt");
        assertEquals("text/plain", mimetype);

        mimetype = util.getMimeType("test.html");
        assertEquals("text/html", mimetype);

        mimetype = util.getMimeType(new File("test.zip"));
        assertEquals("application/zip", mimetype);

        mimetype = util.getMimeType(new File("test.000"));
        assertEquals("application/octet-stream", mimetype);

        String filename = null;
        mimetype = util.getMimeType(filename);
        assertEquals("application/octet-stream", mimetype);

        File file = null;
        mimetype = util.getMimeType(file);
        assertEquals("application/octet-stream", mimetype);
    }
}
