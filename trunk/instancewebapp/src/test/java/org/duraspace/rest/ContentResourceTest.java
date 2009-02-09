package org.duraspace.rest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Tests content functions.
 *
 * @author Bill Branan
 */
public class ContentResourceTest
        extends TestCase {

    @Test
    public void testGetServices() throws Exception {
        InputStream content = ContentResource.getContent("customer1",
                                                         "space1",
                                                         "content1");
        assertNotNull(content);
    }

    @Test
    public void testGetContentProperties() throws Exception {
        String xml = ContentResource.getContentProperties("customer1",
                                                          "space1",
                                                          "content1");
        assertNotNull(xml);
        assertEquals(xml, "<content />");
    }

    @Test
    public void testUpdateContentProperties() throws Exception {
        boolean success = ContentResource.updateContentProperties("customer1",
                                                                  "provider1",
                                                                  "content1",
                                                                  "myContent");
        assertTrue(success);
    }

    @Test
    public void testAddContent() throws Exception {
        String content = "content";
        ByteArrayInputStream is = new ByteArrayInputStream(content.getBytes());
        boolean success = ContentResource.addContent("customer1",
                                                     "provider1",
                                                     "content1",
                                                     is,
                                                     "text/plain",
                                                     7);
        assertTrue(success);
    }

    @Test
    public void testDeleteContent() throws Exception {
        boolean success = ContentResource.deleteContent("customer1",
                                                        "provider1",
                                                        "content1");
        assertTrue(success);
    }

}