
package org.duracloud.servicesutil.client;

import java.util.HashMap;
import java.util.Map;

import org.duracloud.common.util.SerializationUtil;
import org.duracloud.common.web.RestHttpHelper;
import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.servicesutil.util.XMLServiceSerializerImpl;
import org.easymock.EasyMock;

import junit.framework.Assert;
import junit.framework.TestCase;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

public class ServiceUploadClientTest
        extends TestCase {

    private ServiceUploadClient client;

    private final String configId = "configId-test";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        client = new ServiceUploadClient();
        client.setBaseURL("http://junk.com");
        client.setSerializer(new XMLServiceSerializerImpl());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testPostServiceBundle() {
        //        fail("Not yet implemented");
    }

    public void testDeleteServiceBundle() {
        //        fail("Not yet implemented");
    }

    public void testGetServiceListing() {
        //        fail("Not yet implemented");
    }

    public void testGetServiceConfig() throws Exception {
        // SetUp
        Map<String, String> testConfig = new HashMap<String, String>();
        testConfig.put("key0", "val0");
        testConfig.put("key1", "val1");
        testConfig.put("key2", "val2");
        String testConfigXml = SerializationUtil.serializeMap(testConfig);

        client.setRester(mockRestHttpHelperConfigGET(testConfigXml));

        // Test
        Map<String, String> config = client.getServiceConfig(configId);
        Assert.assertNotNull(config);
        Assert.assertEquals(testConfig.size(), config.size());

        for (String key : testConfig.keySet()) {
            Assert.assertEquals(testConfig.get(key), config.get(key));
        }
    }

    private RestHttpHelper mockRestHttpHelperConfigGET(String xml)
            throws Exception {
        HttpResponse mockResponse = createMock(HttpResponse.class);
        EasyMock.expect(mockResponse.getResponseBody()).andReturn(xml)
                .anyTimes();
        replay(mockResponse);

        RestHttpHelper helper = createMock(RestHttpHelper.class);
        EasyMock.expect(helper.get(EasyMock.isA(String.class)))
                .andReturn(mockResponse);
        replay(helper);

        return helper;
    }

}
