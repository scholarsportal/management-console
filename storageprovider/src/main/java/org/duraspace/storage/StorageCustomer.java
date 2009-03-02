package org.duraspace.storage;

import java.net.URL;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;

import org.duraspace.storage.StorageAccount.AccountType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Provides a starting point for dealing with a customer's
 * content storage.
 *
 * @author Bill Branan
 */
public class StorageCustomer {

    protected final Logger log = Logger.getLogger(getClass());

    private String customerId = null;
    private String primaryStorageProviderId = null;
    private HashMap<String, StorageAccount> storageAccounts = null;

    public StorageCustomer(String customerId, String host, int port)
    throws StorageException {
        this.customerId = customerId;
        storageAccounts = new HashMap<String, StorageAccount>();

        try {
            URL url = new URL("http", host, port, "/mainwebapp/storage/" + customerId);
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(url);
            Element accounts = doc.getRootElement();

            Iterator<?> accountList = accounts.getChildren().iterator();
            while(accountList.hasNext()) {
                Element account = (Element)accountList.next();
                String accountId = account.getChildText("storageProviderId");
                // TODO: Storage provider type should be in a <storageProviderType> tag
                String type = account.getChildText("storageProviderId");
                Element credentials = account.getChild("storageProviderCred");
                String username = credentials.getChildText("username");
                String password = credentials.getChildText("password");

                StorageAccount storageAccount = null;
                if(type.equals("amazon-s3")) {
                    storageAccount =
                        new StorageAccount(accountId,
                                           username,
                                           password,
                                           AccountType.S3);
                    storageAccounts.put(accountId, storageAccount);
                } else if(type.equals("ms-azure")) {
                    storageAccount =
                        new StorageAccount(accountId,
                                           username,
                                           password,
                                           AccountType.Azure);
                    storageAccounts.put(accountId, storageAccount);
                } else {
                    log.warn("While creating storage account list for customer '" +
                             customerId + "' skipping storage account with accountId '" +
                             accountId + "' due to an unsupported type '" + type + "'");
                }

                String primary = account.getAttributeValue("isPrimary");
                if(primary.equalsIgnoreCase("true")) {
                    primaryStorageProviderId = accountId;
                }
            }
        } catch (Exception e) {
            String error = "Unable to retrieve storage account information for '" +
                           customerId + "' due to error" + e.getMessage();
            throw new StorageException(error, e);
        }
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
