package org.duracloud.aitsync.util;

import java.io.File;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/**
 * 
 * @author Daniel Bernstein
 * Date:  12/24/2012
 *
 */
public class IOUtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testToAndFrom() throws Exception {
        Integer i = new Integer(1000);
        File file = File.createTempFile("test", ".xml");
        file.deleteOnExit();
        IOUtils.toXML(file, i);
        Integer i2 = (Integer) IOUtils.fromXML(file);
        Assert.assertEquals(i, i2);
    }

}
