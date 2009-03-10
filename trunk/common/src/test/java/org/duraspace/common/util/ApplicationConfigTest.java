
package org.duraspace.common.util;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

import static junit.framework.Assert.assertNotNull;

/**
 * @author Andrew Woods
 */
public class ApplicationConfigTest {

    private final String portKey = "port";

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetMainWebAppProps() throws Exception {
        Properties props = ApplicationConfig.getMainWebAppProps();
        assertNotNull(props);

        String port = props.getProperty(portKey);
        assertNotNull(port);
        Assert.assertEquals("8080", port);
    }

}
