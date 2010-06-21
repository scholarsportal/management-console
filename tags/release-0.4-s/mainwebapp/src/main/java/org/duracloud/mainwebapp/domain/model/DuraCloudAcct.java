/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.domain.model;

import java.util.List;

import org.duracloud.common.model.Credential;

public class DuraCloudAcct {

    private int id;

    private String accountName;

    private int billingInfoId;

    private List<User> users;

    private String computeAcctId; // One compute-account per customer-account.

    // TODO: remove me
    private Credential duracloudCredential;

    public boolean authenticates(Credential cred) {
        return (cred != null
                && duracloudCredential.getUsername().equals(cred.getUsername()) && duracloudCredential
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
        StringBuilder sb = new StringBuilder("DuraCloudAcct[");
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

    public Credential getDuracloudCredential() {
        return duracloudCredential;
    }

    public void setDuracloudCredential(Credential credential) {
        this.duracloudCredential = credential;
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
