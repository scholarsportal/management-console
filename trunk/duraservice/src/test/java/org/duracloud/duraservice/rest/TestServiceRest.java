package org.duracloud.duraservice.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duracloud.common.util.SerializationUtil;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.duraservice.config.DuraServiceConfig;
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

    private static String servicesUrl;

    private ServiceSerializer serializer;

    private static final String testServiceId = "helloservice-1.0.0.jar";

    @Override
    @Before
    protected void setUp() throws Exception {
        String baseUrl = RestTestHelper.getBaseUrl();
        servicesUrl = baseUrl + "/services";

        // Initialize DuraService
        HttpResponse response = RestTestHelper.initialize();
        int statusCode = response.getStatusCode();
        assertTrue("status: " + statusCode, statusCode == 200);
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
        assertTrue(response.getStatusCode() == 200);
        String content = response.getResponseBody();
        assertNotNull(content);
        List<Object> allServicesList =
            SerializationUtil.deserializeList(content);
        assertTrue(allServicesList.contains(testServiceId));

        url = servicesUrl + "?show=all";
        response = restHelper.get(url);
        assertTrue(response.getStatusCode() == 200);
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

        // TODO: Re-enable after verifying that uninstalling via services admin works
        // assertTrue((deployedServicesStart.size() + 1)  == deployedServicesEnd.size());
    }

    @Test
    public void testConfigureService() throws Exception {
        deployService();

        // FIXME: Uncomment to actually make calls which call through to services admin
//        Map<String, String> configStart =
//            servicesAdmin.getServiceConfig(testServiceId);
//        assertNotNull(configStart);

//        String configXml = RestTestHelper.buildTestServiceConfigXml();

//        String url = servicesUrl + "/" + testServiceId;
//        HttpResponse response = restHelper.post(url, configXml, null);
//        assertTrue(response.getStatusCode() == 200);

//        Map<String, String> configEnd =
//            servicesAdmin.getServiceConfig(testServiceId);
//        assertNotNull(configEnd);
//        assertTrue(configEnd.get("property1").equals("value1"));
//        assertTrue(configEnd.get("property2").equals("value2"));

        // TODO: Re-enable after verifying that uninstalling via services admin works
//        assertFalse(configStart.equals(configEnd));
    }

    @Test
    public void testUnDeployService() throws Exception {
        deployService();
        List<String> deployedServicesStart = getDeployedServicesViaAdmin();
        assertNotNull(deployedServicesStart);
        HttpResponse response = undeployService();
        // FIXME: Uncomment to actually check services admin response
//        assertTrue(response.getStatusCode() == 200);
        List<String> deployedServicesEnd = getDeployedServicesViaAdmin();
        assertNotNull(deployedServicesEnd);

        // TODO: Re-enable after verifying that uninstalling via services admin works
//        assertTrue((deployedServicesStart.size() - 1)  ==
//                     deployedServicesEnd.size());
    }

    @Test
    public void testGetAvailableServices() throws Exception {
        String url = servicesUrl + "?show=available";
        HttpResponse response = restHelper.get(url);
        assertTrue(response.getStatusCode() == 200);
        String content = response.getResponseBody();
        assertNotNull(content);
        List<Object> availableServicesListStart =
            SerializationUtil.deserializeList(content);
        assertTrue(availableServicesListStart.size() >= 1);
        assertTrue(availableServicesListStart.contains(testServiceId));

        deployService();

        response = restHelper.get(url);
        assertTrue(response.getStatusCode() == 200);
        content = response.getResponseBody();
        assertNotNull(content);
        List<Object> availableServicesListEnd =
            SerializationUtil.deserializeList(content);

        // FIXME: Uncomment when deployment is actually happening
//        assertTrue((availableServicesListStart.size() - 1) ==
//                    availableServicesListEnd.size());
//        assertFalse(availableServicesListEnd.contains(testServiceId));
    }

    @Test
    public void testGetDeployedServices() throws Exception {
        String url = servicesUrl + "?show=deployed";
        HttpResponse response = restHelper.get(url);
        assertTrue(response.getStatusCode() == 200);
        String content = response.getResponseBody();
        assertNotNull(content);
        List<Object> deployedServicesListStart =
            SerializationUtil.deserializeList(content);
        assertFalse(deployedServicesListStart.contains(testServiceId));

        deployService();

        response = restHelper.get(url);
        assertTrue(response.getStatusCode() == 200);
        content = response.getResponseBody();
        assertNotNull(content);
        List<Object> deployedServicesListEnd =
            SerializationUtil.deserializeList(content);

        // FIXME: Uncomment when deployment is actually happening
//        assertTrue((deployedServicesListStart.size() + 1) ==
//                    deployedServicesListEnd.size());
//        assertTrue(deployedServicesListEnd.contains(testServiceId));
    }

    @Test
    public void testGetServiceStatus() throws Exception {
        deployService();
        String url = servicesUrl + "/" + testServiceId;
        HttpResponse response = restHelper.get(url);
        // TODO: Update when get service status is implemented
        assertTrue(response.getStatusCode() == 501);
    }

    private void deployService() throws Exception {
        // FIXME: Uncomment to actually make calls which call through to services admin
//        String url = servicesUrl + "/" + testServiceId;
//        HttpResponse response = restHelper.put(url, null, null);
//        assertTrue(response.getStatusCode() == 201);
    }

    private HttpResponse undeployService() throws Exception {
        // FIXME: Uncomment to actually make calls which call through to services admin
//        String url = servicesUrl + "/" + testServiceId;
//        return restHelper.delete(url);

        return null;
    }

    private List<String> getDeployedServicesViaAdmin() throws Exception {
     // FIXME: Uncomment to actually make calls to services admin
//        String deployedServicesXml =
//            servicesAdmin.getServiceListing().getResponseBody();
//
//        List<ComputeServiceBean> deployedServices;
//        if(deployedServicesXml == null || deployedServicesXml.equals("")) {
//            deployedServices = new ArrayList<ComputeServiceBean>();
//        } else {
//            deployedServices =
//                getSerializer().deserializeList(deployedServicesXml);
//        }
//
//        List<String> deployedServiceNames = new ArrayList<String>();
//        for(ComputeServiceBean deployedService : deployedServices) {
//            deployedServiceNames.add(deployedService.getServiceName());
//        }
//        return deployedServiceNames;

        return new ArrayList<String>();
    }

    private ServiceSerializer getSerializer() {
        if (serializer == null) {
            serializer = new XMLServiceSerializerImpl();
        }
        return serializer;
    }
}