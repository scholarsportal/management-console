
package org.duracloud.duradmin.util;

import org.duracloud.client.ServicesManager;
import org.duracloud.client.error.NotFoundException;
import org.duracloud.client.error.ServicesException;
import org.duracloud.serviceconfig.ServiceInfo;

import java.util.List;

/**
 * Utilities for handling services
 * 
 * @author Bill Branan
 */
public class ServicesUtil {

    public static List<ServiceInfo> getDeployedServices(ServicesManager servicesManager)
            throws ServicesException {
        return servicesManager.getDeployedServices();
    }

    public static List<ServiceInfo> getAvailableServices(ServicesManager servicesManager)
            throws ServicesException {
        return servicesManager.getAvailableServices();
    }

    public static ServiceInfo getService(ServicesManager servicesManager, int serviceId)
        throws NotFoundException, ServicesException {
        return servicesManager.getService(serviceId);
    }
}
