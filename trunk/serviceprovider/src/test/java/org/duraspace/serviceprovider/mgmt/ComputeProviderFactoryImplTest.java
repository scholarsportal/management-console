
package org.duraspace.serviceprovider.mgmt;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ComputeProviderFactoryImplTest {

    private final String instanceId = "testInstanceId";

    private Map<String, String> map;

    private final String AMAZON = "amazon-ec2";

    private final String BAD = "bad";

    @Before
    public void setUp() throws Exception {

        map = new HashMap<String, String>();
        map
                .put(AMAZON,
                     "org.duraspace.serviceprovider.mgmt.mock.MockComputeProviderImpl");
        map.put(BAD, "org.duraspace.serviceprovider.mgmt.Mockxxxxxxxxx");

        ComputeProviderFactoryImpl.setIdToClassMap(map);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetComputeProvider() throws Exception {
        ServiceProvider provider = ComputeProviderFactoryImpl.getComputeProvider(AMAZON);
        assertNotNull(provider);

        assertFalse(provider.isInstanceRunning(instanceId));
    }

    @Test
    public void testGetInvalidComputeProvider() {
        ServiceProvider provider = null;
        try {
            provider = ComputeProviderFactoryImpl.getComputeProvider(BAD);
            fail("Should throw and exception!");
        } catch (Exception e) {
        }

        assertTrue(provider == null);
    }

}
