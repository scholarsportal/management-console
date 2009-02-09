package org.duraspace.rest;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Tests space functions.
 *
 * @author Bill Branan
 */
public class SpaceResourceTest
        extends TestCase {

    @Test
    public void testGetSpaces() throws Exception {
        String xml = SpaceResource.getSpaces("customer1");

        assertNotNull(xml);
        assertEquals(xml, "<spaces />");
    }

    @Test
    public void testGetSpaceProperties() throws Exception {
        String xml = SpaceResource.getSpaceProperties("customer1", "space1");

        assertNotNull(xml);
        assertEquals(xml, "<space />");
    }

    @Test
    public void testGetSpaceContents() throws Exception {
        String xml = SpaceResource.getSpaceContents("customer1", "space1");
        assertNotNull(xml);
        assertEquals(xml, "<contents />");
    }

    @Test
    public void testAddSpace() throws Exception {
        boolean success = SpaceResource.addSpace("customer1",
                                                 "space1",
                                                 "mySpace",
                                                 "open");
        assertTrue(success);
    }

    @Test
    public void testUpdateSpaceProperties() throws Exception {
        boolean success = SpaceResource.updateSpaceProperties("customer1",
                                                              "space1",
                                                              "mySpace",
                                                              "open");
        assertTrue(success);
    }

    @Test
    public void testDeleteSpace() throws Exception {
        boolean success = SpaceResource.deleteSpace("customer1", "space1");
        assertTrue(success);
    }
}