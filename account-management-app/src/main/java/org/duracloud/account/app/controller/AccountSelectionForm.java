/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;


/**
 * 
 * @author Daniel Bernstein 
 *         Date: Mar 8, 2012
 */
public class AccountSelectionForm {
    
    private Long[] accountIds;

    public Long[] getAccountIds() {
        return accountIds;
    }

    public void setAccountIds(Long[] accountIds) {
        this.accountIds = accountIds;
    }
}
