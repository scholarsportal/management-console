/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
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
    public boolean checkSubdomain(String subdomain);

    /**
     * @return
     */
    public List<StorageProviderType> getStorageProviders();

    /**
     * @param storageProviderTypes
     */
    public void setStorageProvider(List<StorageProviderType> storageProviderTypes);

	/**
     * @return empty list
     */
	public List<DuracloudUser> getUsers();
}
