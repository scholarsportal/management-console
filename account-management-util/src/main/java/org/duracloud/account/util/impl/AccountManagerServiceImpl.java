/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountCreationInfo;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.AccountType;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.common.domain.ServerDetails;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudServerDetailsRepo;
import org.duracloud.account.db.IdUtil;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.AccountServiceFactory;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.error.SubdomainAlreadyExistsException;
import org.duracloud.account.util.sys.EventMonitor;
import org.duracloud.storage.domain.StorageProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */

public class AccountManagerServiceImpl implements AccountManagerService {

    private Logger log =
        LoggerFactory.getLogger(AccountManagerServiceImpl.class);

    private DuracloudRepoMgr repoMgr;
    private DuracloudUserService userService;
    private AccountServiceFactory accountServiceFactory;
    private Set<EventMonitor> eventMonitors;
    private DuracloudProviderAccountUtil providerAccountUtil;

    public AccountManagerServiceImpl(DuracloudRepoMgr duracloudRepoMgr,
                                     DuracloudUserService duracloudUserService,
                                     AccountServiceFactory accountServiceFactory,
                                     DuracloudProviderAccountUtil providerAccountUtil,
                                     Set<EventMonitor> eventMonitors) {
        this.repoMgr = duracloudRepoMgr;
        this.userService = duracloudUserService;
        this.accountServiceFactory = accountServiceFactory;
        this.providerAccountUtil = providerAccountUtil;
        this.eventMonitors = eventMonitors;
    }

    @Override
    public AccountService createAccount(AccountCreationInfo accountCreationInfo,
                                        DuracloudUser owner)
        throws SubdomainAlreadyExistsException {
        log.info("Creating account with subdomain {} and owner {}",
                 accountCreationInfo.getSubdomain(),
                 owner.getUsername());

        AccountService acctService = doCreateAccount(accountCreationInfo, owner);

        // Notify monitors if account created successfully.
        Iterator<EventMonitor> itr = eventMonitors.iterator();
        while (itr.hasNext()) {
            try {
                itr.next().accountCreated(accountCreationInfo, owner);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return acctService;
    }

    private synchronized AccountService doCreateAccount(AccountCreationInfo accountCreationInfo,
                                                        DuracloudUser owner)
        throws SubdomainAlreadyExistsException {
        if (!subdomainAvailable(accountCreationInfo.getSubdomain())) {
            throw new SubdomainAlreadyExistsException();
        }
        try {
            int acctId = getIdUtil().newAccountId();
            int serverDetailsId = -1;
            AccountInfo.AccountStatus status = AccountInfo.AccountStatus.ACTIVE;

            AccountType accountType = accountCreationInfo.getAccountType();
            if(AccountType.FULL.equals(accountType)) {
                status = AccountInfo.AccountStatus.PENDING;

                serverDetailsId = getIdUtil().newServerDetailsId();

                int computeProviderAccountId =
                    providerAccountUtil.createEmptyComputeProviderAccount();

                StorageProviderType primaryStorageType =
                    accountCreationInfo.getPrimaryStorageProviderType();
                int primaryStorageProviderAccountId =
                    providerAccountUtil.
                        createEmptyStorageProviderAccount(primaryStorageType);

                Set<Integer> secondaryStorageProviderAccountIds =
                    new HashSet<Integer>();
                for(StorageProviderType storageType :
                    accountCreationInfo.getSecondaryStorageProviderTypes()) {
                    int id = providerAccountUtil.
                        createEmptyStorageProviderAccount(storageType);
                    secondaryStorageProviderAccountIds.add(id);
                }

                // Empty set for now. Will want to provide a way for users to
                // specify service repos, but likely not on account creation.
                Set<Integer> secondaryServiceRepositoryIds =
                    new HashSet<Integer>();

                ServerDetails serverDetails =
                    new ServerDetails(serverDetailsId,
                                      computeProviderAccountId,
                                      primaryStorageProviderAccountId,
                                      secondaryStorageProviderAccountIds,
                                      secondaryServiceRepositoryIds,
                                      accountCreationInfo.getServicePlan());

                getServerDetailsRepo().save(serverDetails);
            }

            // TODO: Hook up to payment data
            int paymentInfoId = -1;

            AccountInfo newAccountInfo =
                new AccountInfo(acctId,
                                accountCreationInfo.getSubdomain(),
                                accountCreationInfo.getAcctName(),
                                accountCreationInfo.getOrgName(),
                                accountCreationInfo.getDepartment(),
                                paymentInfoId,
                                serverDetailsId,
                                status,
                                accountType);

            getAccountRepo().save(newAccountInfo);

            userService.setUserRights(acctId, owner.getId(), Role.ROLE_OWNER);
            return accountServiceFactory.getAccount(newAccountInfo);
            
        } catch (DBConcurrentUpdateException ex) {
            throw new Error(ex);
        }
    }

    @Override
    public AccountService getAccount(int accountId)
        throws AccountNotFoundException {
        return accountServiceFactory.getAccount(accountId);
    }

    @Override
    public Set<AccountInfo> findAccountsByUserId(int userId) {
        Set<AccountRights> userRights = null;
        Set<AccountInfo> userAccounts = null;
        try {
            userRights = getRightsRepo().findByUserId(userId);
            userAccounts = new HashSet<AccountInfo>();
            for (AccountRights rights : userRights) {
                userAccounts.add(
                    getAccountRepo().findById(rights.getAccountId()));
            }
            return userAccounts;
        } catch (DBNotFoundException e) {
            log.info("No accounts found for user {}", userId);
        }

        return new HashSet<AccountInfo>();
    }

    @Override
    public boolean subdomainAvailable(String subdomain) {
        try {
            AccountInfo acct = getAccountRepo().findBySubdomain(subdomain);
            return false;
        } catch (DBNotFoundException e) {
            return true;
        }
    }

    private DuracloudAccountRepo getAccountRepo() {
        return repoMgr.getAccountRepo();
    }

    private DuracloudRightsRepo getRightsRepo() {
        return repoMgr.getRightsRepo();
    }

    private DuracloudServerDetailsRepo getServerDetailsRepo() {
        return repoMgr.getServerDetailsRepo();
    }

    private IdUtil getIdUtil() {
        return repoMgr.getIdUtil();
    }

}
