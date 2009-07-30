
package org.duracloud.servicesutil.osgi;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

import org.duracloud.services.ComputeService;
import org.duracloud.servicesutil.util.ServiceLister;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.osgi.util.OsgiStringUtils;

import junit.framework.Assert;

public class TestOSGiContainer
        extends AbstractDuracloudOSGiTestBase {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final static String REPLICATION_SERVICE = "ReplicationService";

    public void testInspectBundles() throws Exception {
        Bundle[] bundles = bundleContext.getBundles();
        assertNotNull(bundles);

        StringBuilder sb = new StringBuilder("bundles:");
        for (Bundle bundle : bundles) {
            sb.append("\tbundle: " + OsgiStringUtils.nullSafeName(bundle));
            String name = bundle.getSymbolicName();
            int state = bundle.getState();
            sb.append(": " + name + ": " + state);
            sb.append(", \n");
            if (bundle.getSymbolicName().contains("TestOSGiContainer")) {
                Dictionary headers = bundle.getHeaders();
                Enumeration keys = headers.keys();
                while (keys.hasMoreElements()) {
                    Object key = keys.nextElement();
                    sb.append("\t\tTest-Manifest-Entry : [" + key + "|"
                            + headers.get(key) + "]\n");
                }
            }

        }
        log.debug(sb.toString());
    }

    public void testServiceLister() throws Exception {
        ServiceReference ref =
                bundleContext
                        .getServiceReference("org.duracloud.servicesutil.util.ServiceLister");
        Assert.assertNotNull(ref);

        StringBuilder sb = new StringBuilder("properties:");
        for (String key : ref.getPropertyKeys()) {
            sb.append("\tprop: [" + key);
            sb.append(":" + ref.getProperty(key) + "]\n");
        }
        Bundle listerBundle = ref.getBundle();
        assertNotNull(listerBundle);

        ServiceLister lister = (ServiceLister) bundleContext.getService(ref);
        assertNotNull(lister);

        List<ComputeService> duraServices = lister.getDuraServices();
        assertNotNull(duraServices);

        verifyComputeServicesFound(duraServices);

        log.debug(sb.toString());
    }

    public void testComputeServicesInRegistry() throws Exception {
        ServiceReference[] refs =
                bundleContext.getAllServiceReferences(ComputeService.class
                        .getName(), null);
        Assert.assertNotNull(refs);

        List<ComputeService> computeServices = new ArrayList<ComputeService>();
        for (ServiceReference ref : refs) {
            Bundle serviceBundle = ref.getBundle();
            assertNotNull(serviceBundle);

            ComputeService computeService =
                    (ComputeService) bundleContext.getService(ref);
            assertNotNull(computeService);
            computeServices.add(computeService);
        }

        verifyComputeServicesFound(computeServices);
    }

    private void verifyComputeServicesFound(List<ComputeService> computeServices)
            throws Exception {
        boolean replicationServiceFound = false;
        for (ComputeService duraService : computeServices) {
            String serviceDesc = duraService.describe();
            log.debug("dura-service: " + serviceDesc);

            if (!replicationServiceFound) {
                replicationServiceFound =
                        serviceFound(serviceDesc, REPLICATION_SERVICE);
            }
        }
        Assert.assertTrue(replicationServiceFound);
    }

    private boolean serviceFound(String serviceName, String targetName) {
        return serviceName.contains(targetName);
    }
}
