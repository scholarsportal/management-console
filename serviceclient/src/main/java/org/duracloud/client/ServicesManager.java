package org.duracloud.client;

import java.util.List;
import java.util.Map;

import org.duracloud.common.util.SerializationUtil;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.serviceconfig.ServiceInfo;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

/**
 * Allows communication with a services manager
 *
 * @author Bill Branan
 */
public class ServicesManager {

    private static enum ServicesList {
        ALL ("all"),
        DEPLOYED ("deployed"),
        AVAILABLE ("available");

        public String type;

        ServicesList(String type) {
            this.type = type;
        }
    }

    private static final String DEFAULT_CONTEXT = "duraservice";

    public static final String SERVICE_STATUS = "service.status";

    private String baseURL = null;

    private static RestHttpHelper restHelper = new RestHttpHelper();

    public ServicesManager(String host, String port) {
        this(host, port, DEFAULT_CONTEXT);
    }

    public ServicesManager(String host, String port, String context) {
        if (host == null || host.equals("")) {
            throw new IllegalArgumentException("Host must be a valid server host name");
        }

        if (context == null) {
            context = DEFAULT_CONTEXT;
        }

        if (port == null || port.equals("")) {
            baseURL = "http://" + host + "/" + context;
        } else {
            baseURL = "http://" + host + ":" + port + "/" + context;
        }
    }

    public String getBaseURL() {
        return baseURL;
    }

    private String buildURL(String relativeURL) {
        String url = baseURL + relativeURL;
        return url;
    }

    private String buildGetServicesURL(ServicesList servicesList) {
        if(servicesList == null) {
            servicesList = ServicesList.ALL;
        }
        return buildURL("/services?show=" + servicesList.type);
    }

    private String buildServiceURL(String serviceId) {
        return buildURL("/services/" + serviceId);
    }

    private String buildServiceHostsURL() {
        return buildURL("/servicehosts");
    }

    /**
     * Provides a listing of all services.
     *
     * @return List of all services
     * @throws ServicesException
     */
    public List<String> getAllServices() throws ServicesException {
        return getServices(buildGetServicesURL(ServicesList.ALL));
    }

    

    /**
     * Provides a listing of available services, that is, services which
     * can be deployed but have not yet been deployed.
     *
     * @return List of available services
     * @throws ServicesException
     */
    public List<String> getAvailableServices() throws ServicesException {
        return getServices(buildGetServicesURL(ServicesList.AVAILABLE));
    }

    /**
     * Provides a listing of all deployed services.
     *
     * @return List of deployed services
     * @throws ServicesException
     */
    public List<String> getDeployedServices() throws ServicesException {
        return getServices(buildGetServicesURL(ServicesList.DEPLOYED));
    }

    private List<String> getServices(String url) throws ServicesException {
        try {
            HttpResponse response = restHelper.get(url);
            checkResponse(response, 200);
            String responseText = response.getResponseBody();
            if (responseText != null) {
                List<String> servicesList =
                    SerializationUtil.deserializeList(responseText);
                return servicesList;
            } else {
                throw new ServicesException("Response body is empty");
            }
        } catch (Exception e) {
            throw new ServicesException("Could not get spaces due to: " +
                                            e.getMessage(), e);
        }
    }

    /**
     * Gets the status of a service.
     *
     * @throws ServicesException
     */
    public String getServiceStatus(String serviceId) throws ServicesException {
        String url = buildServiceURL(serviceId);
        try {
            HttpResponse response = restHelper.get(url);
            checkResponse(response, 200);
            String responseText = response.getResponseBody();
            if (responseText != null) {
                Map<String, String> serviceConfig =
                    SerializationUtil.deserializeMap(responseText);
                if(serviceConfig.containsKey(SERVICE_STATUS)) {
                    return serviceConfig.get(SERVICE_STATUS);
                } else {
                    throw new ServicesException("No status available");
                }
            } else {
                throw new ServicesException("Response body is empty");
            }
        } catch (Exception e) {
            throw new ServicesException("Could not get service status for " +
                                        serviceId + " due to: " + e.getMessage(), e);
        }
    }

