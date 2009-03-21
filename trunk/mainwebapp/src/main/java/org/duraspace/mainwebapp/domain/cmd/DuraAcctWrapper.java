
package org.duraspace.mainwebapp.domain.cmd;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.duraspace.mainwebapp.domain.model.DuraSpaceAcct;
import org.duraspace.mainwebapp.domain.model.StorageAcct;
import org.duraspace.mainwebapp.domain.model.User;
import org.duraspace.mainwebapp.mgmt.DuraSpaceAcctManager;

public class DuraAcctWrapper {

    protected static final Logger log = Logger.getLogger(DuraAcctWrapper.class);

    private DuraSpaceAcct duraAcct;

    private List<User> users;

    private List<ComputeAcct> computeAccts;

    private List<StorageAcct> storageAccts;

    public static DuraAcctWrapper buildWrapper(DuraSpaceAcctManager mgr,
                                               int duraAcctId) throws Exception {
        DuraSpaceAcct duraAcct = mgr.findDuraSpaceAcctById(duraAcctId);
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

    private static List<ComputeAcct> findComputeAccts(DuraSpaceAcctManager mgr,
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

    private static List<StorageAcct> findStorageAccts(DuraSpaceAcctManager mgr,
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

    public DuraSpaceAcct getDuraAcct() {
        return duraAcct;
    }

    public void setDuraAcct(DuraSpaceAcct duraAcct) {
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
