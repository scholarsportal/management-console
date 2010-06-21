/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.domain.cmd;

public class StorageStatusCmd {

    private String cmd;

    private Integer storageAcctId;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public int getStorageAcctIdAsInt() {
        return storageAcctId;
    }

    public void setStorageAcctId(String storageAcctId) {
        this.storageAcctId = Integer.parseInt(storageAcctId);
    }

    public Integer getStorageAcctId() {
        return storageAcctId;
    }

    public void setStorageAcctId(Integer storageAcctId) {
        this.storageAcctId = storageAcctId;
    }
}
