
package org.duracloud.servicesutil.osgi;

import java.util.List;

import org.duracloud.services.ComputeService;
import org.duracloud.servicesutil.util.ServiceLister;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class ServiceListerTester {

    private final ServiceLister lister;

    private final static String REPLICATION_SERVICE = "ReplicationService";

    public ServiceListerTester(ServiceLister lister) {
        this.lister = lister;
    }

    public void testServiceLister() throws Exception {
        List<ComputeService> duraServices = lister.getDuraServices();
        assertNotNull(duraServices);

        verifyComputeServicesFound(duraServices);

    }

    private void verifyComputeServicesFound(List<ComputeService> computeServices)
            throws Exception {
        boolean replicationServiceFound = false;
        for (ComputeService duraService : computeServices) {
            String serviceDesc = duraService.describe();

            if (!replicationServiceFound) {
                replicationServiceFound =
                        serviceFound(serviceDesc, REPLICATION_SERVICE);
            }
        }
        assertTrue(replicationServiceFound);
    }

    private boolean serviceFound(String serviceName, String targetName) {
        return serviceName.contains(targetName);
    }
}
