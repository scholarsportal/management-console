package org.duracloud.serviceconfig;

import java.io.InputStream;
import java.util.List;

/**
 * ServicesConfigDocument is the top-level abstraction for the entire set of
 * sevice-config settings for all applicable services.
 * It provides the ability to serialize and deserialize these settings.
 *
 * @author Andrew Woods
 *         Date: Nov 6, 2009
 */
public class ServicesConfigDocument {

    private static final String version = "0.2";
    private List<ServiceInfo> serviceInfos;

    public String getVersion() {
        return version;
    }

    public List<ServiceInfo> getServiceList(InputStream xml) {
        return null;
    }

    public ServiceInfo getService(InputStream xml) {
        return null;
    }

    public String getServiceListAsXML(List<ServiceInfo> serviceList) {
        // TODO: Convert ServiceInfo list to XML
        return null;
    }

    public String getServiceAsXML(ServiceInfo service) {
        // TODO: Convert ServiceInfo to XML
        return null;
    }

}
