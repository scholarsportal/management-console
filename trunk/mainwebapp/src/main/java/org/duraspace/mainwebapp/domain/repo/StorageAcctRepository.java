
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
     * This method returns all customer-storage-accounts that belong to the
     * customer associated with the provided id.
     *
     * @param customerId
     * @return
     * @throws Exception
     *         If no accounts are found.
     */
    public List<StorageAcct> findStorageAccts(String customerId)
            throws Exception;

    /**
     * This method persists the provided storage-account.
     *
     * @param acct
     * @throws Exception
     */
    public void saveStorageAcct(StorageAcct acct) throws Exception;

}
