package org.duraspace.storage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.duraspace.common.web.RestHttpHelper;
import org.duraspace.common.web.RestHttpHelper.HttpResponse;
import org.duraspace.storage.StorageAccount.AccountType;

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

    public StorageCustomer(String customerId)
    throws StorageException {
        this.customerId = customerId;
        storageAccounts = new HashMap<String, StorageAccount>();

        /* TODO:
         * - Get a list of the storage accounts for this customer from DuraSpace.org
         * - Parse XML response and create StorageAccount objects
         * - Set the primary storage provider id
         */

        // For now, create a storage account for testing
        String accountId = "duraspace-test-s3-account";
        primaryStorageProviderId = accountId;

        RestHttpHelper restHelper = new RestHttpHelper();
        String url = "http://localhost:8080/awsCredentials";

        String accessKey = null;
        String secretKey = null;
        try {
            HttpResponse response = restHelper.get(url);
            if(404 == response.getStatusCode()) {
                throw new Exception();
            }

            String credentials = response.getResponseBody();
            credentials = credentials.trim();
            String[] credentialParts = credentials.split(":");
            if(credentialParts.length > 2) {
                throw new Exception();
            }

            accessKey = credentialParts[0];
            secretKey = credentialParts[1];
        } catch (Exception e) {
            String error =
                "Unable to retrieve credentials for Amazon S3 Account. " +
            	"While the integration between the DuraSpace instance and " +
            	"the DuraSpace.org site is in work you must add a file that " +
            	"is accessable at this URL: http://localhost:8080/awsCredentials. " +
            	"This file should include on a single line: Your Amazon Access Key ID, " +
            	"followed by a colon, followed by your Amazon Secret Access Key.";
            throw new StorageException(error);
        }

        StorageAccount storageAccount =
            new StorageAccount(accountId, accessKey, secretKey, AccountType.S3);
        storageAccounts.put(accountId, storageAccount);
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
