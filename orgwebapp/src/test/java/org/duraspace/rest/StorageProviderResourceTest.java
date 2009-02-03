package org.duraspace.rest;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Tests storage provider functions.
 *
 * @author Bill Branan
 */
public class StorageProviderResourceTest
        extends TestCase {

    @Test
    public void testGetStorageProviders() throws Exception {
        String xml = StorageProviderResource.getStorageProviders();

        assertNotNull(xml);
        assertEquals(xml, "<storageProviders />");
    }

    @Test
    public void testGetStorageProviderAccounts() throws Exception {
        String xml = StorageProviderResource.
            getStorageProviderAccounts("customer1");

        assertNotNull(xml);
        assertEquals(xml, "<storageProviderAccounts />");
    }

    @Test
    public void testGetStorageProviderAccount() throws Exception {
        String xml = StorageProviderResource.
            getStorageProviderAccount("customer1", "provider1");

        assertNotNull(xml);
        assertEquals(xml, "<storageProviderAccount />");
    }

    @Test
    public void testAddStorageProviderAccount() throws Exception {
        boolean success = StorageProviderResource.
            addStorageProviderAccount("customer1", "provider1");

        assertTrue(success);
    }

    @Test
    public void testCloseStorageProviderAccount() throws Exception {
        boolean success = StorageProviderResource.
            closeStorageProviderAccount("customer1", "provider1");

        assertTrue(success);
    }

    @Test
    public void testSetPrimaryStorageProvider() throws Exception {
        boolean success = StorageProviderResource.
            setPrimaryStorageProvider("customer1", "provider1");

        assertTrue(success);
    }
}