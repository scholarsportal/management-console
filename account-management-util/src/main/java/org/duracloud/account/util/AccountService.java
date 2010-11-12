/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import java.util.List;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.PaymentInfo;
import org.duracloud.storage.domain.StorageProviderType;

/**
 * An interface for manipulating account data.
 *
 * @author "Daniel Bernstein (dbernstein@duracloud.org)"
 */
public interface AccountService {
    /**
     * @return
     */
    public AccountInfo retrieveAccountInfo();

    /**
     * @param acctName
     * @param orgName
     * @param department
     */
    public void storeAccountInfo(String acctName,
                                 String orgName,
                                 String department);

    /**
     * @return
     */
    public PaymentInfo retrievePaymentInfo();

    /**
     * @param paymentInfo
     */
    public void storePaymentInfo(PaymentInfo paymentInfo);

    /**
     * @param subdomain
     */
    public void storeSubdomain(String subdomain);


    /**
     * @return
     */
    public List<StorageProviderType> getStorageProviders();

    /**
     * @param storageProviderTypes
     */
    public void setStorageProviders(List<StorageProviderType> storageProviderTypes);

	/**
     * @return empty list
     */
	public List<DuracloudUser> getUsers();
}
