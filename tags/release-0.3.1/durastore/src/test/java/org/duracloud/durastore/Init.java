package org.duracloud.durastore;

import org.junit.Test;

import org.duracloud.common.web.RestHttpHelper.HttpResponse;
import org.duracloud.durastore.rest.RestTestHelper;
import org.apache.commons.httpclient.HttpStatus;

import junit.framework.TestCase;

/**
 * Provides a way to initialize DuraStore with values
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
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
    }
 }