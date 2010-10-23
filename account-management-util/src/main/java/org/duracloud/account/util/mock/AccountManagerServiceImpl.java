package org.duracloud.account.util.mock;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.error.SubdomainAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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
	
	@Autowired
	private DuracloudUserService userService;
	
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
		String username = accountInfo.getOwner().getUsername();
		try{
			this.userService.addUserToAccount(id, username);
			this.userService.grantOwnerRights(id, username);
			return accountService;
		}catch(DBNotFoundException ex){
			throw new Error(ex);
		}
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
	public List<AccountInfo> lookupAccountsByUsername(String username)
			throws UsernameNotFoundException {

		try {
			DuracloudUser user  = this.userService.loadDuracloudUserByUsername(username);
			user  = this.userService.loadDuracloudUserByUsername(username);
			
			List<AccountInfo> accounts = new LinkedList<AccountInfo>();
			for(String accountId : user.getAcctToRoles().keySet()){
				accounts.add(this.accountServiceMap.get(accountId).retrieveAccountInfo());
			}
			
			return accounts;
		} catch (DBNotFoundException e) {
			throw new UsernameNotFoundException("user [" + username+"] not found", e);
		}


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


	public void setUserService(DuracloudUserService userService) {
		this.userService = userService;
	}


	public DuracloudUserService getUserService() {
		return userService;
	}

}
