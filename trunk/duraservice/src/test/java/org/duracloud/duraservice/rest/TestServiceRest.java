package org.duracloud.duraservice.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duracloud.common.util.SerializationUtil;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.duraservice.config.DuraServiceConfig;
import org.duracloud.duraservice.domain.ServiceManager;
import org.duracloud.servicesutil.beans.ComputeServiceBean;
import org.duracloud.servicesutil.client.ServiceUploadClient;
import org.duracloud.servicesutil.util.ServiceSerializer;
import org.duracloud.servicesutil.util.XMLServiceSerializerImpl;

import junit.framework.TestCase;

/**
 * Runtime test of service REST API. The duraservice and durastore web
 * applications must be deployed and available in order for these tests
 * to pass. The durastore web application must also be initialized
 *
 * @author Bill Branan
 */
public class TestServiceRest
        extends TestCase {

    private static String configFileName = "test-duraservice.properties";
    private static ServiceUploadClient servicesAdmin;
    static {
        DuraServiceConfig.setConfigFileName(configFileName);
        String servicesAdminBaseURL;
        try {
            servicesAdminBaseURL = DuraServiceConfig.getServicesAdminUrl();
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        servicesAdmin = new ServiceUploadClient();
        servicesAdmin.setBaseURL(servicesAdminBaseURL);
        servicesAdmin.setRester(new RestHttpHelper());
    }

    private static RestHttpHelper restHelper = new RestHttpHelper();

    private static String baseUrl;

    private static String servicesUrl;

    private ServiceSerializer serializer;

    private static final String testServiceId = "helloservice-1.0.0.jar";

    @Override
    @Before
    protected void setUp() throws Exception {
        baseUrl = RestTestHelper.getBaseUrl();
        servicesUrl = baseUrl + "/services";

        // Initialize DuraService
        HttpResponse response = RestTestHelper.initialize();
        assertEquals(200, response.getStatusCode());
    }

    @Override
    @After
    protected void tearDown() throws Exception {
        undeployService();
    }

    @Test
    public void testGetAllServices() throws Exception {
        String url = servicesUrl;
        HttpResponse response = restHelper.get(url);
        assertEquals(200, response.getStatusCode());
        String content = response.getResponseBody();
        assertNotNull(content);
        List<String> allServicesList =
            SerializationUtil.deserializeList(content);
        assertTrue(allServicesList.contains(testServiceId));

        url = servicesUrl + "?show=all";
        response = restHelper.get(url);
        assertEquals(200, response.getStatusCode());
        String allContent = response.getResponseBody();
        assertNotNull(allContent);
        assertEquals(content, allContent);
    }

    @Test
    public void testDeployService() throws Exception {
        List<String> deployedServicesStart = getDeployedServicesViaAdmin();
        assertNotNull(deployedServicesStart);
        deployService();
        List<String> deployedServicesEnd = getDeployedServicesViaAdmin();
        assertNotNull(deployedServicesEnd);

        // TODO: Re-enable after verifying that listing works properly
        // assertEquals((deployedServicesStart.size() + 1), deployedServicesEnd.size());
    }

    @Test
    public void testGetService() throws Exception {
        deployService();
        String url = servicesUrl + "/" + testServiceId;
        HttpResponse response = restHelper.get(url);
        assertEquals(200, response.getStatusCode());
        String content = response.getResponseBody();
        assertNotNull(content);
        Map<String, String> serviceConfig =
            SerializationUtil.deserializeMap(content);
        assertNotNull(serviceConfig);

        String status = serviceConfig.get(ServiceManager.SERVICE_STATUS);
        assertNotNull(status);
        assertEquals(status, ServiceManager.ServiceStatus.DEPLOYED.status);

        Map<String, String> serviceAdminConfig =
            servicesAdmin.getServiceConfig(testServiceId);
        for(String configName : serviceAdminConfig.keySet()) {
            assertEquals(serviceConfig.get(configName),
                         serviceAdminConfig.get(configName));
        }
    }

    @Test
    public void testConfigureService() throws Exception {
        deployService();

        Map<String, String> configStart =
            servicesAdmin.getServiceConfig(testServiceId);
        assertNotNull(configStart);

        String configXml = RestTestHelper.buildTestServiceConfigXml();
        String value1 = String.valueOf(new Random().nextInt(99999));
        String value2 = String.valueOf(new Random().nextInt(99999));
        configXml = configXml.replace("value1", value1).replace("value2", value2);

        String url = servicesUrl + "/" + testServiceId;
        HttpResponse response = restHelper.post(url, configXml, null);
        assertEquals(200, response.getStatusCode());

        Map<String, String> configEnd =
            servicesAdmin.getServiceConfig(testServiceId);
        assertNotNull(configEnd);
        assertTrue(configEnd.get("property1").equals(value1));
        assertTrue(configEnd.get("property2").equals(value2));
        assertFalse(configStart.equals(configEnd));
    }

    @Test
    public void testUnDeployService() throws Exception {
        deployService();
        List<String> deployedServicesStart = getDeployedServicesViaAdmin();
        assertNotNull(deployedServicesStart);
        HttpResponse response = undeployService();
        assertEquals(200, response.getStatusCode());
        List<String> deployedServicesEnd = getDeployedServicesViaAdmin();
        assertNotNull(deployedServicesEnd);

        // TODO: Re-enable after verifying that uninstalling via services admin works
        // assertEquals((deployedServicesStart.size() - 1), deployedServicesEnd.size());
    }

    @Test
    public void testGetAvailableServices() throws Exception {
        String url = servicesUrl + "?show=available";
        HttpResponse response = restHelper.get(url);
        assertEquals(200, response.getStatusCode());
        String content = response.getResponseBody();
        assertNotNull(content);
        List<String> availableServicesListStart =
            SerializationUtil.deserializeList(content);
        assertTrue(availableServicesListStart.size() >= 1);
        assertTrue(availableServicesListStart.contains(testServiceId));

        deployService();

        response = restHelper.get(url);
        assertEquals(200, response.getStatusCode());
        content = response.getResponseBody();
        assertNotNull(content);
        List<String> availableServicesListEnd =
            SerializationUtil.deserializeList(content);

        assertEquals((availableServicesListStart.size() - 1),
                     availableServicesListEnd.size());
        assertFalse(availableServicesListEnd.contains(testServiceId));
    }

    @Test
    public void testGetDeployedServices() throws Exception {
        String url = servicesUrl + "?show=deployed";
        HttpResponse response = restHelper.get(url);
        assertEquals(200, response.getStatusCode());
        String content = response.getResponseBody();
        assertNotNull(content);
        List<String> deployedServicesListStart =
            SerializationUtil.deserializeList(content);
        assertFalse(deployedServicesListStart.contains(testServiceId));

        deployService();

        response = restHelper.get(url);
        assertEquals(200, response.getStatusCode());
        content = response.getResponseBody();
        assertNotNull(content);
        List<String> deployedServicesListEnd =
            SerializationUtil.deserializeList(content);

        assertEquals((deployedServicesListStart.size() + 1),
                     deployedServicesListEnd.size());
        assertTrue(deployedServicesListEnd.contains(testServiceId));
    }

    @Test
    public void testGetServiceHosts() throws Exception {
        String url = baseUrl + "/servicehosts";
        HttpResponse response = restHelper.get(url);
        assertEquals(200, response.getStatusCode());
        String content = response.getResponseBody();
        assertNotNull(content);
        List<String> serviceHosts =
            SerializationUtil.deserializeList(content);
        assertNotNull(serviceHosts);
        assertTrue(serviceHosts.size() > 0);
        assertTrue(serviceHosts.contains("localhost"));
    }

    private void deployService() throws Exception {
        String url = servicesUrl + "/" + testServiceId;
        HttpResponse response = restHelper.put(url, null, null);
        assertEquals(201, response.getStatusCode());
    }

    private HttpResponse undeployService() throws Exception {
        String url = servicesUrl + "/" + testServiceId;
        return restHelper.delete(url);
    }

    private List<String> getDeployedServicesViaAdmin() throws Exception {
        String deployedServicesXml =
            servicesAdmin.getServiceListing().getResponseBody();

        List<ComputeServiceBean> deployedServices;
        if(deployedServicesXml == null || deployedServicesXml.equals("")) {
            deployedServices = new ArrayList<ComputeServiceBean>();
        } else {
            deployedServices =
                getSerializer().deserializeList(deployedServicesXml);
        }

        List<String> deployedServiceNames = new ArrayList<String>();
        for(ComputeServiceBean deployedService : deployedServices) {
            deployedServiceNames.add(deployedService.getServiceName());
        }
        return deployedServiceNames;
    }

    private ServiceSerializer getSerializer() {
        if (serializer == null) {
            serializer = new XMLServiceSerializerImpl();
        }
        return serializer;
    }
}