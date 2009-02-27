
package org.duraspace.mainwebapp.mgmt;

import java.util.List;

import org.duraspace.mainwebapp.domain.model.StorageAcct;

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
public interface StorageManager {

    /**
     * This method returns a list of storage-provider-accounts associated with
     * the provided customer id.
     *
     * @param customerId
     * @return
     */
    public List<StorageAcct> getStorageProviderAccounts(String customerId);

}