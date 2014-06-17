/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.impl;

import org.apache.commons.lang.StringUtils;
import org.duracloud.account.db.model.*;
import org.duracloud.account.db.model.util.AccountCreationInfo;
import org.duracloud.account.db.repo.DuracloudAccountClusterRepo;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.util.*;
import org.duracloud.account.db.util.error.AccountClusterNotFoundException;
import org.duracloud.account.db.util.error.AccountNotFoundException;
import org.duracloud.account.db.util.error.SubdomainAlreadyExistsException;
import org.duracloud.account.db.util.sys.EventMonitor;
import org.duracloud.account.db.util.usermgmt.UserDetailsPropagator;
import org.duracloud.account.db.util.util.AccountClusterUtil;
import org.duracloud.computeprovider.domain.ComputeProviderType;
import org.duracloud.storage.domain.StorageProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
    private AccountClusterUtil clusterUtil;
    private UserDetailsPropagator propagator;

    public AccountManagerServiceImpl(DuracloudRepoMgr duracloudRepoMgr,
                                     DuracloudUserService duracloudUserService,
                                     AccountServiceFactory accountServiceFactory,
                                     AccountClusterUtil clusterUtil,
                                     UserDetailsPropagator propagator,
                                     Set<EventMonitor> eventMonitors) {
        this.repoMgr = duracloudRepoMgr;
        this.userService = duracloudUserService;
        this.accountServiceFactory = accountServiceFactory;
        this.clusterUtil = clusterUtil;
        this.propagator = propagator;
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

        ServerDetails serverDetails = null;
        Long clusterId = accountCreationInfo.getAccountClusterId();
        AccountInfo.AccountStatus status = AccountInfo.AccountStatus.ACTIVE;

        AccountType accountType = accountCreationInfo.getAccountType();
        if(AccountType.FULL.equals(accountType)) {
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

                secondaryStorageProviderAccounts.add(storageProviderAccount);
            }

            serverDetails = new ServerDetails();
            serverDetails.setComputeProviderAccount(computeProviderAccount);
            serverDetails.setPrimaryStorageProviderAccount(primaryStorageProviderAccount);
            serverDetails.setSecondaryStorageProviderAccounts(secondaryStorageProviderAccounts);

            repoMgr.getServerDetailsRepo().save(serverDetails);
        }

        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setSubdomain(accountCreationInfo.getSubdomain());
        accountInfo.setAcctName(accountCreationInfo.getAcctName());
        accountInfo.setOrgName(accountCreationInfo.getOrgName());
        accountInfo.setDepartment(accountCreationInfo.getDepartment());
        accountInfo.setServerDetails(serverDetails);
        accountInfo.setStatus(status);
        accountInfo.setType(accountType);
        if(clusterId != null && clusterId > -1L) {
            AccountCluster accountCluster = repoMgr.getAccountClusterRepo().findOne(clusterId);
            accountInfo.setAccountCluster(accountCluster);
        }

        accountInfo = repoMgr.getAccountRepo().save(accountInfo);

        userService.setUserRights(accountInfo.getId(), owner.getId(), Role.ROLE_OWNER);

        return accountServiceFactory.getAccount(accountInfo);
    }

    @Override
    public AccountService getAccount(Long accountId)
        throws AccountNotFoundException {
        return accountServiceFactory.getAccount(accountId);
    }

    @Override
    public Set<AccountInfo> findAccountsByUserId(Long userId) {
        List<AccountRights> userRights = repoMgr.getRightsRepo().findByUserId(userId);;
        Set<AccountInfo> userAccounts = new HashSet<AccountInfo>();
        userAccounts = new HashSet<AccountInfo>();
        for (AccountRights rights : userRights) {
            userAccounts.add(rights.getAccount());
        }
        return userAccounts;
    }

    @Override
    public boolean subdomainAvailable(String subdomain) {
        AccountInfo accountInfo = repoMgr.getAccountRepo().findBySubdomain(subdomain);
        return accountInfo == null;
    }

    @Override
    public AccountClusterService getAccountCluster(Long accountClusterId)
        throws AccountClusterNotFoundException {
        if(accountClusterId == null || accountClusterId < 0) {
            throw new AccountClusterNotFoundException(accountClusterId);
        }
        DuracloudAccountClusterRepo clusterRepo = repoMgr.getAccountClusterRepo();
        AccountCluster cluster = clusterRepo.findOne(accountClusterId);
        return new AccountClusterServiceImpl(cluster,
                                             repoMgr,
                                             clusterUtil,
                                             propagator);
    }

    @Override
    public AccountClusterService createAccountCluster(String clusterName) {
        AccountCluster cluster = new AccountCluster();
        cluster.setClusterName(clusterName);
        cluster = repoMgr.getAccountClusterRepo().save(cluster);
        return new AccountClusterServiceImpl(cluster,
                                             repoMgr,
                                             clusterUtil,
                                             propagator);
    }

    @Override
    public Set<AccountCluster> listAccountClusters(String filter) {
        DuracloudAccountClusterRepo clusterRepo = repoMgr.getAccountClusterRepo();
        HashSet<AccountCluster> set = new HashSet<AccountCluster>();
        List<AccountCluster > allClusters = clusterRepo.findAll();
        for(AccountCluster cluster: allClusters){
            String name = cluster.getClusterName();
            if(StringUtils.isBlank(filter) || name.startsWith(filter.trim())){
                set.add(cluster);
            }
        }
        return set;
    }
}
