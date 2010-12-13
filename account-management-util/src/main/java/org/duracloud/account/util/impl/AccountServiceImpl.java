/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import java.util.HashSet;
import java.util.Set;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.PaymentInfo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountService;
import org.duracloud.storage.domain.StorageProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class AccountServiceImpl implements AccountService {
    private Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);
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
        Set<DuracloudUser> users = new HashSet<DuracloudUser>();
        try {

            Set<AccountRights> rights =
                this.repoMgr.getRightsRepo().findByAccountId(
                    this.account.getId());

            for (AccountRights right : rights) {
                DuracloudUser user = this.repoMgr.getUserRepo().findById(right.getUserId());
                Set<AccountRights> userRights = new HashSet<AccountRights>();
                userRights.add(right);
                user.setAccountRights(userRights); 
                users.add(user);
            }
        } catch (DBNotFoundException ex) {
            log
                .warn(
                    "something's wrong: no AccountRights found for account[{}]: error message: {}",
                    this.account.getId(), ex.getMessage());
        }

        return users;
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
    public void setStorageProviders(
        Set<StorageProviderType> storageProviderTypes)
        throws DBConcurrentUpdateException {
        account.setStorageProviders(storageProviderTypes);
        repoMgr.getAccountRepo().save(account);
    }

    @Override
    public void storeAccountInfo(String acctName, String orgName,
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
