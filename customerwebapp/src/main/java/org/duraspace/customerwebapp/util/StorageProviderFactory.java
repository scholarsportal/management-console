
package org.duraspace.customerwebapp.util;

import java.util.HashMap;

import org.duraspace.rackspacestorage.RackspaceStorageProvider;
import org.duraspace.s3storage.S3StorageProvider;
import org.duraspace.storage.domain.StorageAccount;
import org.duraspace.storage.domain.StorageCustomer;
import org.duraspace.storage.domain.StorageException;
import org.duraspace.storage.domain.StorageProviderType;
import org.duraspace.storage.provider.BrokeredStorageProvider;
import org.duraspace.storage.provider.StatelessStorageProvider;
import org.duraspace.storage.provider.StorageProvider;

/**
 * Performs storage provider services
 *
 * @author Bill Branan
 */
public class StorageProviderFactory {

    private static String mainHost = null;

    private static int mainPort = 0;

    private static StatelessStorageProvider statelessProvider;

    private static HashMap<String, StorageCustomer> storageCustomers =
            new HashMap<String, StorageCustomer>();

    /**
     * Initializes the StorageProviderUtility with information that will allow
     * connections to be made to the main DuraSpace application.
     *
     * @param host
     *        - the host on which the main DuraSpace application resides
     * @param port
     *        - the port on which the REST API of the main DuraSpace application
     *        is available
     */
    public static void initialize(String host, int port) {
        if (host == null || host.equals("")) {
            throw new IllegalArgumentException("Host must be a valid web host");
        }

        if (port <= 0) {
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
     * Retrieves a particular storage provider for a given customer. If the a
     * storage account cannot be retrieved based on the given accountId, the
     * primary storage provider account is used.
     *
     * @param account
     * @param accountId
     *        - the ID of the storage provider account
     * @return
     * @throws StorageException
     */
    public static StorageProvider getStorageProvider(String duraspaceAccountId,
                                                     String storageAccountId)
            throws StorageException {
        if (mainHost == null || mainPort == 0) {
            String error =
                    "The Storage Provider Utility must be initilized "
                            + "with a valid host and port at which the DuraSpace "
                            + "Main webapp can be accessed before it can fulfill "
                            + "any requests.";
            throw new StorageException(error);
        }

        StorageCustomer customer = null;
        if (storageCustomers.containsKey(duraspaceAccountId)) {
            customer = storageCustomers.get(duraspaceAccountId);
        } else {
            customer =
                    new StorageCustomer(duraspaceAccountId, mainHost, mainPort);
            storageCustomers.put(duraspaceAccountId, customer);
        }

        StorageAccount account = customer.getStorageAccount(storageAccountId);
        if (storageAccountId == null) {
            account = customer.getPrimaryStorageAccount();
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
            // TODO: Create Sun storage provider
        } else if (type.equals(StorageProviderType.RACKSPACE)) {
            storageProvider = new RackspaceStorageProvider(username, password);
        } else if (type.equals(StorageProviderType.EMC)) {
            // TODO: Create EMC storage provider
        }

        return new BrokeredStorageProvider(statelessProvider, storageProvider);
    }

    public StatelessStorageProvider getStatelessProvider() {
        return statelessProvider;
    }

    public static void setStatelessProvider(StatelessStorageProvider statelessProvider) {
        StorageProviderFactory.statelessProvider = statelessProvider;
    }

}
