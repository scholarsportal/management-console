/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.util;

import java.util.List;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.error.SubdomainAlreadyExistsException;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public interface AccountManagerService {

    /**
     * Checks if the subdomain is available.
     * @return true if the subdomain is available, otherwise file.
     */
    public boolean checkSubdomain(String subdomain);

	
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
        throws SubdomainAlreadyExistsException;

    /**
     * @param username
     * @param password
     * @return list of account ids
     * @throws AccountNotFoundException
     */
    public List<String> lookupAccounts(String username, String password)
        throws AccountNotFoundException;
}
