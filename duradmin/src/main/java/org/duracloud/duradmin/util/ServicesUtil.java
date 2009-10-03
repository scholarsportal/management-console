
package org.duracloud.duradmin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.duracloud.client.ServicesException;
import org.duracloud.client.ServicesManager;
import org.duracloud.duradmin.domain.Service;

/**
 * Utilities for handling services
 * 
 * @author Bill Branan
 */
public class ServicesUtil {

    public static List<Service> getDeployedServices(ServicesManager servicesManager)
            throws ServicesException {
        List<String> deployedServices = servicesManager.getDeployedServices();
        List<Service> depServiceList = new ArrayList<Service>();

        for (String serviceId : deployedServices) {
            Service service = new Service();
            service.setServiceId(serviceId);
            service.setConfig(servicesManager.getServiceConfig(serviceId));
            service.setStatus(servicesManager.getServiceStatus(serviceId));
            depServiceList.add(service);
        }

        return depServiceList;
    }

    public static List<Service> getAvailableServices(ServicesManager servicesManager)
            throws ServicesException {
        List<String> availableServices = servicesManager.getAvailableServices();
        List<Service> avlServiceList = new ArrayList<Service>();

        for (String serviceId : availableServices) {
            Service service = new Service();
            service.setServiceId(serviceId);
            // TODO: Determine where to get configuration options for available services
            service.setConfig(new HashMap<String, String>());
            avlServiceList.add(service);
        }

        return avlServiceList;
    }

    public static List<String> getServiceHosts(ServicesManager servicesManager)
            throws ServicesException {
        return servicesManager.getServiceHosts();
    }
}
