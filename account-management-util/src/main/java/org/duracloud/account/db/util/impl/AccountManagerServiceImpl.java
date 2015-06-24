/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.AccountRights;
import org.duracloud.account.db.model.ComputeProviderAccount;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.model.ServerDetails;
import org.duracloud.account.db.model.StorageProviderAccount;
import org.duracloud.account.db.model.util.AccountCreationInfo;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.util.AccountManagerService;
import org.duracloud.account.db.util.AccountService;
import org.duracloud.account.db.util.AccountServiceFactory;
import org.duracloud.account.db.util.error.AccountNotFoundException;
import org.duracloud.account.db.util.error.SubdomainAlreadyExistsException;
import org.duracloud.account.db.util.sys.EventMonitor;
import org.duracloud.computeprovider.domain.ComputeProviderType;
import org.duracloud.storage.domain.StorageProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */

public class AccountManagerServiceImpl implements AccountManagerService {

    private Logger log =
        LoggerFactory.getLogger(AccountManagerServiceImpl.class);

    private DuracloudRepoMgr repoMgr;
    private AccountServiceFactory accountServiceFactory;
    private Set<EventMonitor> eventMonitors;

    public AccountManagerServiceImpl(DuracloudRepoMgr duracloudRepoMgr,
                                     AccountServiceFactory accountServiceFactory,
                                     Set<EventMonitor> eventMonitors) {
        this.repoMgr = duracloudRepoMgr;
        this.accountServiceFactory = accountServiceFactory;
        this.eventMonitors = eventMonitors;
    }

    @Override
    public AccountService createAccount(AccountCreationInfo accountCreationInfo)
        throws SubdomainAlreadyExistsException {
        log.info("Creating account with subdomain {}",
                 accountCreationInfo.getSubdomain());

        AccountService acctService = doCreateAccount(accountCreationInfo);

        // Notify monitors if account created successfully.
        Iterator<EventMonitor> itr = eventMonitors.iterator();
        while (itr.hasNext()) {
            try {
                itr.next().accountCreated(accountCreationInfo);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return acctService;
    }

    private synchronized AccountService doCreateAccount(AccountCreationInfo accountCreationInfo)
        throws SubdomainAlreadyExistsException {
        if (!subdomainAvailable(accountCreationInfo.getSubdomain())) {
            throw new SubdomainAlreadyExistsException();
        }

        ServerDetails serverDetails = null;
        AccountInfo.AccountStatus status = AccountInfo.AccountStatus.ACTIVE;

        status = AccountInfo.AccountStatus.PENDING;

        ComputeProviderAccount computeProviderAccount = new ComputeProviderAccount();
        computeProviderAccount.setProviderType(ComputeProviderType.AMAZON_EC2);
        computeProviderAccount = repoMgr.getComputeProviderAccountRepo()
                .save(computeProviderAccount);

        StorageProviderType primaryStorageType =
            accountCreationInfo.getPrimaryStorageProviderType();
        StorageProviderAccount primaryStorageProviderAccount = new StorageProviderAccount();
        primaryStorageProviderAccount.setProviderType(primaryStorageType);
        primaryStorageProviderAccount.setRrs(true);
        primaryStorageProviderAccount = repoMgr.getStorageProviderAccountRepo()
                .save(primaryStorageProviderAccount);

        Set<StorageProviderAccount> secondaryStorageProviderAccounts =
            new HashSet<StorageProviderAccount>();
        for(StorageProviderType storageType :
            accountCreationInfo.getSecondaryStorageProviderTypes()) {
            StorageProviderAccount storageProviderAccount = new StorageProviderAccount();
            storageProviderAccount.setProviderType(storageType);
            storageProviderAccount.setRrs(true);
            storageProviderAccount = repoMgr.getStorageProviderAccountRepo()
                    .save(storageProviderAccount);
            secondaryStorageProviderAccounts.add(storageProviderAccount);
        }

        
        serverDetails = new ServerDetails();
        serverDetails.setComputeProviderAccount(computeProviderAccount);
        serverDetails.setPrimaryStorageProviderAccount(primaryStorageProviderAccount);
        serverDetails.setSecondaryStorageProviderAccounts(secondaryStorageProviderAccounts);

        repoMgr.getServerDetailsRepo().save(serverDetails);

        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setSubdomain(accountCreationInfo.getSubdomain());
        accountInfo.setAcctName(accountCreationInfo.getAcctName());
        accountInfo.setOrgName(accountCreationInfo.getOrgName());
        accountInfo.setDepartment(accountCreationInfo.getDepartment());
        accountInfo.setServerDetails(serverDetails);
        accountInfo.setStatus(status);

        accountInfo = repoMgr.getAccountRepo().save(accountInfo);

        return accountServiceFactory.getAccount(accountInfo);
    }

    @Override
    public AccountService getAccount(Long accountId)
        throws AccountNotFoundException {
        return accountServiceFactory.getAccount(accountId);
    }

    @Override
    public Set<AccountInfo> findAccountsByUserId(Long userId) {
        
        DuracloudUser user = repoMgr.getUserRepo().findOne(userId);
        if(user.isRoot()){
            return new HashSet<>(repoMgr.getAccountRepo().findAll());
        }else{
            List<AccountRights> userRights = repoMgr.getRightsRepo().findByUserId(userId);
            Set<AccountInfo> userAccounts = new HashSet<>();
            for (AccountRights rights : userRights) {
                userAccounts.add(rights.getAccount());
            }
            return userAccounts;
        }
    }

    @Override
    public boolean subdomainAvailable(String subdomain) {
        AccountInfo accountInfo = repoMgr.getAccountRepo().findBySubdomain(subdomain);
        return accountInfo == null;
    }

}
