package org.duracloud.aitsync.util;

import java.io.InputStream;

import org.duracloud.aitsync.domain.Configuration;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 
 * @author Daniel Bernstein
 * @created 12/17/2012
 *
 */
public class ConfigurationMarshaller {

    public static Configuration unmarshall(InputStream is){
        XStream xs = createMarshaller();
        return (Configuration) xs.fromXML(is);
    }

    public static Configuration unmarshall(String xmlString){
        XStream xs = createMarshaller();
        return (Configuration) xs.fromXML(xmlString);
    }

    public static String marshall(Configuration config){
        XStream xs = createMarshaller();
        return xs.toXML(config);
    }

    protected static XStream createMarshaller() {
        XStream xs = new XStream(new DomDriver());
        xs.alias("configuration", Configuration.class);
        return xs;
    }
}
