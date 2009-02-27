
package org.duraspace.mainwebapp.mgmt;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import org.duraspace.mainwebapp.domain.model.StorageAcct;
import org.duraspace.mainwebapp.domain.repo.StorageAcctRepository;

public class StorageManagerImpl
        implements StorageManager {

    protected final Logger log = Logger.getLogger(getClass());

    private StorageAcctRepository repo;

    /**
     * {@inheritDoc}
     */
    public List<StorageAcct> getStorageProviderAccounts(String customerId) {
        List<StorageAcct> accts = new ArrayList<StorageAcct>();
        try {
            accts = repo.findStorageAccts(customerId);
        } catch (Exception e) {
            log.error(e);
        }
        return accts;
    }

    public StorageAcctRepository getRepo() {
        return repo;
    }

    public void setRepo(StorageAcctRepository repo) {
        this.repo = repo;
    }

}
