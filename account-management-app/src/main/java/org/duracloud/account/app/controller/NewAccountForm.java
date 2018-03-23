/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import java.io.Serializable;

import org.duracloud.account.annotation.UniqueSubdomainConstraint;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author Daniel Bernstein
 */
@Component("newAccountForm")
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class NewAccountForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "You must specify an organization.")
    private String orgName;

    private String department;

    @NotBlank(message = "You must specify an account name.")
    private String acctName;

    @UniqueSubdomainConstraint
    @Length(min = 3, max = 25, message = "Subdomain must be between 3 and 25 characters.")
    private String subdomain;

    public String getSubdomain() {
        return subdomain;
    }

    public void setSubdomain(String subdomain) {
        this.subdomain = subdomain;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName;
    }

}
