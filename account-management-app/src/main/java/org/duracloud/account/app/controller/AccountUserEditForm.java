/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import javax.validation.constraints.NotNull;


public class AccountUserEditForm {
    @NotNull
    private String role;
    @NotNull
    private Long accountId;
    
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setAccountId(Long accountId){
        this.accountId = accountId;
    }

    public Long getAccountId() {
        return accountId;
    }
}
