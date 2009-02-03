package org.duraspace.rest;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Tests service functions.
 *
 * @author Bill Branan
 */
public class ServiceResourceTest
        extends TestCase {

    @Test
    public void testGetServices() throws Exception {
        String xml = ServiceResource.getServices();

        assertNotNull(xml);
        assertEquals(xml, "<services />");
    }

    @Test
    public void testGetServiceSubscriptions() throws Exception {
        String xml = ServiceResource.getServiceSubscriptions("customer1");

        assertNotNull(xml);
        assertEquals(xml, "<serviceSubscriptions />");
    }

    @Test
    public void testGetServiceConfiguration() throws Exception {
        String xml = ServiceResource.
            getServiceConfiguration("customer1", "provider1");

        assertNotNull(xml);
        assertEquals(xml, "<serviceConfiguration />");
    }

    @Test
    public void testAddServiceConfiguration() throws Exception {
        boolean success = ServiceResource.
            addServiceSubscription("customer1", "provider1", "<config />");

        assertTrue(success);
    }

    @Test
    public void testUpdateServiceConfiguration() throws Exception {
        boolean success = ServiceResource.
            updateServiceConfiguration("customer1", "provider1", "<config />");

        assertTrue(success);
    }

    @Test
    public void testRemoveServiceConfiguration() throws Exception {
        boolean success = ServiceResource.
            removeServiceSubscription("customer1", "provider1");

        assertTrue(success);
    }

}