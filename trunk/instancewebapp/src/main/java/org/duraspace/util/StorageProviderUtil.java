package org.duraspace.util;

import java.util.HashMap;

import org.duraspace.s3storage.S3StorageProvider;
import org.duraspace.storage.StorageAccount;
import org.duraspace.storage.StorageCustomer;
import org.duraspace.storage.StorageException;
import org.duraspace.storage.StorageProvider;
import org.duraspace.storage.StorageAccount.AccountType;

/**
 * Performs storage provider services
 *
 * @author Bill Branan
 */
public class StorageProviderUtil {

    private static String mainHost = null;
    private static int mainPort = 0;

    private static HashMap<String, StorageCustomer> storageCustomers =
        new HashMap<String, StorageCustomer>();

    /**
     * Initializes the StorageProviderUtility with information that will
     * allow connections to be made to the main DuraSpace application.
     *
     * @param host - the host on which the main DuraSpace application resides
     * @param port - the port on which the REST API of the main DuraSpace
     *               application is available
     */
    public static void initialize(String host, int port) {
        if(host == null || host.equals("")) {
            throw new IllegalArgumentException("Host must be a valid web host");
        }

        if(port <= 0) {
            throw new IllegalArgumentException("Port must be a valid http port");
        }

        mainHost = host;
        mainPort = port;
    }

    /**
     * Retrieves the primary storage provider for a given customer.
     *
     * @param account
     * @return
     * @throws StorageException
     */
    public static StorageProvider getStorageProvider(String duraspaceAccountId)
    throws StorageException {
        return getStorageProvider(duraspaceAccountId, null);
    }

    /**
     * Retrieves a particular storage provider for a given customer.
     * If the a storage account cannot be retrieved based on the given
     * accountId, the primary storage provider account is used.
     *
     * @param account
     * @param accountId - the ID of the storage provider account
     * @return
     * @throws StorageException
     */
    public static StorageProvider getStorageProvider(String duraspaceAccountId,
                                                     String storageAccountId)
    throws StorageException {
        if(mainHost == null || mainPort == 0) {
            String error = "The Storage Provider Utility must be initilized " +
            		       "with a valid host and port at which the DuraSpace " +
            		       "Main webapp can be accessed before it can fulfill.";
            throw new StorageException(error);
        }

        StorageCustomer customer = null;
        if(storageCustomers.containsKey(duraspaceAccountId)) {
            customer = storageCustomers.get(duraspaceAccountId);
        } else {
            customer = new StorageCustomer(duraspaceAccountId, mainHost, mainPort);
            storageCustomers.put(duraspaceAccountId, customer);
        }

        StorageAccount account = customer.getStorageAccount(storageAccountId);
        if(storageAccountId == null) {
            account = customer.getPrimaryStorageAccount();
        }
        String username = account.getUsername();
        String password = account.getPassword();
        AccountType type = account.getType();

        StorageProvider storageProvider = null;
        if(type.equals(AccountType.S3)) {
            storageProvider = new S3StorageProvider(username, password);
        } else if(type.equals(AccountType.Azure)) {
            // TODO: Create Azure storage provider
        }

        return storageProvider;
    }

}
