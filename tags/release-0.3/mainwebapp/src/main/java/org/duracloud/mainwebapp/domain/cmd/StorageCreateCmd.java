package org.duracloud.mainwebapp.domain.cmd;

import org.duracloud.common.model.Credential;

public class StorageCreateCmd {

    private int duraAcctId;

    private int isPrimary;

    private String storageAcctNamespace;

    private String storageProviderType;

    private Credential storageCred;

    private String cmd;

    public int getDuraAcctId() {
        return duraAcctId;
    }

    public int getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(int isPrimary) {
        this.isPrimary = isPrimary;
    }

    public String getStorageAcctNamespace() {
        return storageAcctNamespace;
    }

    public void setStorageAcctNamespace(String storageAcctNamespace) {
        this.storageAcctNamespace = storageAcctNamespace;
    }

    public String getStorageProviderType() {
        return storageProviderType;
    }

    public void setStorageProviderType(String storageProviderType) {
        this.storageProviderType = storageProviderType;
    }

    public Credential getStorageCred() {
        return storageCred;
    }

    public void setStorageCred(Credential storageCred) {
        this.storageCred = storageCred;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public void setDuraAcctId(int duraAcctId) {
        this.duraAcctId = duraAcctId;
    }

}
