package org.duracloud.account.util.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.NullArgumentException;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.error.SubdomainAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private Logger log = LoggerFactory.getLogger(getClass());
	private DuracloudUserRepo userRepo;
	private DuracloudAccountRepo accountRepo;
	private DuracloudUserService userService;

	public AccountManagerServiceImpl(DuracloudUserRepo userRepo,
			DuracloudAccountRepo accountRepo) {
		if (userRepo == null) {
			throw new NullArgumentException("userRepo must be non null.");
		}

		this.userRepo = userRepo;

		if (accountRepo == null) {
			throw new NullArgumentException("accountRepo must be non null.");
		}

		this.accountRepo = accountRepo;
		
		this.userService = 
			new DuracloudUserServiceImpl(this.userRepo, this.accountRepo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.duracloud.account.util.AccountManagerService#createAccount(org.duracloud
	 * .account.common.domain.AccountInfo)
	 */
	@Override
	public synchronized AccountService createAccount(AccountInfo accountInfo)
			throws SubdomainAlreadyExistsException {
		if (!checkSubdomain(accountInfo.getSubdomain())) {
			throw new SubdomainAlreadyExistsException();
		}
		try {

			String username = accountInfo.getOwner().getUsername();
			String id = (this.accountRepo.getIds().size() + 1) + "";
			AccountInfo newAccountInfo = new AccountInfo(
					id, 
					accountInfo.getSubdomain(), 
					accountInfo.getAcctName(), 
					accountInfo.getOrgName(), 
					accountInfo.getDepartment(), 
					accountInfo.getOwner(), 
					accountInfo.getStorageProviders());
			
			this.accountRepo.save(newAccountInfo);
			
			userService.addUserToAccount(id, username);
			userService.grantOwnerRights(id, username);
			return new AccountServiceImpl(id, this.accountRepo.findById(id));
		} catch (DBNotFoundException ex) {
			throw new Error(ex);
		} catch (DBConcurrentUpdateException ex) {
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
		try {
			return new AccountServiceImpl(accountId, this.accountRepo.findById(accountId));
		} catch (DBNotFoundException e) {
			throw new AccountNotFoundException();
		}
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
			DuracloudUser user = this.userService
					.loadDuracloudUserByUsername(username);
			user = this.userService.loadDuracloudUserByUsername(username);
			List<AccountInfo> accounts = new LinkedList<AccountInfo>();
			for (String accountId : user.getAcctToRoles().keySet()) {
				accounts.add(this.accountRepo.findById(accountId));
			}

			return accounts;
		} catch (DBNotFoundException e) {
			throw new UsernameNotFoundException("user [" + username
					+ "] not found", e);
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
		for (String accountId : this.accountRepo.getIds()) {
			try {
				AccountInfo accountInfo;
				accountInfo = this.accountRepo.findById(accountId);
				if (accountInfo.getSubdomain().equals(subdomain)) {
					return false;
				}
			} catch (DBNotFoundException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

}
