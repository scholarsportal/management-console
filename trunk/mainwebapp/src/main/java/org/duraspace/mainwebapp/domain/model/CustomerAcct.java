
package org.duraspace.mainwebapp.domain.model;

import java.util.List;

public class CustomerAcct {

    private String id;

    private List<User> users;

    private Credential duraspaceCredential;

    private String computeAcctId; // One compute-account per customer-account.

    public boolean authenticates(Credential cred) {
        return (cred != null
                && duraspaceCredential.getUsername().equals(cred.getUsername()) && duraspaceCredential
                .getPassword().equals(cred.getPassword()));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CustomerAcct[");
        sb.append(duraspaceCredential.getUsername());
        sb.append(":");
        sb.append(duraspaceCredential.getPassword().replaceAll(".", "*"));
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComputeAcctId() {
        return computeAcctId;
    }

    public void setComputeAcctId(String computeAcctId) {
        this.computeAcctId = computeAcctId;
    }

}
