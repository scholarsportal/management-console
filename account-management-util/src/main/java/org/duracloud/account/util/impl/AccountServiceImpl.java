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
import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudUserInvitationRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountService;
import org.duracloud.storage.domain.StorageProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

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
        DuracloudUserRepo userRepo = repoMgr.getUserRepo();
        DuracloudRightsRepo rightsRepo = repoMgr.getRightsRepo();

        Set<DuracloudUser> users = new HashSet<DuracloudUser>();
        try {
            Set<AccountRights> rights =
                rightsRepo.findByAccountId(account.getId());

            for (AccountRights right : rights) {
                DuracloudUser user = userRepo.findById(right.getUserId());
                Set<AccountRights> userRights = new HashSet<AccountRights>();
                userRights.add(right);
                user.setAccountRights(userRights);
                users.add(user);
            }
        } catch (DBNotFoundException ex) {
            log.warn(
                "No AccountRights found for account[{}]: error message: {}",
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
    public void storeAccountInfo(
        String acctName, String orgName, String department) {
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

    @Override
    public UserInvitation createUserInvitation(String emailAddress)
        throws DBConcurrentUpdateException {
        
        DuracloudUserInvitationRepo userInvitationRepo =
            repoMgr.getUserInvitationRepo();

        int id = repoMgr.getIdUtil().newUserInvitationId();
        String redemptionCode =
            DigestUtils.md5DigestAsHex((emailAddress + System
                .currentTimeMillis()).getBytes());
        UserInvitation userInvitation =
            new UserInvitation(
                id, this.account.getId(), emailAddress, 14, redemptionCode, 0);

        userInvitationRepo.save(userInvitation);

        return userInvitation;
    }

    @Override
    public Set<UserInvitation> getPendingInvitations()
        throws DBConcurrentUpdateException {
        DuracloudUserInvitationRepo userInvitationRepo =
            repoMgr.getUserInvitationRepo();
        return userInvitationRepo.findByAccountId(this.account.getId());
    }

}
