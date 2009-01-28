
package org.duraspace.domain;

import java.io.Serializable;

import java.util.List;

public class User
        implements Serializable {

    private static final long serialVersionUID = 1816069045684903936L;

    private String id;

    private String lastname;

    private String firstname;

    private String email;

    private int phoneWork;

    private int phoneOther;

    private List<Address> addrShippings;

    public User() {

    }

    public User getDefaultUser() {
        User user = new User();
        user.setFirstname("first-name");
        user.setLastname("last-name");
        return user;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getPhoneWork() {
        return phoneWork;
    }

    public void setPhoneWork(int phoneWork) {
        this.phoneWork = phoneWork;
    }

    public int getPhoneOther() {
        return phoneOther;
    }

    public void setPhoneOther(int phoneOther) {
        this.phoneOther = phoneOther;
    }

    public List<Address> getAddrShippings() {
        return addrShippings;
    }

    public void setAddrShippings(List<Address> addrShippings) {
        this.addrShippings = addrShippings;
    }

}
