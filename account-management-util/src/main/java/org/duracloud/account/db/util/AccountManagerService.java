/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util;

import java.util.Set;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.util.AccountCreationInfo;
import org.duracloud.account.db.util.error.AccountNotFoundException;
import org.duracloud.account.db.util.error.SubdomainAlreadyExistsException;
import org.springframework.security.access.annotation.Secured;

/**
 * @author Erik Paulsson
 *         Date: 7/17/13
 */
public interface AccountManagerService {

    /**
     * Checks to see if the subdomain is available.
     *
     * @return true if the subdomain is available, otherwise file.
     */
    @Secured({"role:ROLE_USER, scope:ANY"})
    public boolean subdomainAvailable(String subdomain);

    /**
     * Returns an <code>AccountService</code> interface.
     *
     * @param accountId
     * @return
     * @throws AccountNotFoundException
     *
     */
    @Secured({"role:ROLE_USER, scope:SELF_ACCT"})
    public AccountService getAccount(Long accountId)
            throws AccountNotFoundException;

    /**
     * @param accountCreationInfo
     * @return AccountService
     * @throws SubdomainAlreadyExistsException
     *
     */
    @Secured({"role:ROLE_USER, scope:ANY"})
    public AccountService createAccount(AccountCreationInfo accountCreationInfo) throws SubdomainAlreadyExistsException;

    /**
     * @param userId
     * @return Returns the set of accounts loaded with the user's set of rights.
     *         If there are no accounts associated with the specified user, an
     *         empty set is returned.
     */
    @Secured({"role:ROLE_USER, scope:SELF_ID"})
    public Set<AccountInfo> findAccountsByUserId(Long userId);

}
