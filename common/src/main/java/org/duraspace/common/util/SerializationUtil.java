package org.duraspace.common.util;

import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Provides utility methods for serializing and deserializing.
 *
 * @author Bill Branan
 */
public class SerializationUtil {

    public static String serializeMap(Map<String, String> map) {
        XStream xstream = new XStream(new DomDriver());
        return xstream.toXML(map);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> deserializeMap(String map) {
        XStream xstream = new XStream(new DomDriver());
        return (Map<String, String>)xstream.fromXML(map);
    }
}
