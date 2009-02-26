
package org.duraspace.mainwebapp.domain.repo;

import java.util.List;
import java.util.Properties;

import org.duraspace.mainwebapp.domain.model.StorageAcct;

public interface StorageAcctRepository {

    public List<StorageAcct> findStorageAccts(String customerId)
            throws Exception;

    public void saveStorageAcct(StorageAcct acct) throws Exception;

    public void setProperties(Properties props) throws Exception;

}
