
package org.duracloud.duradmin.util;

import org.duracloud.client.ServicesException;
import org.duracloud.client.ServicesManager;
import org.duracloud.serviceconfig.user.Option;
import org.duracloud.serviceconfig.ServiceInfo;
import org.duracloud.serviceconfig.SystemConfig;
import org.duracloud.serviceconfig.user.MultiSelectUserConfig;
import org.duracloud.serviceconfig.user.SingleSelectUserConfig;
import org.duracloud.serviceconfig.user.TextUserConfig;
import org.duracloud.serviceconfig.user.UserConfig;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Utilities for handling services
 * 
 * @author Bill Branan
 */
public class ServicesUtil {

    public static List<ServiceInfo> getDeployedServices(ServicesManager servicesManager)
            throws ServicesException {
        List<String> deployedServices = servicesManager.getDeployedServices();
        List<ServiceInfo> depServiceList = new ArrayList<ServiceInfo>();

        for (String serviceId : deployedServices) {
            depServiceList.add(initializeService(servicesManager, serviceId));
        }

        return depServiceList;
    }

    public static ServiceInfo initializeService(ServicesManager servicesManager,
                                                String serviceId)
            throws ServicesException {
        List<SystemConfig> systemConfigs = new LinkedList<SystemConfig>();
        systemConfigs.add(new SystemConfig("host", "localhost", "localhost"));
        systemConfigs.add(new SystemConfig("port", "8080", "8080"));

        List<UserConfig> userConfigs = new LinkedList<UserConfig>();

        List<Option> stores = new LinkedList<Option>();
        stores.add(new Option("Amazon", "1", false));
        stores.add(new Option("EMC", "2", false));
        stores.add(new Option("Rackspace", "3", false));

        userConfigs.add(new SingleSelectUserConfig("fromStoreId",
                                                   "The source store",
                                                   true,
                                                   stores));
        userConfigs.add(new SingleSelectUserConfig("toStoreId",
                                                   "The destination store",
                                                   true,
                                                   stores));

        List<Option> spaces = new LinkedList<Option>();
        spaces.add(new Option("Space 1", "1", false));
        spaces.add(new Option("Space 2", "2", false));
        spaces.add(new Option("Space 3", "3", false));
        spaces.add(new Option("Space 4", "4", false));
        userConfigs.add(new MultiSelectUserConfig("spaces",
                                                  "Spaces",
                                                  true,
                                                  spaces));
        userConfigs.add(new TextUserConfig("mimetypes",
                                           "Mime Types",
                                           true,
                                           null));

        ServiceInfo service = new ServiceInfo();
        service.setId(Integer.parseInt(serviceId)); // FIXME: if this is indeed an int, it should be guaranteed at a higher level.
        service.setDisplayName("Replicaton Service");
        service
                .setDescription("A description of the replication service goes here.  We should provide enough space for multiple lines of text.");
        service.setSystemConfigs(systemConfigs);
        service.setUserConfigs(userConfigs);
        return service;
    }

    public static List<ServiceInfo> getAvailableServices(ServicesManager servicesManager)
            throws ServicesException {
        List<String> availableServices = servicesManager.getAvailableServices();
        List<ServiceInfo> avlServiceList = new ArrayList<ServiceInfo>();

        for (String serviceId : availableServices) {
            ServiceInfo service = initializeService(servicesManager, serviceId);
            avlServiceList.add(service);
        }

        return avlServiceList;
    }

    public static List<String> getServiceHosts(ServicesManager servicesManager)
            throws ServicesException {
        return servicesManager.getServiceHosts();
    }
}
