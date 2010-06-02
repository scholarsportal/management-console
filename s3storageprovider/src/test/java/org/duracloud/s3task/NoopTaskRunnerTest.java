package org.duracloud.s3task;

import org.junit.Test;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author: Bill Branan
 * Date: May 21, 2010
 */
public class NoopTaskRunnerTest {

    @Test
    public void testNoopTaskRunner() {
        NoopTaskRunner noop = new NoopTaskRunner();

        String name = noop.getName();
        assertEquals("noop", name);

        String response = noop.performTask("");
        assertNotNull(response);
    }
}
