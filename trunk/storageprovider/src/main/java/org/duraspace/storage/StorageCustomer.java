package org.duraspace.storage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Provides a starting point for dealing with a customer's
 * content storage.
 *
 * @author Bill Branan
 */
public class StorageCustomer {

    private String customerId = null;
    private String primaryStorageProviderId = null;
    private HashMap<String, StorageAccount> storageAccounts = null;

    public StorageCustomer(String customerId) {
        this.customerId = customerId;

        /* TODO:
         * - Get a list of the storage accounts for this customer from DuraSpace.org
         * - Parse XML response and create StorageAccount objects
         * - Set the primary storage provider id
         */
    }

    public String getCustomerId() {
        return customerId;
    }

    public StorageAccount getPrimaryStorageAccount() {
        return getStorageAccount(primaryStorageProviderId);
    }

    public Iterator<String> getStorageAccountIds() {
        return storageAccounts.keySet().iterator();
    }

    public StorageAccount getStorageAccount(String storageProviderId) {
        return storageAccounts.get(storageProviderId);
    }

    public Map<String, StorageAccount> getStorageAccounts() {
        return storageAccounts;
    }
}
