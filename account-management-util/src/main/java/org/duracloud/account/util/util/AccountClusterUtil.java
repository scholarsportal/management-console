/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.util;

import org.duracloud.account.common.domain.AccountCluster;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.DuracloudAccountClusterRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBNotFoundException;

import java.util.HashSet;
import java.util.Set;

/**
 * @author: Bill Branan
 * Date: 2/17/12
 */
public class AccountClusterUtil {

    private DuracloudRepoMgr repoMgr;

    public AccountClusterUtil(DuracloudRepoMgr repoMgr) {
        this.repoMgr = repoMgr;
    }

    /**
     * Retrieves the users associated with the all accounts in the cluster
     * where given account is a member.
     *
     * @param account from which the cluster it to be determined
     * @return the set of users associated with all accounts in the cluster
     * @throws org.duracloud.account.db.error.DBNotFoundException
     */
    public Set<DuracloudUser> getAccountClusterUsers(AccountInfo account)
        throws DBNotFoundException {

        Set<Integer> allAccountIds = getClusterAccountIds(account);

        DuracloudRightsRepo rightsRepo = repoMgr.getRightsRepo();
        DuracloudUserRepo userRepo = repoMgr.getUserRepo();

        Set<DuracloudUser> users = new HashSet<DuracloudUser>();
        for(int clusterAccountId : allAccountIds) {
            Set<AccountRights> acctRights =
                rightsRepo.findByAccountId(clusterAccountId);
            for(AccountRights rights : acctRights) {
                DuracloudUser user = userRepo.findById(rights.getUserId());
                user.setAccountRights(rights);
                users.add(user);
            }
        }
        return users;
    }

    /**
     * Retrieves the set of account IDs for the accounts which are a part
     * of the same cluster as the given account. If the given account is
     * not part of a cluster, only its ID will be in the returned set.
     *
     * @param account to check for cluster membership
     * @return set of IDs of all accounts in the same cluster as the given acct
     * @throws DBNotFoundException
     */
    public Set<Integer> getClusterAccountIds(AccountInfo account)
        throws DBNotFoundException {

        Set<Integer> allAccountIds = new HashSet<Integer>();
        allAccountIds.add(new Integer(account.getId()));

        // Determine all accounts in the cluster (if applicable)
        int clusterId = account.getAccountClusterId();
        if(clusterId > -1) {
            DuracloudAccountClusterRepo clusterRepo =
                repoMgr.getAccountClusterRepo();
            AccountCluster cluster = clusterRepo.findById(clusterId);
            Set<Integer> clusterAcctIds = cluster.getClusterAccountIds();
            allAccountIds.addAll(clusterAcctIds);
        }
        return allAccountIds;
    }

}
