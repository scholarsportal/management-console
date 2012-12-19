package org.duracloud.aitsync.util;

import java.io.InputStream;

import org.duracloud.aitsync.domain.ArchiveItConfig;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 
 * @author Daniel Bernstein
 * @created 12/17/2012
 *
 */
public class ArchiveItConfigMarshaller {

    public static ArchiveItConfig unmarshall(InputStream is){
        XStream xs = createMarshaller();
        return (ArchiveItConfig) xs.fromXML(is);
    }

    public static ArchiveItConfig unmarshall(String xmlString){
        XStream xs = createMarshaller();
        return (ArchiveItConfig) xs.fromXML(xmlString);
    }

    public static String marshall(ArchiveItConfig config){
        XStream xs = createMarshaller();
        return xs.toXML(config);
    }

    protected static XStream createMarshaller() {
        XStream xs = new XStream(new DomDriver());
        xs.alias("archiveItConfig", ArchiveItConfig.class);
        return xs;
    }
}
