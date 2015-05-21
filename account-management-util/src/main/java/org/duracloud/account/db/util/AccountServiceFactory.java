/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.util.error.AccountNotFoundException;
/**
 * @author Andrew Woods
 *         Date: 4/7/11
 */
public interface AccountServiceFactory {

    /**
     * This method provides an AccountService for access to the arg acctInfo
     *
     * @param acctInfo of the expected AccountService
     * @return AccountService
     */
    public AccountService getAccount(AccountInfo acctInfo);

    /**
     * This method provides an AccountService for access to the arg acctId
     *
     * @param acctId of the expected AccountService
     * @return AccountService
     * @throws AccountNotFoundException if not acct with acctId found in DB
     */
    public AccountService getAccount(Long acctId)
        throws AccountNotFoundException;
}
