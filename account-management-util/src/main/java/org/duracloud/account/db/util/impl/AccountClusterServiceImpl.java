/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util.impl;

import org.duracloud.account.db.model.AccountCluster;
import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.repo.DuracloudAccountRepo;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.util.AccountClusterService;
import org.duracloud.account.db.util.usermgmt.UserDetailsPropagator;
import org.duracloud.account.db.util.util.AccountClusterUtil;

/**
 * @author: Bill Branan
 * Date: 2/22/12
 */
public class AccountClusterServiceImpl implements AccountClusterService {

    private AccountCluster cluster;
    private DuracloudRepoMgr repoMgr;
    private AccountClusterUtil clusterUtil;
    private UserDetailsPropagator propagator;

    public AccountClusterServiceImpl(AccountCluster cluster,
                                     DuracloudRepoMgr repoMgr,
                                     AccountClusterUtil clusterUtil,
                                     UserDetailsPropagator propagator) {
        this.cluster = cluster;
        this.repoMgr = repoMgr;
        this.clusterUtil = clusterUtil;
        this.propagator = propagator;
    }

    @Override
    public AccountCluster retrieveAccountCluster() {
        return cluster;
    }

    @Override
    public void renameAccountCluster(String clusterName) {
        cluster.setClusterName(clusterName);
        repoMgr.getAccountClusterRepo().save(cluster);
    }

    @Override
    public void addAccountToCluster(Long accountId) {
        DuracloudAccountRepo accountRepo = repoMgr.getAccountRepo();
        AccountInfo accountInfo = accountRepo.findOne(accountId);
        accountInfo.setAccountCluster(cluster);
        accountRepo.save(accountInfo);
    }

    @Override
    public void removeAccountFromCluster(Long accountId) {
        DuracloudAccountRepo accountRepo = repoMgr.getAccountRepo();
        AccountInfo accountInfo = accountRepo.findOne(accountId);
        accountInfo.setAccountCluster(null);
        accountRepo.save(accountInfo);

        // Propagate users/groups changes down to instances (removed account)
        propagator.propagateClusterUpdate(accountId, cluster.getId());

        // Propagate users/groups changes down to instances (cluster)
        propagator.propagateClusterUpdate(cluster.getId());
    }
}
