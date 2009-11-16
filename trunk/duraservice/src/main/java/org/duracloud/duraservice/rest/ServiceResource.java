package org.duracloud.duraservice.rest;

import org.duracloud.duraservice.domain.ServiceManager;
import org.duracloud.duraservice.error.NoSuchDeployedServiceException;
import org.duracloud.duraservice.error.NoSuchServiceComputeInstanceException;
import org.duracloud.duraservice.error.NoSuchServiceException;
import org.duracloud.serviceconfig.ServiceInfo;
import org.duracloud.serviceconfig.ServicesConfigDocument;
import org.duracloud.serviceconfig.ServicesConfigDocumentImpl;

import java.io.InputStream;
import java.util.List;

/**
 * Provides interaction with content
 *
 * @author Bill Branan
 */
public class ServiceResource {

    private static ServiceManager serviceManager;

    public static void configureManager(InputStream configXml) {
        serviceManager.configure(configXml);
    }

    public static String getDeployedServices() {
        List<ServiceInfo> deployedServices = serviceManager.getDeployedServices();
        ServicesConfigDocument configDoc = new ServicesConfigDocumentImpl();
        return configDoc.getServiceListAsXML(deployedServices);        
    }

    public static String getAvailableServices() {
        List<ServiceInfo> availableServices = serviceManager.getAvailableServices();
        ServicesConfigDocument configDoc = new ServicesConfigDocumentImpl();
        return configDoc.getServiceListAsXML(availableServices);
    }

    public static String getService(String serviceId)
        throws NoSuchServiceException {
        ServiceInfo service = serviceManager.getService(serviceId);
        ServicesConfigDocument configDoc = new ServicesConfigDocumentImpl();
        return configDoc.getServiceAsXML(service);
    }

    public static String getDeployedService(String serviceId, int deploymentId)
        throws NoSuchDeployedServiceException {
        ServiceInfo service =
            serviceManager.getDeployedService(serviceId, deploymentId);
        ServicesConfigDocument configDoc = new ServicesConfigDocumentImpl();
        return configDoc.getServiceAsXML(service);
    }

    public static void deployService(String serviceId,
                                     String serviceHost,
                                     InputStream serviceXml)
        throws NoSuchServiceException, NoSuchServiceComputeInstanceException {
        ServicesConfigDocument configDoc = new ServicesConfigDocumentImpl();
        ServiceInfo service = configDoc.getService(serviceXml);
        serviceManager.deployService(serviceId,
                                     serviceHost,
                                     service.getUserConfigVersion(),
                                     service.getUserConfigs());
    }

    public static void updateServiceConfig(String serviceId,
                                            int deploymentId,
                                            InputStream serviceXml)
        throws NoSuchDeployedServiceException {
        ServicesConfigDocument configDoc = new ServicesConfigDocumentImpl();
        ServiceInfo service = configDoc.getService(serviceXml);
        serviceManager.updateServiceConfig(serviceId,
                                           deploymentId,
                                           service.getUserConfigs());
    }

    public static void undeployService(String serviceId, int deploymentId)
        throws NoSuchDeployedServiceException {
        serviceManager.undeployService(serviceId, deploymentId);
    }

    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    public void setServiceManager(ServiceManager manager) {
        serviceManager = manager;
    }

}
