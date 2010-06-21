package org.duracloud.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

/**
 * Provides utility methods for I/O.
 *
 * @author Bill Branan
 */
public class IOUtil {

    public static String readStringFromStream(InputStream stream)
    throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(stream, writer, "UTF-8");
        stream.close();
        return writer.toString();
    }

    public static InputStream writeStringToStream(String string)
    throws IOException {
        return IOUtils.toInputStream(string, "UTF-8");
    }

}
