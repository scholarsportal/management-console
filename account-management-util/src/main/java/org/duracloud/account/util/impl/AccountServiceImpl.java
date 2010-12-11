/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.PaymentInfo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.DuracloudInstanceManagerService;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.error.DuracloudInstanceNotAvailableException;
import org.duracloud.account.util.error.DuracloudInvalidVersionException;
import org.duracloud.storage.domain.StorageProviderType;

import java.util.Set;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)" 
 */
public class AccountServiceImpl implements AccountService {

    // The AccountInfo member is a read-cache. All 'getter' come from it, and
    // writes go to both it and the persistence layer.
	private AccountInfo account;
    private DuracloudRepoMgr repoMgr;

	/**
	 * @param acct
	 */
	public AccountServiceImpl(AccountInfo acct, DuracloudRepoMgr repoMgr) {
		this.account = acct;
        this.repoMgr = repoMgr;
	}

	@Override
	public Set<DuracloudUser> getUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccountInfo retrieveAccountInfo() {
		return account;
	}

    @Override
    public Set<StorageProviderType> getStorageProviders() {
        return account.getStorageProviders();
    }

    @Override
    public void setStorageProviders(Set<StorageProviderType> storageProviderTypes)
        throws DBConcurrentUpdateException {
        account.setStorageProviders(storageProviderTypes);
        repoMgr.getAccountRepo().save(account);
    }

    @Override
	public void storeAccountInfo(String acctName,
                                 String orgName,
			                     String department) {
		// TODO Auto-generated method stub

	}

	@Override
	public void storePaymentInfo(PaymentInfo paymentInfo) {
		// TODO Auto-generated method stub

	}

    @Override
    public PaymentInfo retrievePaymentInfo() {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public void storeSubdomain(String subdomain) {
		// TODO Auto-generated method stub

	}
    
}
