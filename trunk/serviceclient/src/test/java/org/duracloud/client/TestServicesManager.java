package org.duracloud.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Runtime test of DuraService java client. DuraService must
 * be available and initialized for these tests to pass.
 *
 * @author Bill Branan
 */
public class TestServicesManager
        extends TestCase {

    protected static final Logger log =
        Logger.getLogger(TestServicesManager.class);

    private static String host = "localhost";

    private static String port = null;

    private static final String defaultPort = "8080";

    private ServicesManager servicesManager;

    private static final String testServiceId = "helloservice-1.0.0.jar";

    @Override
    @Before
    protected void setUp() throws Exception {
        servicesManager = new ServicesManager(host, getPort());
        assertNotNull(servicesManager.getBaseURL());
    }

    private static String getPort() throws Exception {
        if(port == null) {
            port = System.getProperty("tomcat.port.default");
            if(port == null) {
                port = defaultPort;
            }
        }
        return port;
    }

    @Override
    @After
    protected void tearDown() throws Exception {
        // Make sure the test service is undeployed
        try {
            servicesManager.undeployService(testServiceId);
        } catch(ServicesException se) {
            // Ignore, the service was likely already removed
        }
    }

    @Test
    public void testGetAllServices() throws Exception {
        List<String> allServices = servicesManager.getAllServices();
        assertTrue(allServices.contains(testServiceId));
    }

    @Test
    public void testDeployService() throws Exception {
        List<String> deployedServicesStart = servicesManager.getDeployedServices();
        assertNotNull(deployedServicesStart);
        servicesManager.deployService(testServiceId, null);
        List<String> deployedServicesEnd = servicesManager.getDeployedServices();
        assertNotNull(deployedServicesEnd);
        assertTrue((deployedServicesStart.size() + 1)  == deployedServicesEnd.size());
    }

    @Test
    public void testGetServiceStatus() throws Exception {
        String status = servicesManager.getServiceStatus(testServiceId);
        assertNotNull(status);
        assertEquals("available", status);
        servicesManager.deployService(testServiceId, null);
        status = servicesManager.getServiceStatus(testServiceId);
        assertNotNull(status);
        assertEquals("deployed", status);
    }

    @Test
    public void testConfigureService() throws Exception {
        servicesManager.deployService(testServiceId, null);

        Map<String, String> configStart =
            servicesManager.getServiceConfig(testServiceId);
        assertNotNull(configStart);

        Map<String, String> newConfig = new HashMap<String, String>();
        String value1 = String.valueOf(new Random().nextInt(99999));
        String value2 = String.valueOf(new Random().nextInt(99999));
        newConfig.put("property1", value1);
        newConfig.put("property2", value2);
        servicesManager.configureService(testServiceId, newConfig);

        Map<String, String> configEnd =
            servicesManager.getServiceConfig(testServiceId);
        assertNotNull(configEnd);
        assertTrue(configEnd.get("property1").equals(value1));
        assertTrue(configEnd.get("property2").equals(value2));

        assertFalse(configStart.equals(configEnd));
    }

    @Test
    public void testUnDeployService() throws Exception {
        servicesManager.deployService(testServiceId, null);
        List<String> deployedServicesStart = servicesManager.getDeployedServices();
        assertNotNull(deployedServicesStart);
        servicesManager.undeployService(testServiceId);
        List<String> deployedServicesEnd = servicesManager.getDeployedServices();
        assertNotNull(deployedServicesEnd);
        assertTrue((deployedServicesStart.size() - 1)  == deployedServicesEnd.size());
    }

    @Test
    public void testGetAvailableServices() throws Exception {
        List<String> availableServicesListStart =
            servicesManager.getAvailableServices();
        assertTrue(availableServicesListStart.size() >= 1);
        assertTrue(availableServicesListStart.contains(testServiceId));

        servicesManager.deployService(testServiceId, null);

        List<String> availableServicesListEnd =
            servicesManager.getAvailableServices();
        assertTrue((availableServicesListStart.size() - 1) ==
                    availableServicesListEnd.size());
        assertFalse(availableServicesListEnd.contains(testServiceId));
    }

    @Test
    public void testGetDeployedServices() throws Exception {
        List<String> deployedServicesListStart =
            servicesManager.getDeployedServices();
        assertFalse(deployedServicesListStart.contains(testServiceId));

        servicesManager.deployService(testServiceId, null);

        List<String> deployedServicesListEnd =
            servicesManager.getDeployedServices();
        assertTrue((deployedServicesListStart.size() + 1) ==
                    deployedServicesListEnd.size());
        assertTrue(deployedServicesListEnd.contains(testServiceId));
    }

}