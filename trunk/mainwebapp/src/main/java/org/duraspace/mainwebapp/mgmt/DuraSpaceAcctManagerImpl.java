
package org.duraspace.mainwebapp.mgmt;

import java.util.List;

import org.apache.log4j.Logger;

import org.duraspace.common.model.Credential;
import org.duraspace.mainwebapp.domain.model.Address;
import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.duraspace.mainwebapp.domain.model.DuraSpaceAcct;
import org.duraspace.mainwebapp.domain.model.StorageAcct;
import org.duraspace.mainwebapp.domain.model.User;
import org.duraspace.mainwebapp.domain.repo.DuraSpaceAcctRepository;

public class DuraSpaceAcctManagerImpl
        implements DuraSpaceAcctManager {

    protected static final Logger log =
            Logger.getLogger(DuraSpaceAcctManagerImpl.class);

    private ComputeAcctManager computeAcctManager;

    private StorageAcctManager storageAcctManager;

    private DuraSpaceAcctRepository duraSpaceAcctRepository;

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

    public DuraSpaceAcct findDuraSpaceAccount(Credential duraCred)
            throws Exception {
        User user = this.getUserManager().findUser(duraCred);
        int duraAcctId = user.getDuraAcctId();

        return getDuraSpaceAcctRepository().findDuraAcctById(duraAcctId);
    }

    public List<ComputeAcct> findComputeAccounts(Credential duraCred) throws Exception {
        DuraSpaceAcct duraAcct = findDuraSpaceAccount(duraCred);

        return getComputeAcctManager().findComputeAccountsByDuraAcctId(duraAcct
                .getId());
    }

    public List<StorageAcct> findStorageAccounts(int duraAcctId)
            throws Exception {
        log.info("finding storage provider accts for id: " + duraAcctId);
        return getStorageAcctManager()
                .getStorageProviderAccountsByDuraAcctId(duraAcctId);

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

    public int saveDuraAcctForUser(DuraSpaceAcct duraAcct, int userId)
            throws Exception {
        int duraAcctId = getDuraSpaceAcctRepository().saveDuraAcct(duraAcct);
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

    public DuraSpaceAcctRepository getDuraSpaceAcctRepository() {
        return duraSpaceAcctRepository;
    }

    public void setDuraSpaceAcctRepository(DuraSpaceAcctRepository duraSpaceAcctRepository) {
        this.duraSpaceAcctRepository = duraSpaceAcctRepository;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

}
