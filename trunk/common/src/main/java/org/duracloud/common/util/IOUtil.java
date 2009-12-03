package org.duracloud.common.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;
import org.duracloud.common.error.DuraCloudRuntimeException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

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

    public static OutputStream getOutputStream(File file) {
        OutputStream output;
        try {
            output = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new DuraCloudRuntimeException(e);
        }
        return output;
    }

    public static void copy(InputStream input, OutputStream output) {
        try {
            IOUtils.copy(input, output);
        } catch (IOException e) {
            throw new DuraCloudRuntimeException(e);
        }
    }

    public static void copyFileToDirectory(File file, File dir) {
        try {
            FileUtils.copyFileToDirectory(file, dir);
        } catch (IOException e) {
            throw new DuraCloudRuntimeException(e);
        }
    }

}
