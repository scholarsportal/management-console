package org.duracloud.aitsync.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;

/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
public class IOUtils {
    private static Logger log = LoggerFactory.getLogger(IOUtils.class);

    public static Object fromXML(File file)
        throws IOException,
            FileNotFoundException {
        XStream xstream = new XStream();
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            return xstream.fromXML(is);
        } catch (FileNotFoundException e) {
            log.error("file not found", e);
            throw e;
        } catch (XStreamException e) {
            log.error(e.getMessage(), e);
            throw new IOException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void toXML(File file, Object object) {
        XStream xstream = new XStream();
        Writer writer = null;

        try {
            writer = new OutputStreamWriter(new FileOutputStream(file));
            xstream.toXML(object, writer);
        } catch (IOException e) {
            log.error("file not found", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
