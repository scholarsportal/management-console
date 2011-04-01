/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import org.duracloud.account.common.domain.AccountCreationInfo;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.error.SubdomainAlreadyExistsException;

import org.springframework.security.access.annotation.Secured;

import java.util.Set;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public interface AccountManagerService {

	/**
	 * Checks to see if the subdomain is available.
	 * 
	 * @return true if the subdomain is available, otherwise file.
	 */
    @Secured({"role:ROLE_USER, scope:any"})
	public boolean subdomainAvailable(String subdomain);

	/**
	 * Returns an <code>AccountService</code> interface.
	 * 
	 * @param accountId
	 * @return
	 * @throws org.duracloud.account.util.error.AccountNotFoundException
	 * 
	 */
    @Secured({"role:ROLE_USER, scope:self-acct"})
	public AccountService getAccount(int accountId)
			throws AccountNotFoundException;

	/**
	 * @param accountCreationInfo
	 * @return AccountService
	 * @throws org.duracloud.account.util.error.UsernameAlreadyExistsException
	 * 
	 */
    @Secured({"role:ROLE_USER, scope:any"})
	public AccountService createAccount(AccountCreationInfo accountCreationInfo,
			DuracloudUser owner) throws SubdomainAlreadyExistsException;

	/**
	 * @param userId
	 * @return Returns the set of accounts loaded with the user's set of rights.
	 *         If there are no accounts associated with the specified user, an
	 *         empty set is returned.
	 */
    @Secured({"role:ROLE_USER, scope:self"})
	public Set<AccountInfo> findAccountsByUserId(int userId);
}
