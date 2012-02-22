/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountCluster;
import org.duracloud.account.db.DuracloudAccountClusterRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.util.AccountClusterService;
import org.duracloud.account.util.util.AccountClusterUtil;
import org.duracloud.common.error.DuraCloudRuntimeException;

/**
 * @author: Bill Branan
 * Date: 2/22/12
 */
public class AccountClusterServiceImpl implements AccountClusterService {

    private AccountCluster cluster;
    private DuracloudRepoMgr repoMgr;
    private AccountClusterUtil clusterUtil;

    public AccountClusterServiceImpl(AccountCluster cluster,
                                     DuracloudRepoMgr repoMgr,
                                     AccountClusterUtil clusterUtil) {
        this.cluster = cluster;
        this.repoMgr = repoMgr;
        this.clusterUtil = clusterUtil;
    }

    @Override
    public AccountCluster retrieveAccountCluster() {
        return cluster;
    }

    @Override
    public void renameAccountCluster(String clusterName) {
        DuracloudAccountClusterRepo clusterRepo =
            repoMgr.getAccountClusterRepo();
        try {
            cluster.setClusterName(clusterName);
            clusterRepo.save(cluster);
        } catch(DBConcurrentUpdateException e) {
            String msg = "Error encountered attempting to update the name of " +
                "account cluster with ID " + cluster.getId() + ": " +
                e.getMessage();
            throw new DuraCloudRuntimeException(msg, e);
        }
    }

    @Override
    public void addAccountToCluster(int accountId) {
        // Add cluster to account
        clusterUtil.setAccountCluster(accountId, cluster.getId());

        // Add account to cluster
        clusterUtil.addAccountToCluster(accountId, cluster.getId());
    }

    @Override
    public void removeAccountFromCluster(int accountId) {
        // Remove cluster from account
        clusterUtil.setAccountCluster(accountId, -1);

        // Remove account from cluster
        clusterUtil.removeAccountFromCluster(accountId, cluster.getId());
    }

}
