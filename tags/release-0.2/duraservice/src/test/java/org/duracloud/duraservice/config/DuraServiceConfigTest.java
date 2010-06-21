package org.duracloud.duraservice.config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

public class DuraServiceConfigTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetProps() throws Exception {
        DuraServiceConfig config = new DuraServiceConfig();

        String port = config.getPort();
        assertNotNull(port);
        assertFalse(port.equals("${tomcat.port}"));

        String servicesAdminURL = config.getServicesAdminUrl();
        assertNotNull(servicesAdminURL);
    }
}
