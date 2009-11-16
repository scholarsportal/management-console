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
public interface ServicesConfigDocument {

    public String getVersion();

    public List<ServiceInfo> getServiceList(InputStream xml);

    public ServiceInfo getService(InputStream xml);

    public String getServiceListAsXML(List<ServiceInfo> serviceList);

    public String getServiceAsXML(ServiceInfo service);

}
