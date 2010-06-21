package org.duracloud.duraservice;

import org.junit.Test;

import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.duraservice.rest.RestTestHelper;

import junit.framework.TestCase;

/**
 * Provides a way to initialize DuraService with values
 * from the test database without needing to run the
 * full test suite.
 *
 * @author Bill Branan
 */
public class Init
       extends TestCase {

    @Test
    public void testInit() throws Exception {
        HttpResponse response = RestTestHelper.initialize();
        assertEquals(200, response.getStatusCode());
    }
}