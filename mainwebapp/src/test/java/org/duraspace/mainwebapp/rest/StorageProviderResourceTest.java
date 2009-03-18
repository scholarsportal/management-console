
package org.duraspace.mainwebapp.rest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.duraspace.mainwebapp.domain.model.StorageAcct;
import org.duraspace.mainwebapp.mgmt.DuraSpaceAcctManager;
import org.easymock.EasyMock;

import junit.framework.TestCase;

/**
 * Tests storage provider functions.
 *
 * @author Bill Branan
 */
public class StorageProviderResourceTest
        extends TestCase {

    private final Integer duraAcctId = 123;

    private final int storageAcctId = 111;

    private final StorageAcct storageAcct = new StorageAcct();

    private final List<StorageAcct> storageAccts = new ArrayList<StorageAcct>();

    @Override
    @Before
    public void setUp() throws Exception {

        storageAcct.setId(storageAcctId);
        storageAccts.add(storageAcct);

        DuraSpaceAcctManager mgr =
                EasyMock.createMock(DuraSpaceAcctManager.class);
        EasyMock.expect(mgr.findStorageProviderAccounts(duraAcctId))
                .andReturn(storageAccts);
        EasyMock.replay(mgr);

        StorageProviderResource.setDuraSpaceAcctManager(mgr);
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
                StorageProviderResource.getStorageProviderAccounts(duraAcctId
                        .toString());

        assertNotNull(xml);

        String idElement = "<id>" + storageAcctId + "</id>";
        assertTrue(xml.indexOf(idElement) > 0);
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