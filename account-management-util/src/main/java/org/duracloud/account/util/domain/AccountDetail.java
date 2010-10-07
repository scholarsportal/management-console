/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.util.domain;

/**
 * Read-only summary of salient account details.
 *
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class AccountDetail {
    private String id;
    private String username;
    private String fullName;
    private String orgName;
    private String email;
    private String department;

    protected AccountDetail(String id,
                            String username,
                            String fullName,
                            String email,
                            String orgName,
                            String department) {
        super();
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.orgName = orgName;
        this.department = department;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getOrgName() {
        return orgName;
    }

    public String getDepartment() {
        return department;
    }

}
