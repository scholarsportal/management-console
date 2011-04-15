package org.duracloud.account.common.domain;

import java.util.Set;

public class DuracloudAccount {
    private AccountInfo accountInfo;

    private DuracloudInstance instance;

    private String instanceStatus;

    private String instanceVersion;

    private Set<String> versions;

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public void setAccountInfo(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
    }

    public DuracloudInstance getInstance() {
        return instance;
    }

    public void setInstance(DuracloudInstance instance) {
        this.instance = instance;
    }

    public String getInstanceStatus() {
        return instanceStatus;
    }

    public void setInstanceStatus(String instanceStatus) {
        this.instanceStatus = instanceStatus;
    }

    public String getInstanceVersion() {
        return instanceVersion;
    }

    public void setInstanceVersion(String instanceVersion) {
        this.instanceVersion = instanceVersion;
    }

    public Set<String> getVersions() {
        return versions;
    }

    public void setVersions(Set<String> versions) {
        this.versions = versions;
    }
}
