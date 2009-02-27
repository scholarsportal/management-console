
package org.duraspace.common.model;

import java.io.Serializable;

public class Credential
        implements Serializable {

    private static final long serialVersionUID = -7069231739026478165L;

    private String username;

    private String password;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Credential [");
        sb.append(username);
        sb.append(":");
        sb.append(password == null ? null : password.replaceAll(".", "*"));
        sb.append("]");
        return sb.toString();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
