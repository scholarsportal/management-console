/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.error.SubdomainAlreadyExistsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Set;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public interface AccountManagerService {

    /**
     * Checks to see if the subdomain is available.
     * @return true if the subdomain is available, otherwise file.
     */
    public boolean subdomainAvailable(String subdomain);
	
	/**
     * Returns an <code>AccountService</code> interface.
     *
     * @param accountId
     * @return
     * @throws org.duracloud.account.util.error.AccountNotFoundException
     *
     */
    public AccountService getAccount(int accountId)
        throws AccountNotFoundException;

    /**
     * @param accountInfo
     * @return AccountService
     * @throws org.duracloud.account.util.error.UsernameAlreadyExistsException
     *
     */
    public AccountService createAccount(AccountInfo accountInfo,
                                        DuracloudUser owner)
        throws SubdomainAlreadyExistsException;

    /**
     * @param userId
     * @return list of account ids
     * @throws AccountNotFoundException
     */
    public Set<AccountInfo> findAccountsByUserId(int userId)
        throws UsernameNotFoundException;
}
