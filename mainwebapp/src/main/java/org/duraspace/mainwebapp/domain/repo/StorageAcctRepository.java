
package org.duraspace.mainwebapp.domain.repo;

import java.util.List;

import org.duraspace.mainwebapp.domain.model.StorageAcct;

/**
 * <pre>
 * This interface exposes access to the underlying persistence of
 * customer storage accounts.
 * </pre>
 *
 * @author Andrew Woods
 */
public interface StorageAcctRepository {

    /**
     * This method returns storage-account associated with the provided storage
     * acct id.
     *
     * @param Storage Acct Id
     * @return
     * @throws Exception
     *         If no accounts are found.
     */
    public StorageAcct findStorageAcctById(int storageAcctId)
            throws Exception;

    /**
     * This method returns all storage-accounts associated with the provided
     * DuraSpace id.
     *
     * @param DuraSpace Account id
     * @return
     * @throws Exception
     *         If no accounts are found.
     */
    public List<StorageAcct> findStorageAcctsByDuraAcctId(int duraAcctId)
            throws Exception;

    /**
     * This method persists the provided storage-account.
     *
     * @param acct
     * @throws Exception
     */
    public int saveStorageAcct(StorageAcct acct) throws Exception;

}
