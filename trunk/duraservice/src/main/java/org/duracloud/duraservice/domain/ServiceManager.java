package org.duracloud.duraservice.domain;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.util.HttpURLConnection;

import org.apache.log4j.Logger;

import org.duracloud.client.ContentStore;
import org.duracloud.client.ContentStoreException;
import org.duracloud.client.ContentStoreManager;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.computeprovider.domain.ComputeProviderType;
import org.duracloud.domain.Space;
import org.duracloud.duraservice.config.DuraServiceConfig;
import org.duracloud.servicesadminclient.ServicesAdminClient;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Performs management functions over services.
 *
 * @author Bill Branan
 */
public class ServiceManager {

    private static final Logger log = Logger.getLogger(ServiceManager.class);

    private static final String LOCAL_HOST = "localhost";

    private String localServicesAdminBaseURL;

    // List of all service IDs
    private List<String> services = null;

    // Maps serviceIds to deployment location (instance host names)
    private Map<String, String> deployedServices = null;

    // Provides access to service packages
    private ContentStore store = null;

    // ServiceStore in which services packages reside
    private ServiceStore serviceStore = null;

    // ServiceCompute used to run service compute instances
    private ServiceCompute serviceCompute = null;

    // Maps servicesAdmin instance host names to client
    private Map<String, ServicesAdminClient> servicesAdmins = null;

    public static final String SERVICE_STATUS = "service.status";
    public static final String SERVICE_HOST = "service.host";

    public static enum ServiceStatus {
        AVAILABLE ("available"),
        DEPLOYED ("deployed"),
        UNKNOWN_SERVICE ("not-available");

        public String status;

        ServiceStatus(String status) {
            this.status = status;
        }
    }

    public ServiceManager() {
        deployedServices = new HashMap<String, String>();
        servicesAdmins = new HashMap<String, ServicesAdminClient>();
    }

    public void configure(InputStream configXml) {
        parseManagerConfigXml(configXml);

        try {
            initializeServicesList(serviceStore);
        } catch (ContentStoreException cse) {
            String error = "Could not build services list due " +
            		       "to exception: " + cse.getMessage();
            log.error(error);
            throw new RuntimeException(error, cse);
        }

        try {
            localServicesAdminBaseURL = DuraServiceConfig.getServicesAdminUrl();
            addServicesAdmin(LOCAL_HOST);
        } catch (Exception e) {
            String error = "Could not retrieve local servicesAdmin " +
            		       "URL due to error: " + e.getMessage();
            log.error(error);
            throw new RuntimeException(error, e);
        }
    }

    private void checkConfigured() {
        if(serviceStore == null) {
            throw new RuntimeException("The Service Manager must be initialized " +
            		                   "prior to performing any other activities.");
        }
    }

    private void parseManagerConfigXml(InputStream xml) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(xml);
            Element servicesConfig = doc.getRootElement();

            Element serviceStorage = servicesConfig.getChild("serviceStorage");
            serviceStore = new ServiceStore();
            serviceStore.setHost(serviceStorage.getChildText("host"));
            serviceStore.setPort(serviceStorage.getChildText("port"));
            serviceStore.setContext(serviceStorage.getChildText("context"));
            serviceStore.setSpaceId(serviceStorage.getChildText("spaceId"));

