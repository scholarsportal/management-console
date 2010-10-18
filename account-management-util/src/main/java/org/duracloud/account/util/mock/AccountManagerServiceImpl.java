package org.duracloud.account.util.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.error.SubdomainAlreadyExistsException;
import org.duracloud.account.util.error.UsernameAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license
 */

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */

public class AccountManagerServiceImpl implements AccountManagerService {
	private Map<String, AccountService> accountServiceMap = new HashMap<String, AccountService>();
	private Logger log = LoggerFactory.getLogger(getClass());
	
	
	public AccountManagerServiceImpl(){
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.duracloud.account.util.AccountManagerService#createAccount(org.duracloud
	 * .account.common.domain.AccountInfo)
	 */
	@Override
	public AccountService createAccount(AccountInfo accountInfo)
			throws SubdomainAlreadyExistsException {
		if(!checkSubdomain(accountInfo.getSubdomain())){
			throw new SubdomainAlreadyExistsException();
		}
		String id = this.accountServiceMap.size() + "";
		AccountService accountService = new AccountServiceImpl(id, accountInfo);
		accountServiceMap.put(id,accountService);
		return accountService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.duracloud.account.util.AccountManagerService#getAccount(java.lang
	 * .String)
	 */
	@Override
	public AccountService getAccount(String accountId)
			throws AccountNotFoundException {
		if (!this.accountServiceMap.containsKey(accountId)) {
			throw new AccountNotFoundException();
		}
		return this.accountServiceMap.get(accountId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.duracloud.account.util.AccountManagerService#lookupAccounts(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public List<String> lookupAccounts(String username, String password)
			throws AccountNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.duracloud.account.util.AccountManagerService#checkSubdomain(java.
	 * lang.String)
	 */
	@Override
	public boolean checkSubdomain(String subdomain) {
		for (AccountService s : this.accountServiceMap.values()) {
			if (s.retrieveAccountInfo().getSubdomain().equals(subdomain)) {
				return false;
			}
		}
		return true;
	}

}
