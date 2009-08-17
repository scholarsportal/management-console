
package org.duracloud.servicesadmin.osgi;

import java.util.HashMap;
import java.util.Map;

import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.services.ComputeService;
import org.duracloud.servicesutil.client.ServiceUploadClient;

import junit.framework.Assert;

public class ServiceConfigurationTester {

    private final ComputeService service;

    private ServiceUploadClient client;

    private Map<String, String> configOrig;

    private Map<String, String> configNew;

    private final String configId = "org.duracloud.test.config";

    private final String servicePidKey = "service.pid";

    private static String BASE_URL =
            "http://localhost:8089/servicesadmin-1.0.0";

    public ServiceConfigurationTester(ComputeService service) {
        Assert.assertNotNull(service);
        this.service = service;
        setUp();
    }

    private void setUp() {
        configOrig = new HashMap<String, String>();
        configNew = new HashMap<String, String>();

        for (int i = 0; i < 3; ++i) {
            String key = "key" + i;
            String val = "val" + i;
            configOrig.put(key, val + "-orig");
            configNew.put(key, val + "-new");
        }
    }

    public void testServiceConfiguration() throws Exception {
        // Post and verify original-config.
        getClient().postServiceConfig(configId, configOrig);

        Map<String, String> props = getClient().getServiceConfig(configId);
        verifyConfiguration(configOrig, props);

        // Post and verify updated-config.
        getClient().postServiceConfig(configId, configNew);

        props = getClient().getServiceConfig(configId);
        verifyConfiguration(configNew, props);

    }

    private void verifyConfiguration(Map<String, String> configExpected,
                                     Map<String, String> configFound) {
        Assert.assertNotNull(configFound);

        String pid = configFound.get(servicePidKey);
        Assert.assertNotNull(pid);
        Assert.assertEquals(configId, pid);
        configFound.remove(servicePidKey);

        Assert.assertEquals(configExpected.size(), configFound.size());

        for (String key : configExpected.keySet()) {
            Assert.assertTrue(configFound.containsKey(key));
            Assert.assertEquals(configExpected.get(key), configFound.get(key));
        }
    }

    private ServiceUploadClient getClient() {
        if (client == null) {
            client = new ServiceUploadClient();
            client.setRester(new RestHttpHelper());
            client.setBaseURL(BASE_URL);
        }
        return client;
    }
}
