package org.duracloud.durastore.config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

public class DuraStoreConfigTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetMainWebAppProps() throws Exception {

        String port = DuraStoreConfig.getPort();
        assertNotNull(port);
        assertFalse(port.equals("${tomcat.port}"));

    }
}
