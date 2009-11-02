package org.duracloud.mainwebapp.domain.model;

import java.io.Serializable;

public class User
        implements Serializable {

    private static final long serialVersionUID = 1816069045684903936L;

    private int id;

    private String lastname;

    private String firstname;

    private String email;

    private String phoneWork;

    private String phoneOther;

    private int addrShippingId;

    private int credentialId;

    private int duraAcctId;

    public boolean hasId() {
        return id > 0;
    }

    public boolean hasAddrShippingId() {
        return addrShippingId > 0;
    }

    public boolean hasCredentialId() {
        return credentialId > 0;
    }

    public boolean hasDuraAcctId() {
        return duraAcctId > 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[User: ");
        sb.append(this.lastname);
        sb.append(", ");
        sb.append(this.firstname);
        sb.append("]");
        return sb.toString();
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneWork() {
        return phoneWork;
    }

    public void setPhoneWork(String phoneWork) {
        this.phoneWork = phoneWork;
    }

    public String getPhoneOther() {
        return phoneOther;
    }

    public void setPhoneOther(String phoneOther) {
        this.phoneOther = phoneOther;
    }

    public int getAddrShippingId() {
        return addrShippingId;
    }

    public void setAddrShippingId(int addrShippingId) {
        this.addrShippingId = addrShippingId;
    }

    public int getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(int credentialId) {
        this.credentialId = credentialId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDuraAcctId() {
        return duraAcctId;
    }

    public void setDuraAcctId(int duraAcctId) {
        this.duraAcctId = duraAcctId;
    }

}
