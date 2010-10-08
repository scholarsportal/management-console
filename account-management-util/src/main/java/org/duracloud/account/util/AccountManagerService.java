/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.util;

import org.duracloud.account.util.domain.AccountInfo;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.error.UsernameAlreadyExistsException;

import java.util.List;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public interface AccountManagerService {
    /**
     * Returns an <code>AccountService</code> interface.
     *
     * @param accountId
     * @return
     * @throws org.duracloud.account.util.error.AccountNotFoundException
     *
     */
    public AccountService getAccount(String accountId)
        throws AccountNotFoundException;

    /**
     * @param accountInfo
     * @return AccountService
     * @throws org.duracloud.account.util.error.UsernameAlreadyExistsException
     *
     */
    public AccountService createAccount(AccountInfo accountInfo)
        throws UsernameAlreadyExistsException;

    /**
     * @param username
     * @param password
     * @return list of account ids
     * @throws AccountNotFoundException
     */
    public List<String> lookupAccounts(String username, String password)
        throws AccountNotFoundException;
}