            Element serviceComputeProvider = servicesConfig.getChild("serviceCompute");
            serviceCompute = new ServiceCompute();
            String computeProviderType = serviceComputeProvider.getChildText("type");
            serviceCompute.setType(ComputeProviderType.fromString(computeProviderType));
            serviceCompute.setImageId(serviceComputeProvider.getChildText("imageId"));
            Element computeCredential =
                serviceComputeProvider.getChild("computeProviderCredential");
            serviceCompute.setUsername(computeCredential.getChildText("username"));
            serviceCompute.setPassword(computeCredential.getChildText("password"));
        } catch (Exception e) {
            String error = "Error encountered attempting to parse DuraService " +
            		       "configuration xml " + e.getMessage();
            log.error(error);
            throw new RuntimeException(error, e);
        }
    }

    /**
     * Reviews the list of content available in the DuraCloud
     * service storage location and builds a service registry.
     */
    protected void initializeServicesList(ServiceStore serviceStore)
    throws ContentStoreException {
        ContentStoreManager storeManager =
            new ContentStoreManager(serviceStore.getHost(),
                                    serviceStore.getPort(),
                                    serviceStore.getContext());
        setContentStore(storeManager.getPrimaryContentStore());

        Space space = store.getSpace(serviceStore.getSpaceId());
        setServicesList(space.getContentIds());
    }

    protected void setContentStore(ContentStore store) {
        this.store = store;
    }

    protected void setServicesList(List<String> services) {
        this.services = services;
    }

    public List<String> getAllServices() {
        checkConfigured();
        return services;
    }

    public List<String> getDeployedServices() {
        checkConfigured();
        Set<String> serviceIds = deployedServices.keySet();
        List<String> deployedServiceIds = new ArrayList<String>();
        for(String serviceId : serviceIds) {
            deployedServiceIds.add(serviceId);
        }
        return deployedServiceIds;
    }

    public void deployService(String serviceId, String serviceHost)
    throws ServiceException {
        checkConfigured();

        if(serviceHost == null || serviceHost.equals("")) {
            serviceHost = LOCAL_HOST;
        }

        if(!services.contains(serviceId)) {
            throw new ServiceException("Cannot deploy service " + serviceId +
                                       ". No service with that ID is " +
                                       "available for deployment.");
        }

        log.info("Deploying service " + serviceId + " to " + serviceHost);

        try {
            // Grab file from store
            InputStream serviceStream =
                store.getContent(serviceStore.getSpaceId(), serviceId).getStream();

            // Push file to services admin
            ServicesAdminClient servicesAdmin = getServicesAdmin(serviceHost);
            HttpResponse response =
                servicesAdmin.postServiceBundle(serviceId, serviceStream);
            if(response.getStatusCode() != HttpURLConnection.HTTP_OK) {
                throw new ServiceException("Services Admin response code was " +
                                           response.getStatusCode());
            }
        } catch(Exception e) {
            String error = "Unable to deploy service " + serviceId +
                           " to " + serviceHost + " due to error: " +
                           e.getMessage();
            log.error(error);
            throw new ServiceException(error, e);
        }

        deployedServices.put(serviceId, serviceHost);
    }

    public void configureService(String serviceId, InputStream configXml)
    throws ServiceException {
        checkConfigured();
        String serviceHost = deployedServices.get(serviceId);
        if(serviceHost != null) {
            log.info("Configuring service: " + serviceId + " from " + serviceHost);

            Map<String, String> config;
            if(configXml != null) {
                config = parseServiceConfigXml(configXml);
                config.remove(SERVICE_STATUS);
            } else {
                String error = "Cannot configure service: " + serviceId +
                               ". The config XML is null.";
                log.error(error);
                throw new ServiceException(error);
            }

            try {
                ServicesAdminClient servicesAdmin = getServicesAdmin(serviceHost);
                servicesAdmin.postServiceConfig(serviceId, config);
            } catch (Exception e) {
                String error = "Unable to configure service " + serviceId +
                               " at " + serviceHost + " due to error: " +
                               e.getMessage();
                log.error(error);
                throw new ServiceException(error, e);
            }
        } else {
            String error = "Cannot configure service: " + serviceId +
                           ". It has not been deployed.";
            log.error(error);
            throw new ServiceException(error);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> parseServiceConfigXml(InputStream xml) {
        Map<String, String> config = new HashMap<String, String>();

        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(xml);
            Element serviceConfig = doc.getRootElement();

            List<Element> configItems =
                serviceConfig.getChildren("configItem");
            for(Element configItem : configItems) {
                config.put(configItem.getChildText("name"),
                           configItem.getChildText("value"));
            }
        } catch (Exception e) {
            String error = "Error encountered attempting to parse service " +
                           "configuration xml " + e.getMessage();
            log.error(error);
            throw new RuntimeException(error, e);
        }

        return config;
    }

    public Map<String, String> getService(String serviceId)
    throws ServiceException {
        checkConfigured();
        String serviceHost = deployedServices.get(serviceId);
        if(serviceHost != null) {
            log.info("Getting service: " + serviceId + " from " + serviceHost);

            try {
                ServicesAdminClient servicesAdmin = getServicesAdmin(serviceHost);
                Map<String, String> serviceConfig =
                    servicesAdmin.getServiceConfig(serviceId);
                serviceConfig.put(SERVICE_STATUS, ServiceStatus.DEPLOYED.status);
                serviceConfig.put(SERVICE_HOST, serviceHost);
                return serviceConfig;
            } catch (Exception e) {
                String error = "Unable to get config for service " + serviceId +
                " from " + serviceHost + " due to error: " + e.getMessage();
                log.error(error);
                throw new ServiceException(error, e);
            }
        } else {
            Map<String, String> serviceConfig = new HashMap<String, String>();
            if(services.contains(serviceId)) {
                serviceConfig.put(SERVICE_STATUS, ServiceStatus.AVAILABLE.status);
            } else {
                serviceConfig.put(SERVICE_STATUS, ServiceStatus.UNKNOWN_SERVICE.status);
            }
            return serviceConfig;
        }
    }

    public void undeployService(String serviceId)
    throws ServiceException {
        checkConfigured();
        String serviceHost = deployedServices.get(serviceId);
        if(serviceHost != null) {
            log.info("UnDeploying service: " + serviceId + " from " + serviceHost);

            try {
                ServicesAdminClient servicesAdmin = getServicesAdmin(serviceHost);
                HttpResponse response = servicesAdmin.deleteServiceBundle(serviceId);

                if(response.getStatusCode() != HttpURLConnection.HTTP_OK) {
                    throw new ServiceException("Services Admin response code was " +
                                               response.getStatusCode());
                }
            } catch (Exception e) {
                String error = "Unable to undeploy service " + serviceId +
                               " from " + serviceHost + " due to error: " +
                               e.getMessage();
                log.error(error);
                throw new ServiceException(error, e);
            }

            deployedServices.remove(serviceId);
        } else {
            String error = "Cannot undeploy service " + serviceId +
                           ". It has not been deployed.";
            log.error(error);
            throw new ServiceException(error);
        }
    }

    protected ServicesAdminClient getServicesAdmin(String instanceHost)
    throws ServiceException {
        if(instanceHost != null && instanceHost != "") {
            if(servicesAdmins.containsKey(instanceHost)) {
                return servicesAdmins.get(instanceHost);
            } else {
                throw new ServiceException("There is no Service Instance on host " +
                                           instanceHost + ".");
            }
        } else {
            return servicesAdmins.get(LOCAL_HOST);
        }
    }

    private void addServicesAdmin(String instanceHost) {
        String baseUrl =
            localServicesAdminBaseURL.replace(LOCAL_HOST, instanceHost);
        ServicesAdminClient servicesAdmin = new ServicesAdminClient();
        servicesAdmin.setBaseURL(baseUrl);
        servicesAdmin.setRester(new RestHttpHelper());
        servicesAdmins.put(instanceHost, servicesAdmin);
    }

    public List<String> getServiceHosts() {
        List<String> serviceHosts = new ArrayList<String>();
        for(String adminHost : servicesAdmins.keySet()) {
            serviceHosts.add(adminHost);
        }
        return serviceHosts;
    }

    /**
     * Starts up a new services compute instance.
     *
     * @return the hostName of the new instance
     */
    public String addServicesInstance() {
        checkConfigured();
        //TODO: Make call to start services instance through compute manager
        //addServicesAdmin(hostName);
        //return hostName;
        return null;
    }

}
