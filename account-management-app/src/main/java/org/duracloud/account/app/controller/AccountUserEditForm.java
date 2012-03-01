package org.duracloud.account.app.controller;

import javax.validation.constraints.NotNull;


public class AccountUserEditForm {
    @NotNull
    private String role;
    @NotNull
    private int accountId;
    
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setAccountId(int accountId){
        this.accountId = accountId;
    }

    public int getAccountId() {
        return accountId;
    }
}
