package org.duraspace.customerwebapp.storage;

import org.junit.Before;
import org.junit.Test;

import org.duraspace.customerwebapp.config.CustomerWebAppConfig;
import org.duraspace.customerwebapp.util.StorageProviderFactory;
import org.duraspace.storage.domain.StorageAccount;
import org.duraspace.storage.domain.StorageCustomer;
import org.duraspace.storage.domain.StorageProviderType;
import org.duraspace.storage.provider.BrokeredStorageProvider;
import org.duraspace.storage.provider.StorageProvider;

import junit.framework.TestCase;

/**
 * Runtime test of Storage Provider classes. The mainwebapp
 * web application must be deployed and available at the
 * default host and port in order for these tests to pass.
 *
 * @author Bill Branan
 */
public class TestStorageProvider
        extends TestCase {

    private static String configFileName = "test-customerwebapp.properties";
    private static String mainHost = "localhost";
    private static int mainPort;

    @Override
    @Before
    public void setUp() throws Exception {
        CustomerWebAppConfig.setConfigFileName(configFileName);
        mainPort = Integer.parseInt(CustomerWebAppConfig.getPort());
    }

    @Test
    public void testStorageCustomer() throws Exception {
        StorageCustomer customer = new StorageCustomer("1",
                                                       mainHost,
                                                       mainPort);
        assertNotNull(customer);

        StorageAccount primary = customer.getPrimaryStorageAccount();
        assertNotNull(primary);
        assertNotNull(primary.getUsername());
        assertNotNull(primary.getPassword());
        assertEquals(primary.getType(), StorageProviderType.AMAZON_S3);
    }

    @Test
    public void testStorageProviderUtility() throws Exception {
        StorageProviderFactory.initialize(mainHost, mainPort);
        StorageProvider storage =
            StorageProviderFactory.getStorageProvider("1");

        assertNotNull(storage);
        assertTrue(storage instanceof BrokeredStorageProvider);
    }

 }