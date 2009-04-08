package org.duraspace.mainwebapp.rest;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * Tests service functions.
 *
 * @author Bill Branan
 */
public class ComputeResourceTest
        extends TestCase {

    @Test
    public void testGetServices() throws Exception {
        String xml = ComputeResource.getServices();

        assertNotNull(xml);
        assertEquals(xml, "<services />");
    }

    @Test
    public void testGetServiceSubscriptions() throws Exception {
        String xml = ComputeResource.getServiceSubscriptions("customer1");

        assertNotNull(xml);
        assertEquals(xml, "<serviceSubscriptions />");
    }

    @Test
    public void testGetServiceConfiguration() throws Exception {
        String xml = ComputeResource.
            getServiceConfiguration("customer1", "provider1");

        assertNotNull(xml);
        assertEquals(xml, "<serviceConfiguration />");
    }

    @Test
    public void testAddServiceConfiguration() throws Exception {
        boolean success = ComputeResource.
            addServiceSubscription("customer1", "provider1", "<config />");

        assertTrue(success);
    }

    @Test
    public void testUpdateServiceConfiguration() throws Exception {
        boolean success = ComputeResource.
            updateServiceConfiguration("customer1", "provider1", "<config />");

        assertTrue(success);
    }

    @Test
    public void testRemoveServiceConfiguration() throws Exception {
        boolean success = ComputeResource.
            removeServiceSubscription("customer1", "provider1");

        assertTrue(success);
    }

}