/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.util;

import java.util.List;

import org.duracloud.account.util.domain.AccountInfo;
import org.duracloud.account.util.domain.DuracloudUser;
import org.duracloud.account.util.domain.PaymentInfo;
import org.duracloud.account.util.error.UsernameAlreadyExistsException;
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
    public List<PaymentInfo> retrievePaymentInfo();
    public PaymentInfo retrievePrimaryPaymentInfo();

    /**
     * @param paymentInfo
     */
    public void storePaymentInfo(PaymentInfo paymentInfo);
    public void setPrimaryPaymentInfo(String paymentInfoId);

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
