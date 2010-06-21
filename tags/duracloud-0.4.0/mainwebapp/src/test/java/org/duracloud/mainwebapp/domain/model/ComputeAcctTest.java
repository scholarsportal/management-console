/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.domain.model;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duracloud.computeprovider.domain.ComputeProviderType;
import org.duracloud.computeprovider.mgmt.ComputeProviderFactory;
import org.duracloud.computeprovider.mgmt.InstanceDescription;
import org.duracloud.computeprovider.mgmt.mock.MockComputeProviderProperties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class ComputeAcctTest {

    private ComputeAcct acct;

    private final int id = 66;

    private final String namespace = "namespace";

    private MockComputeProviderProperties props;

    private final String propA = "propA";

    private final String propB = "propB";

    private final String propC = "propC";

    private final ComputeProviderType providerType =
            ComputeProviderType.AMAZON_EC2;

    private final int providerId = 99;

    private final int credentialId = 88;

    private final int duraAcctId = 77;

    private final String MOCK_PROVIDER_CLASSNAME =
            "org.duracloud.computeprovider.mgmt.mock.LocalComputeProviderImpl";

    @Before
    public void setUp() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put(providerType.toString(), MOCK_PROVIDER_CLASSNAME);
        ComputeProviderFactory.setIdToClassMap(map);

        props = new MockComputeProviderProperties();
        props.setProp0(propA);
        props.setProp1(propB);
        props.setProp2(propC);

        acct = new ComputeAcct();
        acct.setInstanceId(null);
        acct.setNamespace(namespace);
        acct.setXmlProps(props.getAsXml());
        acct.setComputeProviderType(providerType);
        acct.setComputeProviderId(providerId);
        acct.setComputeCredentialId(credentialId);
        acct.setDuraAcctId(duraAcctId);
        acct.setId(id);

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
    public void testGetProps() throws Exception {
        String pps = acct.getXmlProps();
        assertNotNull(pps);

        MockComputeProviderProperties p = new MockComputeProviderProperties();
        p.loadFromXml(pps);
        assertEquals(p.getProp0(), propA);
        assertEquals(p.getProp1(), propB);
        assertEquals(p.getProp2(), propC);
    }

    @Test
    public void testMembers() {
        int compId = acct.getComputeProviderId();
        int anId = acct.getId();
        String nspace = acct.getNamespace();

        assertNotNull(compId);
        assertNotNull(anId);
        assertNotNull(nspace);

        assertEquals(compId, providerId);
        assertEquals(anId, id);
        assertEquals(nspace, namespace);

    }

}
