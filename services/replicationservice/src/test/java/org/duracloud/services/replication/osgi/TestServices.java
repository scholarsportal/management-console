
package org.duracloud.services.replication.osgi;

import org.junit.Test;

import org.duracloud.services.ComputeService;
import org.duracloud.services.replication.ReplicationService;
import org.duracloud.servicesutil.util.DuraConfigAdmin;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;

public class TestServices
        extends AbstractDuracloudOSGiTestBasePax {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void testDynamicConfig() throws Exception {
        log.debug("testing Dynamic Configuration of Replication Service");

        DynamicConfigTester tester =
                new DynamicConfigTester(getConfigAdmin(),
                                        getReplicationService());
        tester.testDynamicConfig();

    }

    protected Object getService(String serviceInterface) throws Exception {
        return getService(serviceInterface, null);
    }

    private Object getService(String serviceInterface, String filter)
            throws Exception {
        ServiceReference[] refs =
                bundleContext.getServiceReferences(serviceInterface, filter);

        if (refs == null || refs.length == 0) {
            String msg = "Unable to find service: " + serviceInterface;
            log.warn(msg);
            throw new Exception(msg);
        }
        Assert.assertNotNull("service not found: " + serviceInterface, refs[0]);
        log.debug(getPropsText(refs[0]));
        return bundleContext.getService(refs[0]);
    }

    private String getPropsText(ServiceReference ref) {
        StringBuilder sb = new StringBuilder("properties:");
        for (String key : ref.getPropertyKeys()) {
            sb.append("\tprop: [" + key);
            sb.append(":" + ref.getProperty(key) + "]\n");
        }
        return sb.toString();
    }

    public DuraConfigAdmin getConfigAdmin() throws Exception {
        DuraConfigAdmin configAdmin =
                (DuraConfigAdmin) getService(DuraConfigAdmin.class.getName());
        Assert.assertNotNull(configAdmin);
        return configAdmin;
    }

    public ReplicationService getReplicationService() throws Exception {
        ReplicationService replicationService =
                (ReplicationService) getService(ComputeService.class.getName(),
                                                "(duraService=replication)");
        Assert.assertNotNull(replicationService);
        return replicationService;
    }

}
