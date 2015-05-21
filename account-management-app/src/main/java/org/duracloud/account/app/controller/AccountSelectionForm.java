/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
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
