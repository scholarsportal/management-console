package org.duracloud.mainwebapp.domain.cmd;

import java.util.ArrayList;
import java.util.List;

import org.duracloud.mainwebapp.domain.model.ComputeAcct;
import org.duracloud.mainwebapp.domain.model.DuraCloudAcct;
import org.duracloud.mainwebapp.domain.model.StorageAcct;
import org.duracloud.mainwebapp.domain.model.User;
import org.duracloud.mainwebapp.mgmt.DuraCloudAcctManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DuraAcctWrapper {

    protected static final Logger log = LoggerFactory.getLogger(DuraAcctWrapper.class);

    private DuraCloudAcct duraAcct;

    private List<User> users;

    private List<ComputeAcct> computeAccts;

    private List<StorageAcct> storageAccts;

    public static DuraAcctWrapper buildWrapper(DuraCloudAcctManager mgr,
                                               int duraAcctId) throws Exception {
        DuraCloudAcct duraAcct = mgr.findDuraCloudAcctById(duraAcctId);
        List<User> users = mgr.findUsers(duraAcctId);
        List<ComputeAcct> computeAccts = findComputeAccts(mgr, duraAcctId);
        List<StorageAcct> storageAccts = findStorageAccts(mgr, duraAcctId);

        DuraAcctWrapper wrapper = new DuraAcctWrapper();
        wrapper.setDuraAcct(duraAcct);
        wrapper.setUsers(users);
        wrapper.setComputeAccts(computeAccts);
        wrapper.setStorageAccts(storageAccts);
        return wrapper;
    }

    private static List<ComputeAcct> findComputeAccts(DuraCloudAcctManager mgr,
                                                      int duraAcctId) {
        List<ComputeAcct> accts = new ArrayList<ComputeAcct>();
        try {
            accts = mgr.findComputeAccounts(duraAcctId);
        } catch (Exception e) {
            log.info("Compute acct not found for duraAcctId: " + duraAcctId
                    + ", compute[" + accts.size() + "]");
        }
        return accts;
    }

    private static List<StorageAcct> findStorageAccts(DuraCloudAcctManager mgr,
                                                      int duraAcctId) {
        List<StorageAcct> accts = new ArrayList<StorageAcct>();
        try {
            accts = mgr.findStorageAccounts(duraAcctId);
        } catch (Exception e) {
            log.info("Storage acct not found for duraAcctId: " + duraAcctId
                    + ", storage[" + accts.size() + "]");
        }
        return accts;
    }

    public DuraCloudAcct getDuraAcct() {
        return duraAcct;
    }

    public void setDuraAcct(DuraCloudAcct duraAcct) {
        this.duraAcct = duraAcct;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<ComputeAcct> getComputeAccts() {
        return computeAccts;
    }

    public void setComputeAccts(List<ComputeAcct> computeAccts) {
        this.computeAccts = computeAccts;
    }

    public List<StorageAcct> getStorageAccts() {
        return storageAccts;
    }

    public void setStorageAccts(List<StorageAcct> storageAccts) {
        this.storageAccts = storageAccts;
    }

}
