/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.common.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class DuracloudUser implements Identifiable {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private Map<String, List<String>> acctToRoles; // acct-ids --> roles

    private int counter;

    public DuracloudUser(String username,
                         String password,
                         String firstName,
                         String lastName,
                         String email) {
        this(username, password, firstName, lastName, email, 0);
    }

    public DuracloudUser(String username,
                         String password,
                         String firstName,
                         String lastName,
                         String email,
                         int counter) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.counter = counter;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Map<String, List<String>> getAcctToRoles() {
        if (null == acctToRoles) {
            acctToRoles = new HashMap<String, List<String>>();
        }
        return acctToRoles;
    }

    public void setAcctToRoles(Map<String, List<String>> acctToRoles) {
        this.acctToRoles = acctToRoles;
    }

    public Integer getCounter() {
        return counter;
    }

    @Override
    public String getId() {
        return getUsername();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DuracloudUser)) {
            return false;
        }

        DuracloudUser that = (DuracloudUser) o;

        if (acctToRoles != null ?
            acctToRoles.size() != (that.getAcctToRoles().size()) :
            that.acctToRoles != null) {
            return false;
        }
        if (email != null ? !email.equals(that.email) : that.email != null) {
            return false;
        }
        if (firstName != null ? !firstName.equals(that.firstName) :
            that.firstName != null) {
            return false;
        }
        if (lastName != null ? !lastName.equals(that.lastName) :
            that.lastName != null) {
            return false;
        }
        if (password != null ? !password.equals(that.password) :
            that.password != null) {
            return false;
        }
        if (username != null ? !username.equals(that.username) :
            that.username != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + getAcctToRoles().size();
        return result;
    }

}
