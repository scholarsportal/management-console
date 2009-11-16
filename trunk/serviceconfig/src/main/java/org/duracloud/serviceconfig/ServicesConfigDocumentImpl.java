package org.duracloud.serviceconfig;

import java.io.InputStream;
import java.util.List;

/**
 * @see org.duracloud.serviceconfig.ServicesConfigDocument
 */
public class ServicesConfigDocumentImpl implements ServicesConfigDocument {

    private String version;
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