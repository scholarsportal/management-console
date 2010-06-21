/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.mgmt;

import java.util.List;

import org.duracloud.common.model.Credential;
import org.duracloud.mainwebapp.domain.model.Address;
import org.duracloud.mainwebapp.domain.model.ComputeAcct;
import org.duracloud.mainwebapp.domain.model.DuraCloudAcct;
import org.duracloud.mainwebapp.domain.model.StorageAcct;
import org.duracloud.mainwebapp.domain.model.User;

public interface DuraCloudAcctManager {

    public void verifyCredential(Credential cred) throws Exception;

    public List<User> findUsers(int duraAcctId) throws Exception;

    public List<ComputeAcct> findComputeAccounts(int duraAcctId)
            throws Exception;

    public DuraCloudAcct findDuraCloudAccount(Credential duraCred)
            throws Exception;

    // TODO:awoods :remove this method
    public List<ComputeAcct> findComputeAccounts(Credential duraCred)
            throws Exception;

    public List<StorageAcct> findStorageAccounts(int duraAcctId)
            throws Exception;

    public int saveUser(User user) throws Exception;

    public int saveAddressForUser(Address addr, int userId) throws Exception;

    public int saveCredentialForUser(Credential cred, int userId)
            throws Exception;

    public int saveDuraAcctForUser(DuraCloudAcct duraAcct, int userId)
            throws Exception;

    public int saveComputeAcct(ComputeAcct computeAcct) throws Exception;

    public int saveStorageAcct(StorageAcct storageAcct) throws Exception;

    public int saveCredentialForStorageAcct(Credential storageAcctCred,
                                            int storageAcctId) throws Exception;

    public int saveCredentialForComputeAcct(Credential computeAcctCred,
                                            int computeAcctId) throws Exception;

    public DuraCloudAcct findDuraCloudAccountByAcctName(String acctName)
            throws Exception;

    public DuraCloudAcct findDuraCloudAcctById(int duraAcctId) throws Exception;

    public boolean isComputeNamespaceTaken(String computeAcctNamespace);

    public boolean isStorageNamespaceTaken(String storageAcctNamespace);

}
