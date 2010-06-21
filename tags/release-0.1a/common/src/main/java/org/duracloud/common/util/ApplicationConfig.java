
package org.duracloud.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.apache.commons.io.input.AutoCloseInputStream;

import org.apache.log4j.Logger;

/**
 * @author Andrew Woods
 */
public class ApplicationConfig {

    protected static final Logger log =
            Logger.getLogger(ApplicationConfig.class);

    protected static Properties getPropsFromResource(String resourceName)
            throws Exception {
        Properties props = new Properties();
        AutoCloseInputStream in =
                new AutoCloseInputStream(ApplicationConfig.class
                        .getClassLoader().getResourceAsStream(resourceName));
        try {
            props.load(in);
        } catch (Exception e) {
            log.warn("Unable to find resource: '" + resourceName + "': "
                    + e.getMessage());
            throw e;
        }
        return props;
    }

    public static Properties getPropsFromXml(String propsXml) throws Exception {
        AutoCloseInputStream in =
                new AutoCloseInputStream(new ByteArrayInputStream(propsXml
                        .getBytes()));

        return getPropsFromXmlStream(in);
    }

    public static Properties getPropsFromXmlStream(InputStream propsXmlStream)
            throws Exception {
        Properties props = new Properties();
        try {
            props.loadFromXML(propsXmlStream);
        } catch (InvalidPropertiesFormatException e) {
            log.error(e.getMessage());
            log.error(ExceptionUtil.getStackTraceAsString(e));
            throw e;
        } catch (IOException e) {
            log.error(e.getMessage());
            log.error(ExceptionUtil.getStackTraceAsString(e));
            throw e;
        } finally {
            if (propsXmlStream != null) {
                propsXmlStream.close();
            }
        }

        return props;
    }

    public static Properties getPropsFromXmlResource(String resourceName)
            throws Exception {
        Properties props = new Properties();
        AutoCloseInputStream in =
                new AutoCloseInputStream(ApplicationConfig.class
                        .getClassLoader().getResourceAsStream(resourceName));
        try {
            props.loadFromXML(in);
        } catch (Exception e) {
            log.warn("Unable to find resource: '" + resourceName + "': "
                    + e.getMessage());
            throw e;
        }
        return props;
    }

    public static String getXmlFromProps(Properties props) throws Exception {
        String comment = null;
        String xml = new String();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            props.storeToXML(os, comment);
            os.flush();
            xml = os.toString();
        } catch (IOException e) {
            log.error("IO exception for props: '" + props + "'");
            log.error(e.getMessage());
            log.error(ExceptionUtil.getStackTraceAsString(e));
            throw e;
        } finally {
            if (os != null) {
                os.close();
            }
        }

        return xml;
    }

}
