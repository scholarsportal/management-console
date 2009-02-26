
package org.duraspace.mainwebapp.domain.model;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.serviceprovider.mgmt.ComputeProviderFactoryImpl;
import org.duraspace.serviceprovider.mgmt.InstanceDescription;
import org.duraspace.serviceprovider.mgmt.mock.MockServiceProviderProperties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class ComputeAcctTest {

    private ComputeAcct acct;

    private final String id = "id";

    private final String namespace = "namespace";

    private MockServiceProviderProperties props;

    private final String propA = "propA";

    private final String propB = "propB";

    private final String propC = "propC";

    private final String MOCK_PROVIDER = "mockProvider";

    private final String MOCK_PROVIDER_CLASSNAME =
            "org.duraspace.serviceprovider.mgmt.mock.MockComputeProviderImpl";

    @Before
    public void setUp() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put(MOCK_PROVIDER, MOCK_PROVIDER_CLASSNAME);
        ComputeProviderFactoryImpl.setIdToClassMap(map);

        props = new MockServiceProviderProperties();
        props.setProp0(propA);
        props.setProp1(propB);
        props.setProp2(propC);

        acct = new ComputeAcct();
        acct.setComputeProviderId(MOCK_PROVIDER);
        acct.setId(id);
        acct.setInstanceId(null);
        acct.setNamespace(namespace);
        acct.setProps(props);

    }

    @After
    public void tearDown() throws Exception {
        props = null;
        acct = null;
    }

    @Test
    public void testDescribeRunningInstance() throws Exception {
        InstanceDescription desc = acct.describeRunningInstance();
        assertNotNull(desc);
    }

    @Test
    public void testCycleInstance() throws Exception {
        assertTrue(!acct.isInstanceBooting());
        assertTrue(!acct.isInstanceRunning());
        assertTrue(!acct.isWebappRunning());

        assertTrue(acct.getInstanceId() == null);

        acct.startInstance();

        assertNotNull(acct.getInstanceId());

        assertTrue(acct.isInstanceRunning());
        assertTrue(acct.isWebappRunning());

        acct.stopInstance();

        assertTrue(!acct.isInstanceBooting());
        assertTrue(!acct.isInstanceRunning());
        assertTrue(!acct.isWebappRunning());
    }

    @Test
    public void testGetProps() {
        MockServiceProviderProperties pps =
                (MockServiceProviderProperties) acct.getProps();
        assertNotNull(pps);
        assertEquals(pps.getProp0(), propA);
        assertEquals(pps.getProp1(), propB);
        assertEquals(pps.getProp2(), propC);
    }

    @Test
    public void testMembers() {
        String compId = acct.getComputeProviderId();
        String anId = acct.getId();
        String nspace = acct.getNamespace();

        assertNotNull(compId);
        assertNotNull(anId);
        assertNotNull(nspace);

        assertEquals(compId, MOCK_PROVIDER);
        assertEquals(anId, id);
        assertEquals(nspace, namespace);

    }

}
