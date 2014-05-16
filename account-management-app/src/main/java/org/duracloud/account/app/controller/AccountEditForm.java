/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 
 * @author Daniel Bernstein
 *
 */
public class AccountEditForm {
    @NotBlank(message = "You must specify an organization.")
    private String orgName;

    private String department;
    
    private Long accountClusterId;

    @NotBlank(message = "You must specify an account name.")
    private String acctName;

    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName;
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

    public Long getAccountClusterId() {
        return accountClusterId;
    }

    public void setAccountClusterId(Long accountClusterId) {
        this.accountClusterId = accountClusterId;
    }
    
}
