package org.duracloud.common.util.error;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Andrew Woods
 *         Date: Oct 24, 2009
 */
public class DuraCloudExceptionTest {

    private final String key0 = "test.element";
    private final String key1 = "test.does.not.exist";

    private final String val0 = "hello";

    @Before
    public void setUp() {
        ExceptionMessages.setConfigFileName("test.properties");
    }

    @Test
    public void testGetFormatedMessagePattern() {
        DuraCloudException e = createException(key0);
        assertNotNull(e);

        String msg = e.getFormatedMessage();
        assertNotNull(msg);
        assertEquals(val0, msg);
    }

    @Test
    public void testGetFormatedMessageStack() {
        DuraCloudException e = createException(key1);
        assertNotNull(e);

        String msg = e.getFormatedMessage();
        assertNotNull(msg);
        assertTrue(!val0.equals(msg));
    }

    private DuraCloudException createException(String key) {
        DuraCloudException e = null;
        try {
            Integer.parseInt("junk");
        } catch (NumberFormatException nfe) {
            e = new DuraCloudException(nfe, key);
        }
        return e;
    }

}
