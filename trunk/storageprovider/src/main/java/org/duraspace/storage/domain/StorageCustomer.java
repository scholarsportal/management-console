package org.duraspace.storage.domain;

import java.io.InputStream;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import org.duraspace.common.util.EncryptionUtil;
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

    private String primaryStorageProviderId = null;
    private HashMap<String, StorageAccount> storageAccounts = null;

    public StorageCustomer(InputStream accountXml)
    throws StorageException {
        storageAccounts = new HashMap<String, StorageAccount>();

        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(accountXml);
            Element accounts = doc.getRootElement();

            Iterator<?> accountList = accounts.getChildren().iterator();
            while(accountList.hasNext()) {
                Element account = (Element)accountList.next();

                String storageAccountId = account.getChildText("id");
                String type = account.getChildText("storageProviderType");
                Element credentials = account.getChild("storageProviderCredential");
                String encUsername = credentials.getChildText("username");
                String encPassword = credentials.getChildText("password");

                EncryptionUtil encryptUtil = new EncryptionUtil();
                String username = encryptUtil.decrypt(encUsername);
                String password = encryptUtil.decrypt(encPassword);

                StorageAccount storageAccount = null;
                StorageProviderType storageAccountType = null;
                if(type.equals("AMAZON_S3")) {
                    storageAccountType = StorageProviderType.AMAZON_S3;
                } else if(type.equals("MS_AZURE")) {
                    storageAccountType = StorageProviderType.MICROSOFT_AZURE;
                } else if(type.equals("SUN")) {
                    storageAccountType = StorageProviderType.SUN;
                } else if(type.equals("RACKSPACE")) {
                    storageAccountType = StorageProviderType.RACKSPACE;
                } else if(type.equals("EMC")) {
                    storageAccountType = StorageProviderType.EMC;
                }

                if(storageAccountType != null) {
                    storageAccount = new StorageAccount(storageAccountId,
                                                        username,
                                                        password,
                                                        storageAccountType);
                    storageAccounts.put(storageAccountId, storageAccount);
                }
                else {
                    log.warn("While creating storage account list, skipping storage " +
                    		 "account with storageAccountId '" + storageAccountId +
                             "' due to an unsupported type '" + type + "'");
                }

            String primary = account.getAttributeValue("isPrimary");
            if(primary != null && primary.equalsIgnoreCase("1")) {
                    primaryStorageProviderId = storageAccountId;
                }
            }

            // Make sure that there is at least one storage account
            if(storageAccounts.isEmpty()) {
                String error = "No storage accounts could be read";
                throw new StorageException(error);
            } else {
                // Make sure a primary provider is set
                if(primaryStorageProviderId == null) {
                    primaryStorageProviderId =
                        storageAccounts.values().iterator().next().getId();
                }
            }
        } catch (Exception e) {
            String error = "Unable to build storage account information due " +
            		       "to error: " + e.getMessage();
            log.error(error);
            throw new StorageException(error, e);
        }
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
