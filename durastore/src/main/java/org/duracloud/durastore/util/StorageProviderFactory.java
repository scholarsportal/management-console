package org.duracloud.durastore.util;

import java.io.InputStream;

import java.util.Iterator;

import org.duracloud.emcstorage.EMCStorageProvider;
import org.duracloud.rackspacestorage.RackspaceStorageProvider;
import org.duracloud.s3storage.S3StorageProvider;
import org.duracloud.storage.domain.StorageAccount;
import org.duracloud.storage.domain.StorageAccountManager;
import org.duracloud.storage.domain.StorageException;
import org.duracloud.storage.domain.StorageProviderType;
import org.duracloud.storage.provider.BrokeredStorageProvider;
import org.duracloud.storage.provider.StatelessStorageProvider;
import org.duracloud.storage.provider.StorageProvider;

/**
 * Performs storage provider services
 *
 * @author Bill Branan
 */
public class StorageProviderFactory {

    private static StatelessStorageProvider statelessProvider;

    private static StorageAccountManager storageAccountManager;

    /**
     * Initializes the StorageProviderUtility with account information
     * necessary to connect to Storage Providers.
     *
     * @param accountXml A stream containing account information in XML format
     */
    public static void initialize(InputStream accountXml)
            throws StorageException {
        if (accountXml == null) {
            throw new IllegalArgumentException("XML containing account information");
        }

        storageAccountManager = new StorageAccountManager(accountXml);
    }

    /**
     * Retrieves the ids for all available storage provider accounts
     *
     * @return
     * @throws StorageException
     */
    public static Iterator<String> getStorageProviderAccountIds()
            throws StorageException {
        checkInitialized();
        return storageAccountManager.getStorageAccountIds();
    }

    /**
     * Retrieves the id for the primary storage provider account
     *
     * @return
     * @throws StorageException
     */
    public static String getPrimaryStorageProviderAccountId()
            throws StorageException {
        checkInitialized();
        return storageAccountManager.getPrimaryStorageAccountId();
    }

    /**
     * Retrieves the primary storage provider for a given customer.
     *
     * @param account
     * @return
     * @throws StorageException
     */
    public static StorageProvider getStorageProvider()
            throws StorageException {
        return getStorageProvider(null);
    }

    /**
     * Retrieves a particular storage provider based on the storage account ID.
     * If a storage account cannot be retrieved, the primary storage provider
     * account is used.
     *
     * @param account
     * @param accountId - the ID of the storage provider account
     * @return
     * @throws StorageException
     */
    public static StorageProvider getStorageProvider(String storageAccountId)
            throws StorageException {
        checkInitialized();
        StorageAccount account =
            storageAccountManager.getStorageAccount(storageAccountId);
        if (account == null) {
            account = storageAccountManager.getPrimaryStorageAccount();
            storageAccountId = storageAccountManager.getPrimaryStorageAccountId();
        }
        String username = account.getUsername();
        String password = account.getPassword();
        StorageProviderType type = account.getType();

        StorageProvider storageProvider = null;
        if (type.equals(StorageProviderType.AMAZON_S3)) {
            storageProvider = new S3StorageProvider(username, password);
        } else if (type.equals(StorageProviderType.MICROSOFT_AZURE)) {
            // TODO: Create Azure storage provider
        } else if (type.equals(StorageProviderType.SUN)) {
            // TODO: Enable this when Sun provider is working
            // storageProvider = new SunStorageProvider(username, password);
        } else if (type.equals(StorageProviderType.RACKSPACE)) {
            storageProvider = new RackspaceStorageProvider(username, password);
        } else if (type.equals(StorageProviderType.EMC)) {
            storageProvider = new EMCStorageProvider(username, password);
        }

        return new BrokeredStorageProvider(statelessProvider,
                                           storageProvider,
                                           storageAccountId);
    }

    /**
     * Returns the type of the storage provider with the given account ID. If
     * no storage provider is available with that ID, the UNKNOWN type is returned.
     *
     * @param storageAccountId
     * @return
     * @throws StorageException
     */
    public static StorageProviderType getStorageProviderType(String storageAccountId)
            throws StorageException {
        checkInitialized();
        StorageAccount account =
            storageAccountManager.getStorageAccount(storageAccountId);
        if(account != null) {
            return account.getType();
        } else {
            return StorageProviderType.UNKNOWN;
        }
    }

    public StatelessStorageProvider getStatelessProvider() {
        return statelessProvider;
    }

    public static void setStatelessProvider(StatelessStorageProvider statelessProvider) {
        StorageProviderFactory.statelessProvider = statelessProvider;
    }

    private static void checkInitialized()
    throws StorageException {
        if (storageAccountManager == null) {
            String error =
                    "The Storage Provider Utility must be initilized with an " +
                    "XML file containing storage account information before it " +
                    "can fulfill any requests.";
            throw new StorageException(error);
        }
    }

}
