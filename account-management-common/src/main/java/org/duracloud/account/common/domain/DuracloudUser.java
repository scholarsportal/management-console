/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.common.domain;

import org.duracloud.security.domain.SecurityUserBean;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class DuracloudUser extends SecurityUserBean {
    private String firstName;
    private String lastName;
    private String email;

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
        this.setUsername(username);
        this.setPassword(password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.counter = counter;
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

    public Integer getCounter() {
        return counter;
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

        if (getUsername() != null ? !getUsername().equals(that.getUsername()) :
            that.getUsername() != null) {
            return false;
        }
        if (getPassword() != null ? !getPassword().equals(that.getPassword()) :
            that.getPassword() != null) {
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

        return true;
    }

    @Override
    public int hashCode() {
        int result = firstName != null ? firstName.hashCode() : 0;
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result +
            (getUsername() != null ? getUsername().hashCode() : 0);
        result = 31 * result +
            (getPassword() != null ? getPassword().hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }
}
