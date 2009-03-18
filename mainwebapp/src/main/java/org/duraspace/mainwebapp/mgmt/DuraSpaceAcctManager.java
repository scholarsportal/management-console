
package org.duraspace.mainwebapp.mgmt;

import java.util.List;

import org.duraspace.common.model.Credential;
import org.duraspace.mainwebapp.domain.model.Address;
import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.duraspace.mainwebapp.domain.model.DuraSpaceAcct;
import org.duraspace.mainwebapp.domain.model.StorageAcct;
import org.duraspace.mainwebapp.domain.model.User;

public interface DuraSpaceAcctManager {

    public DuraSpaceAcct findDuraSpaceAccount(Credential duraCred)
            throws Exception;

    public ComputeAcct findComputeAccount(Credential duraCred) throws Exception;

    public List<StorageAcct> findStorageProviderAccounts(int duraAcctId)
            throws Exception;

    public int saveUser(User user) throws Exception;

    public int saveAddressForUser(Address addr, int userId) throws Exception;

    public int saveCredentialForUser(Credential cred, int userId)
            throws Exception;

    public int saveDuraAcct(DuraSpaceAcct duraAcct) throws Exception;

    public int saveComputeAcct(ComputeAcct computeAcct) throws Exception;

    public int saveStorageAcct(StorageAcct storageAcct) throws Exception;

    public int saveCredentialForStorageAcct(Credential storageAcctCred,
                                            int storageAcctId) throws Exception;

    public int saveCredentialForComputeAcct(Credential computeAcctCred,
                                            int computeAcctId) throws Exception;

}
