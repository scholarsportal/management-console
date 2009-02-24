package org.duraspace.storage;

import org.duraspace.s3storage.S3StorageProvider;
import org.duraspace.storage.StorageAccount.AccountType;

/**
 * Performs storage provider services
 *
 * @author Bill Branan
 */
public class StorageProviderUtility {

    public static StorageProvider getStorageProvider(String customerId)
    throws StorageException {
        StorageCustomer customer = new StorageCustomer(customerId);
        StorageAccount primaryAccount = customer.getPrimaryStorageAccount();
        String username = primaryAccount.getUsername();
        String password = primaryAccount.getPassword();
        AccountType type = primaryAccount.getType();

        StorageProvider storageProvider = null;
        if(type.equals(AccountType.S3)) {
            storageProvider = new S3StorageProvider(username, password);
        }
        return storageProvider;
    }

}
