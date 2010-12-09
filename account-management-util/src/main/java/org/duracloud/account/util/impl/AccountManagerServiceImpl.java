/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.apache.commons.lang.NullArgumentException;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.IdUtil;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.error.SubdomainAlreadyExistsException;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */

public class AccountManagerServiceImpl implements AccountManagerService {
	private Logger log = LoggerFactory.getLogger(getClass());
	private DuracloudUserRepo userRepo;
	private DuracloudAccountRepo accountRepo;
    private DuracloudRightsRepo rightsRepo;
	private DuracloudUserService userService;
    private IdUtil idUtil;

	public AccountManagerServiceImpl(DuracloudUserRepo userRepo,
			                         DuracloudAccountRepo accountRepo,
                                     DuracloudRightsRepo rightsRepo,
                                     IdUtil idUtil) {
		if (userRepo == null) {
			throw new NullArgumentException("userRepo must be non null.");
		}
        if (accountRepo == null) {
            throw new NullArgumentException("accountRepo must be non null.");
        }
        if (rightsRepo == null) {
            throw new NullArgumentException("rightsRepo must be non null");
        }
        if (idUtil == null) {
            throw new NullArgumentException("idUtil must be non null");
        }

        this.userRepo = userRepo;
        this.accountRepo = accountRepo;
        this.rightsRepo = rightsRepo;
        this.idUtil = idUtil;

        this.userService = new DuracloudUserServiceImpl(this.userRepo,
                                                        this.accountRepo,
                                                        this.rightsRepo,
                                                        this.idUtil);
	}

	@Override
	public synchronized AccountService createAccount(AccountInfo accountInfo,
                                                     DuracloudUser owner)
			throws SubdomainAlreadyExistsException {
		if (!subdomainAvailable(accountInfo.getSubdomain())) {
			throw new SubdomainAlreadyExistsException();
		}
		try {
			int acctId = idUtil.newAccountId();
			AccountInfo newAccountInfo =
                new AccountInfo(acctId,
					            accountInfo.getSubdomain(),
					            accountInfo.getAcctName(),
					            accountInfo.getOrgName(),
					            accountInfo.getDepartment(),
                                accountInfo.getPaymentInfoId(),
                                accountInfo.getInstanceIds(),
					            accountInfo.getStorageProviders());
			this.accountRepo.save(newAccountInfo);

			userService.grantOwnerRights(acctId, owner.getId());
			return new AccountServiceImpl(newAccountInfo);
		} catch (DBConcurrentUpdateException ex) {
			throw new Error(ex);
		}
	}

	@Override
	public AccountService getAccount(int accountId)
			throws AccountNotFoundException {
		try {
			return new AccountServiceImpl(accountRepo.findById(accountId));
		} catch (DBNotFoundException e) {
			throw new AccountNotFoundException();
		}
	}

	@Override
	public Set<AccountInfo> findAccountsByUserId(int userId) {
		Set<AccountRights> userRights = null;
		Set<AccountInfo> userAccounts = null;
		try {
			userRights = rightsRepo.findByUserId(userId);
            userAccounts = new HashSet<AccountInfo>();
            for(AccountRights rights : userRights) {
                userAccounts.add(accountRepo.findById(rights.getAccountId()));
            }
            return userAccounts;
		} catch (DBNotFoundException e) {
            log.info("No accounts found for user {}", userId);
		}
		
		return new HashSet<AccountInfo>();

	}

    /*
     * FIXME: This action could be accomplished much more quickly by adding
     *        a db query specific to this need, rather than having to loop
     *        and check all accounts.
     */
	@Override
	public boolean subdomainAvailable(String subdomain) {
		for (int accountId : accountRepo.getIds()) {
			try {
				AccountInfo accountInfo = accountRepo.findById(accountId);
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
