
package org.duraspace.mainwebapp.domain.cmd;

import org.duraspace.mainwebapp.domain.model.StorageAcct;
import org.duraspace.mainwebapp.domain.model.StorageProvider;

public class StorageAcctWrapper {

    private StorageAcct storageAcct;

    private StorageProvider storageProvider;

    public StorageAcct getStorageAcct() {
        return storageAcct;
    }

    public void setStorageAcct(StorageAcct storageAcct) {
        this.storageAcct = storageAcct;
    }

    public StorageProvider getStorageProvider() {
        return storageProvider;
    }

    public void setStorageProvider(StorageProvider storageProvider) {
        this.storageProvider = storageProvider;
    }

}
