
package org.duraspace.mainwebapp.domain.model;

import java.util.List;

import org.duraspace.common.model.Credential;

public class DuraSpaceAcct {

    private int id;

    private String accountName;

    private int billingInfoId;

    private List<User> users;

    private String computeAcctId; // One compute-account per customer-account.

    // TODO: remove me
    private Credential duraspaceCredential;

    public boolean authenticates(Credential cred) {
        return (cred != null
                && duraspaceCredential.getUsername().equals(cred.getUsername()) && duraspaceCredential
                .getPassword().equals(cred.getPassword()));
    }

    public boolean hasId() {
        return id > 0;
    }

    public boolean hasBillingInfoId() {
        return billingInfoId > 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DuraSpaceAcct[");
        sb.append(accountName);
        sb.append("]\n");
        return sb.toString();
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Credential getDuraspaceCredential() {
        return duraspaceCredential;
    }

    public void setDuraspaceCredential(Credential credential) {
        this.duraspaceCredential = credential;
    }

    public String getComputeAcctId() {
        return computeAcctId;
    }

    public void setComputeAcctId(String computeAcctId) {
        this.computeAcctId = computeAcctId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public int getBillingInfoId() {
        return billingInfoId;
    }

    public void setBillingInfoId(int billingInfoId) {
        this.billingInfoId = billingInfoId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
