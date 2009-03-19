
package org.duraspace.mainwebapp.domain.cmd;

import java.util.List;

import org.duraspace.mainwebapp.domain.model.ComputeAcct;
import org.duraspace.mainwebapp.domain.model.DuraSpaceAcct;
import org.duraspace.mainwebapp.domain.model.StorageAcct;
import org.duraspace.mainwebapp.domain.model.User;

public class DuraAcctWrapper {

    private DuraSpaceAcct duraAcct;

    private List<User> users;

    private List<ComputeAcct> computeAccts;

    private List<StorageAcct> storageAccts;

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
