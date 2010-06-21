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
import org.duracloud.mainwebapp.domain.repo.DuraCloudAcctRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DuraCloudAcctManagerImpl
        implements DuraCloudAcctManager {

    protected static final Logger log =
            LoggerFactory.getLogger(DuraCloudAcctManagerImpl.class);

    private ComputeAcctManager computeAcctManager;

    private StorageAcctManager storageAcctManager;

    private DuraCloudAcctRepository duraCloudAcctRepository;

    private UserManager userManager;

    public void verifyCredential(Credential duraCred) throws Exception {
        try {
            getUserManager().findUser(duraCred);
        } catch (Exception e) {
            log.info("Credential validation failed: " + duraCred);
            throw e;
        }
    }

    public List<ComputeAcct> findComputeAccounts(int duraAcctId)
            throws Exception {
        return getComputeAcctManager()
                .findComputeAccountsByDuraAcctId(duraAcctId);
    }

    public List<User> findUsers(int duraAcctId) throws Exception {
        return getUserManager().findUsersByDuraAcctId(duraAcctId);
    }

    public DuraCloudAcct findDuraCloudAccount(Credential duraCred)
            throws Exception {
        User user = this.getUserManager().findUser(duraCred);
        int duraAcctId = user.getDuraAcctId();

        return this.findDuraCloudAcctById(duraAcctId);
    }

    public DuraCloudAcct findDuraCloudAcctById(int duraAcctId) throws Exception {
        return getDuraCloudAcctRepository().findDuraAcctById(duraAcctId);
    }

    public List<ComputeAcct> findComputeAccounts(Credential duraCred)
            throws Exception {
        DuraCloudAcct duraAcct = findDuraCloudAccount(duraCred);

        return getComputeAcctManager().findComputeAccountsByDuraAcctId(duraAcct
                .getId());
    }

    public List<StorageAcct> findStorageAccounts(int duraAcctId)
            throws Exception {
        log.info("finding storage provider accts for id: " + duraAcctId);
        return getStorageAcctManager()
                .getStorageProviderAccountsByDuraAcctId(duraAcctId);

    }

    public DuraCloudAcct findDuraCloudAccountByAcctName(String acctName)
            throws Exception {
        return getDuraCloudAcctRepository().findDuraAcctByName(acctName);
    }

    public int saveUser(User user) throws Exception {
        log.info("saving user: " + user);
        return getUserManager().saveUser(user);
    }

    public int saveAddressForUser(Address addr, int userId) throws Exception {
        return getUserManager().saveAddressForUser(addr, userId);
    }

    public int saveCredentialForUser(Credential cred, int userId)
            throws Exception {
        return getUserManager().saveCredentialForUser(cred, userId);
    }

    public int saveDuraAcctForUser(DuraCloudAcct duraAcct, int userId)
            throws Exception {
        int duraAcctId = getDuraCloudAcctRepository().saveDuraAcct(duraAcct);
        getUserManager().saveDuraAcctIdForUser(duraAcctId, userId);

        return duraAcctId;
    }

    public int saveComputeAcct(ComputeAcct computeAcct) throws Exception {
        return getComputeAcctManager().saveComputeAcct(computeAcct);
    }

    public int saveStorageAcct(StorageAcct storageAcct) throws Exception {
        return getStorageAcctManager().saveStorageAcct(storageAcct);
    }

    public int saveCredentialForStorageAcct(Credential storageAcctCred,
                                            int storageAcctId) throws Exception {
        return getStorageAcctManager()
                .saveCredentialForStorageAcct(storageAcctCred, storageAcctId);
    }

    public int saveCredentialForComputeAcct(Credential computeAcctCred,
                                            int computeAcctId) throws Exception {
        return getComputeAcctManager()
                .saveCredentialForComputeAcct(computeAcctCred, computeAcctId);
    }

    public boolean isComputeNamespaceTaken(String computeAcctNamespace) {
        return getComputeAcctManager()
                .isComputeNamespaceTaken(computeAcctNamespace);
    }

    public boolean isStorageNamespaceTaken(String storageAcctNamespace) {
        return getStorageAcctManager()
                .isStorageNamespaceTaken(storageAcctNamespace);
    }

    public ComputeAcctManager getComputeAcctManager() {
        return computeAcctManager;
    }

    public void setComputeAcctManager(ComputeAcctManager computeAcctManager) {
        this.computeAcctManager = computeAcctManager;
    }

    public StorageAcctManager getStorageAcctManager() {
        return storageAcctManager;
    }

    public void setStorageAcctManager(StorageAcctManager storageAcctManager) {
        this.storageAcctManager = storageAcctManager;
    }

    public DuraCloudAcctRepository getDuraCloudAcctRepository() {
        return duraCloudAcctRepository;
    }

    public void setDuraCloudAcctRepository(DuraCloudAcctRepository duraCloudAcctRepository) {
        this.duraCloudAcctRepository = duraCloudAcctRepository;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

}
