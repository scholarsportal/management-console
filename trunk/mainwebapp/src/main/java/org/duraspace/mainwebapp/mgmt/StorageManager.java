
package org.duraspace.mainwebapp.mgmt;

import java.util.List;

import org.duraspace.mainwebapp.domain.model.StorageAcct;

public interface StorageManager {

    public List<StorageAcct> getStorageProviderAccounts(String customerId);

}