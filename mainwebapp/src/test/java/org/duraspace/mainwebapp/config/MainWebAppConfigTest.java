
package org.duraspace.mainwebapp.config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.duraspace.common.model.Credential;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MainWebAppConfigTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetMainWebAppProps() throws Exception {

        String port = MainWebAppConfig.getPort();
        assertNotNull(port);

        String dbHome = MainWebAppConfig.getDbHome();
        assertNotNull(dbHome);
        assertEquals("/opt/derby/", dbHome);

        String dbName = MainWebAppConfig.getDbName();
        assertNotNull(dbName);
        assertEquals("duraspaceDB", dbName);

        Credential dbCredential = MainWebAppConfig.getDbCredential();
        assertNotNull(dbCredential);
        assertEquals("duraspace", dbCredential.getUsername());
        assertEquals("duraspace", dbCredential.getPassword());

        boolean dbLoadTestData = MainWebAppConfig.getDbLoadTestData();
        assertTrue(dbLoadTestData);
    }
}
