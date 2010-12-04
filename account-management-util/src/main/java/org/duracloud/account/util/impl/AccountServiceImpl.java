/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.PaymentInfo;
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
public class AccountServiceImpl implements AccountService,
		DuracloudInstanceManagerService {
	private AccountInfo account;

	/**
	 * @param acct
	 */
	public AccountServiceImpl(AccountInfo acct) {
		this.account = acct;
	}

	@Override
	public Set<StorageProviderType> getStorageProviders() {
		return account.getStorageProviders();
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
	public void setStorageProviders(Set<StorageProviderType> storageProviderTypes) {
		// TODO Auto-generated method stub
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
	public void storeSubdomain(String subdomain) {
		// TODO Auto-generated method stub

	}

	@Override
	public DuracloudInstanceService createNewInstance(String acctId,
			String version) throws DuracloudInvalidVersionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DuracloudInstanceService getInstance(String acctId)
			throws DuracloudInstanceNotAvailableException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeInstance(String acctId) {
		// TODO Auto-generated method stub
	}

	@Override
	public PaymentInfo retrievePaymentInfo() {
		// TODO Auto-generated method stub
		return null;
	}
}
