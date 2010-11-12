/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import java.util.List;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.PaymentInfo;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.DuracloudInstanceManagerService;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.error.DuracloudInstanceNotAvailableException;
import org.duracloud.account.util.error.DuracloudInvalidVersionException;
import org.duracloud.storage.domain.StorageProviderType;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class AccountServiceImpl implements AccountService,
		DuracloudInstanceManagerService {
	private AccountInfo accountInfo;

	/**
	 * @param accountInfo
	 */
	public AccountServiceImpl(String id, AccountInfo a) {
		this.accountInfo = new AccountInfo(id, a.getSubdomain(), a
				.getAcctName(), a.getOrgName(), a.getDepartment(),
				a.getOwner(), a.getStorageProviders());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.duracloud.account.util.AccountService#getStorageProviders()
	 */
	@Override
	public List<StorageProviderType> getStorageProviders() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.duracloud.account.util.AccountService#getUsers()
	 */
	@Override
	public List<DuracloudUser> getUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.duracloud.account.util.AccountService#retrieveAccountInfo()
	 */
	@Override
	public AccountInfo retrieveAccountInfo() {
		return this.accountInfo;
	}





	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.duracloud.account.util.AccountService#setStorageProvider(java.util
	 * .List)
	 */
	@Override
	public void setStorageProviders(
			List<StorageProviderType> storageProviderTypes) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.duracloud.account.util.AccountService#storeAccountInfo(java.lang.
	 * String, java.lang.String, java.lang.String)
	 */
	@Override
	public void storeAccountInfo(String acctName, String orgName,
			String department) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.duracloud.account.util.AccountService#storePaymentInfo(org.duracloud
	 * .account.common.domain.PaymentInfo)
	 */
	@Override
	public void storePaymentInfo(PaymentInfo paymentInfo) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.duracloud.account.util.AccountService#storeSubdomain(java.lang.String
	 * )
	 */
	@Override
	public void storeSubdomain(String subdomain) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.duracloud.account.util.DuracloudInstanceManagerService#createNewInstance
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public DuracloudInstanceService createNewInstance(String acctId,
			String version) throws DuracloudInvalidVersionException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.duracloud.account.util.DuracloudInstanceManagerService#getInstance
	 * (java.lang.String)
	 */
	@Override
	public DuracloudInstanceService getInstance(String acctId)
			throws DuracloudInstanceNotAvailableException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.duracloud.account.util.DuracloudInstanceManagerService#removeInstance
	 * (java.lang.String)
	 */
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
