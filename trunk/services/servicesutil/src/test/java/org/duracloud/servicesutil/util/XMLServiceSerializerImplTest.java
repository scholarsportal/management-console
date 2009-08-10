
package org.duracloud.servicesutil.util;

import java.util.ArrayList;
import java.util.List;

import org.duracloud.services.ComputeService;
import org.duracloud.servicesutil.beans.ComputeServiceBean;

import junit.framework.TestCase;

public class XMLServiceSerializerImplTest
        extends TestCase {

    private XMLServiceSerializerImpl serializer;

    private final String serviceName0 = "org.test.service0";

    private final String serviceName1 = "org.test.service1";

    private final String serviceName2 = "org.test.service2";

    public void testSerializeDeserializeList() throws Exception {
        serializer = new XMLServiceSerializerImpl();

        List<ComputeService> services = new ArrayList<ComputeService>();
        services.add(new MockComputeService(serviceName0));
        services.add(new MockComputeService(serviceName1));
        services.add(new MockComputeService(serviceName2));

        String serialized = serializer.serialize(services);
        assertNotNull(serialized);

        assertTrue(serialized.contains(serviceName0));
        assertTrue(serialized.contains(serviceName1));
        assertTrue(serialized.contains(serviceName2));

        List<ComputeServiceBean> beans = serializer.deserializeList(serialized);
        assertNotNull(beans);

        boolean foundService0 = false;
        boolean foundService1 = false;
        boolean foundService2 = false;
        for (ComputeServiceBean bean : beans) {
            if (serviceName0.equals(bean.getServiceName())) {
                foundService0 = true;
            } else if (serviceName1.equals(bean.getServiceName())) {
                foundService1 = true;
            } else if (serviceName2.equals(bean.getServiceName())) {
                foundService2 = true;
            }
        }

        assertTrue(foundService0);
        assertTrue(foundService1);
        assertTrue(foundService2);
    }

    public void testSerializeDeserializeBean() throws Exception {
        serializer = new XMLServiceSerializerImpl();

        ComputeServiceBean service = new ComputeServiceBean(serviceName0);

        String xml = serializer.serialize(service);
        assertNotNull(xml);
        assertTrue(xml.contains(serviceName0));

        ComputeServiceBean bean = serializer.deserializeBean(xml);
        assertNotNull(bean);
        assertEquals(serviceName0, bean.getServiceName());

    }

    private class MockComputeService
            implements ComputeService {

        private final String name;

        public MockComputeService(String name) {
            this.name = name;
        }

        public String describe() throws Exception {
            return name;
        }

        public void start() throws Exception {
        }

        public void stop() throws Exception {
        }

    }

}
