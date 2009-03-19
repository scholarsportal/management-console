
package org.duraspace.common.util;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ApplicationConfigTest {

    private final String key0 = "test.element";

    private final String key1 = "test.value";

    private final String val0 = "hello";

    private final String val1 = "monkey";

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetPropsFromResource() throws Exception {
        Properties props =
                ApplicationConfig.getPropsFromResource("test.properties");
        assertNotNull(props);
        assertEquals(val0, props.getProperty(key0));
        assertEquals(val1, props.getProperty(key1));
    }

    @Test
    public void testGetXmlFromProps() throws Exception {
        Properties props = new Properties();
        props.put(key0, val0);
        props.put(key1, val1);

        String xml = ApplicationConfig.getXmlFromProps(props);
        assertNotNull(xml);
        System.out.println(xml);

        Properties propsNew = ApplicationConfig.getPropsFromXml(xml);
        assertNotNull(propsNew);
        assertEquals(val0, propsNew.getProperty(key0));
        assertEquals(val1, propsNew.getProperty(key1));

    }

}
