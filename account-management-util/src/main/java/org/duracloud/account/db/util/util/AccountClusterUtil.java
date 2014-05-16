/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.util;


import org.duracloud.account.db.model.AccountCluster;
import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.AccountRights;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.repo.DuracloudRightsRepo;
import org.duracloud.account.db.repo.DuracloudUserRepo;

import java.util.HashSet;
import java.util.List;
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
     */
    public Set<DuracloudUser> getAccountClusterUsers(AccountInfo account) {

        Set<Long> allAccountIds = getClusterAccountIds(account);

        DuracloudRightsRepo rightsRepo = repoMgr.getRightsRepo();
        DuracloudUserRepo userRepo = repoMgr.getUserRepo();

        Set<DuracloudUser> users = new HashSet<DuracloudUser>();
        for(Long clusterAccountId : allAccountIds) {
            List<AccountRights> acctRights =
                rightsRepo.findByAccountId(clusterAccountId);
            for(AccountRights rights : acctRights) {
                DuracloudUser user = rights.getUser();

                // make sure only the rights for this account are set
                user.getAccountRights().clear();
                user.getAccountRights().add(rights);

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
     */
    public Set<Long> getClusterAccountIds(AccountInfo account) {

        Set<Long> allAccountIds = new HashSet<Long>();
        allAccountIds.add(account.getId());

        // Determine all accounts in the cluster (if applicable)
        AccountCluster cluster = account.getAccountCluster();
        if(cluster != null) {
            Set<AccountInfo> clusterAccts = cluster.getClusterAccounts();
            for(AccountInfo accountInfo: clusterAccts) {
                allAccountIds.add(accountInfo.getId());
            }
        }
        return allAccountIds;
    }

}
