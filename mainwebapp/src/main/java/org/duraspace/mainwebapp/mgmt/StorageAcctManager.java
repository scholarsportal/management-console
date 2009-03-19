
package org.duraspace.mainwebapp.mgmt;

import java.util.List;

import org.duraspace.common.model.Credential;
import org.duraspace.mainwebapp.domain.model.StorageAcct;
import org.duraspace.mainwebapp.domain.model.StorageProvider;

/**
 * <pre>
 * This interface encapsulates the implementation of:
 *  -navigating the customer-account repositories and
 *  -applying business logic / exception handling.
 *
 * </pre>
 *
 * @author Andrew Woods
 */
public interface StorageAcctManager {

    /**
     * This method returns a list of storage-provider-accounts associated with
     * the provided DuraSpace Account ID.
     *
     * @param DuraSpace
     *        Account Id
     * @return
     */
    public List<StorageAcct> getStorageProviderAccountsByDuraAcctId(int duraAcctId);

    public int saveStorageAcct(StorageAcct storageAcct) throws Exception;

    public int saveCredentialForStorageAcct(Credential storageAcctCred,
                                            int storageAcctId) throws Exception;

    public StorageProvider findStorageProviderForStorageAcct(int storageAcctId)
            throws Exception;

    public StorageAcct findStorageAccountAndLoadCredential(int storageAcctId)
            throws Exception;

}