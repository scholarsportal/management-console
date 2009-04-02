
package org.duraspace.mainwebapp.rest;

import java.util.ArrayList;
import java.util.List;

import org.duraspace.common.model.Credential;
import org.duraspace.common.util.EncryptionUtil;
import org.duraspace.mainwebapp.domain.model.StorageAcct;
import org.duraspace.mainwebapp.mgmt.DuraSpaceAcctManager;

/**
 * Provides interaction with storage provider accounts
 *
 * @author Bill Branan
 */
public class StorageProviderResource {

    private static DuraSpaceAcctManager duraSpaceAcctManager;

    /**
     * Provides a listing of all storage providers that are available to
     * DuraSpace users. No customer-specific information is included.
     *
     * @return XML listing of storage providers
     */
    public static String getStorageProviders() {
        String xml = "<storageProviders />";
        return xml;
    }

    /**
     * Provides a listing of all storage provider account subscriptions for a
     * given DuraSpace account.
     *
     * @param DuraSpace
     *        account name
     * @return XML listing of storage provider accounts
     */
    public static String getStorageProviderAccounts(String duraAcctId) {
        List<StorageAcct> accts = new ArrayList<StorageAcct>();
        try {
            int id = Integer.parseInt(duraAcctId);
            accts = duraSpaceAcctManager.findStorageAccounts(id);

            EncryptionUtil encryptUtil = new EncryptionUtil();
            for(StorageAcct acct : accts) {
                Credential credential = acct.getStorageProviderCredential();
                if(credential != null) {
                    String encUsername = encryptUtil.encrypt(credential.getUsername());
                    String encPassword = encryptUtil.encrypt(credential.getPassword());
                    credential.setUsername(encUsername);
                    credential.setPassword(encPassword);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        StringBuilder xml = new StringBuilder("<storageProviderAccounts>");
        for (StorageAcct acct : accts) {
            xml.append(acct.toXml());
        }
        xml.append("</storageProviderAccounts>");

        return xml.toString();
    }

    /**
     * Provides information regarding a particular storage provider account
     * subscription.
     *
     * @param customerID
     * @param storageProviderID
     * @return XML storage provider account information
     */
    public static String getStorageProviderAccount(String customerID,
                                                   String storageProviderID) {
        String xml = "<storageProviderAccount />";
        return xml;
    }

    /**
     * Adds a storage provider account subscription for a customer.
     *
     * @param customerID
     * @param storageProviderID
     * @return success
     */
    public static boolean addStorageProviderAccount(String customerID,
                                                    String storageProviderID) {
        return true;
    }

    /**
     * Closes a storage provider account subscription for a customer. The actual
     * account with the storage provider is retained, but all content is removed
     * from storage and all services to replicate or otherwise handle content in
     * the storage provider are suspended.
     *
     * @param customerID
     * @param storageProviderID
     * @return success
     */
    public static boolean closeStorageProviderAccount(String customerID,
                                                      String storageProviderID) {
        return true;
    }

    /**
     * Sets a storage provider as the primary provider for a customer. If this
     * provider was already the primary, nothing happens, if another provider
     * was primary it is replaced.
     *
     * @param customerID
     * @param storageProviderID
     * @return success
     */
    public static boolean setPrimaryStorageProvider(String customerID,
                                                    String storageProviderID) {
        return true;
    }

    public DuraSpaceAcctManager getDuraSpaceAcctManager() {
        return duraSpaceAcctManager;
    }

    public static void setDuraSpaceAcctManager(DuraSpaceAcctManager duraSpaceAcctManager) {
        StorageProviderResource.duraSpaceAcctManager = duraSpaceAcctManager;
    }

}
