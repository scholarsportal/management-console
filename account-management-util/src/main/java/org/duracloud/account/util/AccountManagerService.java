/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import org.duracloud.account.common.domain.AccountCluster;
import org.duracloud.account.common.domain.AccountCreationInfo;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.util.error.AccountClusterNotFoundException;
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
    @Secured({"role:ROLE_USER, scope:ANY"})
	public boolean subdomainAvailable(String subdomain);

	/**
	 * Returns an <code>AccountService</code> interface.
	 * 
	 * @param accountId
	 * @return
	 * @throws org.duracloud.account.util.error.AccountNotFoundException
	 * 
	 */
    @Secured({"role:ROLE_USER, scope:SELF_ACCT"})
	public AccountService getAccount(int accountId)
			throws AccountNotFoundException;

	/**
	 * @param accountCreationInfo
	 * @return AccountService
	 * @throws SubdomainAlreadyExistsException
	 * 
	 */
    @Secured({"role:ROLE_USER, scope:ANY"})
	public AccountService createAccount(AccountCreationInfo accountCreationInfo,
			DuracloudUser owner) throws SubdomainAlreadyExistsException;

	/**
	 * @param userId
	 * @return Returns the set of accounts loaded with the user's set of rights.
	 *         If there are no accounts associated with the specified user, an
	 *         empty set is returned.
	 */
    @Secured({"role:ROLE_USER, scope:SELF_ID"})
	public Set<AccountInfo> findAccountsByUserId(int userId);

	/**
	 * Returns an <code>AccountClusterService</code> interface.
	 *
	 * @param accountClusterId
	 * @return
	 * @throws AccountClusterNotFoundException
	 *
	 */
    @Secured({"role:ROLE_ADMIN, scope:ANY"})
	public AccountClusterService getAccountCluster(int accountClusterId)
        throws AccountClusterNotFoundException;

	/**
     * Creates a new Account Cluster with the given name and an empty
     * account list.
     *
	 * @param clusterName the display name of this cluster
	 * @return AccountClusterService
	 *
	 */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
	public AccountClusterService createAccountCluster(String clusterName);

    /**
     * Returns a list of account cluster descriptors
     * @param filter
     * @return
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public Set<AccountCluster> listAccountClusters(String filter);

}
