/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.util.error.AccountNotFoundException;

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
    public AccountService getAccount(int acctId)
        throws AccountNotFoundException;
}
