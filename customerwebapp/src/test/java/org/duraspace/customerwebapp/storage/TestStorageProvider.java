package org.duraspace.customerwebapp.storage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import org.duraspace.common.util.EncryptionUtil;
import org.duraspace.customerwebapp.util.StorageProviderFactory;
import org.duraspace.storage.domain.StorageAccount;
import org.duraspace.storage.domain.StorageCustomer;
import org.duraspace.storage.domain.StorageProviderType;
import org.duraspace.storage.provider.BrokeredStorageProvider;
import org.duraspace.storage.provider.StorageProvider;

/**
 * Runtime test of Storage Provider classes.
 *
 * @author Bill Branan
 */
public class TestStorageProvider
        extends TestCase {

    private static String accountXml;

    @Override
    @Before
    public void setUp() throws Exception {
        StringBuilder xml = new StringBuilder();
        xml.append("<storageProviderAccounts>");
        xml.append("  <storageAcct ownerId='0' isPrimary='1'>");
        xml.append("    <id>0</id>");
        xml.append("    <storageProviderType>AMAZON_S3</storageProviderType>");
        xml.append("    <storageProviderCredential>");
        EncryptionUtil encryptUtil = new EncryptionUtil();
        String username = encryptUtil.encrypt("username");
        xml.append("      <username>"+username+"</username>");
        String password = encryptUtil.encrypt("password");
        xml.append("      <password>"+password+"</password>");
        xml.append("    </storageProviderCredential>");
        xml.append("  </storageAcct>");
        xml.append("</storageProviderAccounts>");
        accountXml = xml.toString();
    }

    @Test
    public void testStorageCustomer() throws Exception {
        InputStream is = new ByteArrayInputStream(accountXml.getBytes());
        StorageCustomer customer = new StorageCustomer(is);
        assertNotNull(customer);

        StorageAccount primary = customer.getPrimaryStorageAccount();
        assertNotNull(primary);
        assertNotNull(primary.getUsername());
        assertEquals("username", primary.getUsername());
        assertNotNull(primary.getPassword());
        assertEquals("password", primary.getPassword());
        assertEquals(primary.getType(), StorageProviderType.AMAZON_S3);
    }

    @Test
    public void testStorageProviderUtility() throws Exception {
        InputStream is = new ByteArrayInputStream(accountXml.getBytes());
        StorageProviderFactory.initialize(is);
        StorageProvider storage =
            StorageProviderFactory.getStorageProvider();

        assertNotNull(storage);
        assertTrue(storage instanceof BrokeredStorageProvider);
    }

 }