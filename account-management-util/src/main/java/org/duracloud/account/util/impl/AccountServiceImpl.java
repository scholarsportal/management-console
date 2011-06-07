/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.PaymentInfo;
import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudStorageProviderAccountRepo;
import org.duracloud.account.db.DuracloudUserInvitationRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.error.DuracloudProviderAccountNotAvailableException;
import org.duracloud.account.util.error.UnsentEmailException;
import org.duracloud.common.util.ChecksumUtil;
import org.duracloud.notification.Emailer;
import org.duracloud.storage.domain.StorageProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class AccountServiceImpl implements AccountService {
    private Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);
    // The AccountInfo member is a read-cache. All 'getter' come from it, and
    // writes go to both it and the persistence layer.
    private AccountInfo account;
    private DuracloudRepoMgr repoMgr;
    private DuracloudProviderAccountUtil providerAccountUtil;

    /**
     * @param acct
     */
    public AccountServiceImpl(AccountInfo acct,
                              DuracloudRepoMgr repoMgr,
                              DuracloudProviderAccountUtil providerAccountUtil) {
        this.account = acct;
        this.repoMgr = repoMgr;
        this.providerAccountUtil = providerAccountUtil;
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
                account.getId(), ex.getMessage());
        }

        return users;
    }

    @Override
    public int getAccountId() {
        return account.getId();
    }

    @Override
    public AccountInfo retrieveAccountInfo() {
        return account;
    }

    @Override
    public StorageProviderAccount getPrimaryStorageProvider() {
        int primaryId = account.getPrimaryStorageProviderAccountId();
        DuracloudStorageProviderAccountRepo repo =
            repoMgr.getStorageProviderAccountRepo();
        try {
            return repo.findById(primaryId);
        } catch(DBNotFoundException e) {
            throw new DuracloudProviderAccountNotAvailableException(
                e.getMessage(), e);
        }
    }

    @Override
    public Set<StorageProviderAccount> getSecondaryStorageProviders() {
        DuracloudStorageProviderAccountRepo repo =
            repoMgr.getStorageProviderAccountRepo();
        Set<StorageProviderAccount> accounts =
            new HashSet<StorageProviderAccount>();

        try {
            for(int id : account.getSecondaryStorageProviderAccountIds()) {
                accounts.add(repo.findById(id));
            }
        } catch(DBNotFoundException e) {
            throw new DuracloudProviderAccountNotAvailableException(
                e.getMessage(), e);
        }

        return accounts;
    }

    @Override
    public void addStorageProvider(StorageProviderType storageProviderType)
        throws DBConcurrentUpdateException {
        int id = providerAccountUtil.
            createEmptyStorageProviderAccount(storageProviderType);
        account.getSecondaryStorageProviderAccountIds().add(id);
        repoMgr.getAccountRepo().save(account);
    }

    @Override
    public void removeStorageProvider(int storageProviderId)
        throws DBConcurrentUpdateException {
        if(account.getSecondaryStorageProviderAccountIds()
                  .remove(storageProviderId)) {
            repoMgr.getAccountRepo().save(account);
            repoMgr.getStorageProviderAccountRepo().delete(storageProviderId);
        } else {
            throw new DuracloudProviderAccountNotAvailableException(
                "The storage provider account with ID " + storageProviderId +
                " is not associated with account with id " + account.getId() +
                " as a secondary storage provider.");
        }
    }

    @Override
    public void storeAccountInfo(String acctName,
                                 String orgName,
                                 String department)
        throws DBConcurrentUpdateException {
        account.setAcctName(acctName);
        account.setOrgName(orgName);
        account.setDepartment(department);
        repoMgr.getAccountRepo().save(account);
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
    public String getSubdomain() {
        return account.getSubdomain();
    }

    @Override
    public UserInvitation inviteUser(String emailAddress, String adminUsername, Emailer emailer)
        throws DBConcurrentUpdateException {

        ChecksumUtil cksumUtil = new ChecksumUtil(ChecksumUtil.Algorithm.MD5);

        String code = emailAddress + System.currentTimeMillis();
        String redemptionCode = cksumUtil.generateChecksum(code);

        int id = repoMgr.getIdUtil().newUserInvitationId();
        int acctId = account.getId();
        int expirationDays = 14;
        UserInvitation userInvitation = new UserInvitation(id,
                                                           acctId,
                                                           adminUsername,
                                                           emailAddress,
                                                           expirationDays,
                                                           redemptionCode);
        getUserInvitationRepo().save(userInvitation);
        sendEmail(userInvitation, emailer);

        return userInvitation;
    }

    private void sendEmail(UserInvitation invitation, Emailer emailer) {
        try {
            emailer.send(invitation.getSubject(),
                         invitation.getBody(),
                         invitation.getUserEmail());

        } catch (Exception e) {
            String msg =
                "Error: Unable to send email to: " + invitation.getUserEmail();
            log.error(msg);
            throw new UnsentEmailException(msg, e);
        }
    }

    @Override
    public Set<UserInvitation> getPendingInvitations() {
        return getUserInvitationRepo().findByAccountId(account.getId());
    }

    private DuracloudUserInvitationRepo getUserInvitationRepo() {
        DuracloudUserInvitationRepo userInvitationRepo =
            repoMgr.getUserInvitationRepo();
        return userInvitationRepo;
    }

    @Override
    public void deleteUserInvitation(int invitationId)
        throws DBConcurrentUpdateException {
        getUserInvitationRepo().delete(invitationId);
    }

}