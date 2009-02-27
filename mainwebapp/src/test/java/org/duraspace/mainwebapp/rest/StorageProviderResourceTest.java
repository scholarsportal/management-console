
package org.duraspace.mainwebapp.rest;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import org.duraspace.mainwebapp.domain.repo.StorageAcctRepositoryFileImpl;
import org.duraspace.mainwebapp.mgmt.StorageManagerImpl;

import junit.framework.TestCase;

/**
 * Tests storage provider functions.
 *
 * @author Bill Branan
 */
public class StorageProviderResourceTest
        extends TestCase {

    private final String repoLocationKey =
            StorageAcctRepositoryFileImpl.REPO_LOCATION;

    private final String repoLocation = "testStorageAcctRepo.xml";

    @Override
    @Before
    public void setUp() throws Exception {

        Properties props = new Properties();
        props.put(repoLocationKey, repoLocation);

        StorageAcctRepositoryFileImpl repo = new StorageAcctRepositoryFileImpl();
        repo.setProperties(props);

        StorageManagerImpl mgr = new StorageManagerImpl();
        mgr.setRepo(repo);
        StorageProviderResource.setStorageManager(mgr);
    }

    @Test
    public void testGetStorageProviders() throws Exception {
        String xml = StorageProviderResource.getStorageProviders();

        assertNotNull(xml);
        assertEquals(xml, "<storageProviders />");
    }

    @Test
    public void testGetStorageProviderAccounts() throws Exception {
        String xml =
                StorageProviderResource.getStorageProviderAccounts("customer1");

        StringBuilder sb = new StringBuilder();
        sb.append("<storageProviderAccounts>");
        sb.append("<storageAcct ownerId=\"customer1\" isPrimary=\"true\">");
        sb.append("<storageProviderId>amazon-s3</storageProviderId>");
        sb.append("<storageProviderCred>");
        sb.append("<username>usernameA</username>");
        sb.append("<password>passwordA</password>");
        sb.append("</storageProviderCred>");
        sb.append("</storageAcct>");
        sb.append("</storageProviderAccounts>");

        assertNotNull(xml);
        assertEquals(xml, sb.toString());
    }

    @Test
    public void testGetStorageProviderAccount() throws Exception {
        String xml =
                StorageProviderResource.getStorageProviderAccount("customer1",
                                                                  "provider1");

        assertNotNull(xml);
        assertEquals(xml, "<storageProviderAccount />");
    }

    @Test
    public void testAddStorageProviderAccount() throws Exception {
        boolean success =
                StorageProviderResource.addStorageProviderAccount("customer1",
                                                                  "provider1");

        assertTrue(success);
    }

    @Test
    public void testCloseStorageProviderAccount() throws Exception {
        boolean success =
                StorageProviderResource
                        .closeStorageProviderAccount("customer1", "provider1");

        assertTrue(success);
    }

    @Test
    public void testSetPrimaryStorageProvider() throws Exception {
        boolean success =
                StorageProviderResource.setPrimaryStorageProvider("customer1",
                                                                  "provider1");

        assertTrue(success);
    }
}