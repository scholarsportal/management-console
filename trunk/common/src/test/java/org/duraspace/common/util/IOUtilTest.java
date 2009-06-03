package org.duraspace.common.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * Tests the I/O Utilities.
 *
 * @author Bill Branan
 */
public class IOUtilTest {

    protected static final Logger log =
            Logger.getLogger(SerializationUtilTest.class);

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testReadStringFromStream() throws Exception {
        String testValue = "This is a testing string";
        InputStream testStream = new ByteArrayInputStream(testValue.getBytes("UTF-8"));
        String readValue = IOUtil.readStringFromStream(testStream);
        assertTrue(testValue.equals(readValue));
    }

}