    /**
     * Gets the configuration of a deployed service.
     *
     * @throws ServicesException
     */
    public Map<String, String> getServiceConfig(String serviceId) throws ServicesException {
        String url = buildServiceURL(serviceId);
        try {
            HttpResponse response = restHelper.get(url);
            checkResponse(response, 200);
            String responseText = response.getResponseBody();
            if (responseText != null) {
                Map<String, String> serviceConfig =
                    SerializationUtil.deserializeMap(responseText);
                serviceConfig.remove(SERVICE_STATUS);
                return serviceConfig;
            } else {
                throw new ServicesException("Response body is empty");
            }
        } catch (Exception e) {
            throw new ServicesException("Could not get service config for " +
                                        serviceId + " due to: " + e.getMessage(), e);
        }
    }

    /**
     * Deploys a service.
     *
     * @throws ServicesException
     */
    public void deployService(String serviceId, String serviceHost) throws ServicesException {
        String url = buildServiceURL(serviceId);
        if(serviceHost != null) {
            url += ("?serviceHost=" + serviceHost);
        }

        try {
            HttpResponse response = restHelper.put(url, null, null);
            checkResponse(response, 201);
        } catch (Exception e) {
            throw new ServicesException("Could not deploy service " + serviceId +
                                        " to host " + serviceHost +
                                        " due to: " + e.getMessage(), e);
        }
    }

    /**
     * Allows for configuring a deployed service.
     *
     * @throws ServicesException
     */
    public void configureService(String serviceId,
                                 Map<String, String> configProperties)
    throws ServicesException {
        String url = buildServiceURL(serviceId);
        String configXml = buildConfigXml(configProperties);
        try {
            HttpResponse response = restHelper.post(url, configXml, null);
            checkResponse(response, 200);
        } catch (Exception e) {
            throw new ServicesException("Could not undeploy service " + serviceId +
                                        " due to: " + e.getMessage(), e);
        }
    }

    private String buildConfigXml(Map<String, String> configProperties) {
        Element serviceConfig = new Element("serviceConfig");
        for(String propertyName : configProperties.keySet()) {
            Element configItem = new Element("configItem");
            configItem.addContent(new Element("name").setText(propertyName));
            String propertyValue = configProperties.get(propertyName);
            configItem.addContent(new Element("value").setText(propertyValue));
            serviceConfig.addContent(configItem);
        }
        Document xmlDoc = new Document(serviceConfig);
        XMLOutputter outputter = new XMLOutputter();
        return outputter.outputString(xmlDoc);
    }

    /**
     * UnDeploys a service.
     *
     * @throws ServicesException
     */
    public void undeployService(String serviceId) throws ServicesException {
        String url = buildServiceURL(serviceId);
        try {
            HttpResponse response = restHelper.delete(url);
            checkResponse(response, 200);
        } catch (Exception e) {
            throw new ServicesException("Could not undeploy service " + serviceId +
                                        " due to: " + e.getMessage(), e);
        }
    }

    /**
     * Gets a list of service hosts. This is all of the compute instances
     * which have been deployed to manage service activities.
     *
     * @throws ServicesException
     */
    public List<String> getServiceHosts() throws ServicesException {
        String url = buildServiceHostsURL();
        try {
            HttpResponse response = restHelper.get(url);
            checkResponse(response, 200);
            String responseText = response.getResponseBody();
            if (responseText != null) {
                List<String> serviceHosts =
                    SerializationUtil.deserializeList(responseText);
                return serviceHosts;
            } else {
                throw new ServicesException("Response body is empty");
            }
        } catch (Exception e) {
            throw new ServicesException("Could not get service hosts" +
                                        " due to: " + e.getMessage(), e);
        }
    }

    private void checkResponse(HttpResponse response, int expectedCode)
            throws ServicesException {
        String error = "Could not complete request due to error: ";
        if (response == null) {
            throw new ServicesException(error + "Response content was null.");
        }
        if (response.getStatusCode() != expectedCode) {
            throw new ServicesException(error + "Response code was " +
                                            response.getStatusCode() +
                                            ", expected value was " +
                                            expectedCode);
        }
    }
    
 

}
