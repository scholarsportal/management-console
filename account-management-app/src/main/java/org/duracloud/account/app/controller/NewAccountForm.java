/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.annotation.UniqueSubdomainConstraint;
import org.duracloud.account.db.model.AccountType;
import org.duracloud.account.db.model.ServicePlan;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Daniel Bernstein
 * 
 */
@Component("newAccountForm")
@Scope(value=WebApplicationContext.SCOPE_REQUEST)
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

    private AccountType accountType;

    private Long accountClusterId;

    public List<ServicePlan> getServicePlans(){
        return new LinkedList<ServicePlan>(Arrays.asList(ServicePlan.values()));
    }

    public List<AccountType> getAccountTypes(){
        return new LinkedList<AccountType>(Arrays.asList(AccountType.values()));
    }
    
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


    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Long getAccountClusterId() {
        return accountClusterId;
    }

    public void setAccountClusterId(Long accountClusterId) {
        this.accountClusterId = accountClusterId;
    }
    
    public boolean isCommunity(){
        return AccountType.COMMUNITY == this.accountType;
    }

}
