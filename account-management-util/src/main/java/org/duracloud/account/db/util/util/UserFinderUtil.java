/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.AccountRights;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.repo.DuracloudRightsRepo;
import org.duracloud.account.db.repo.DuracloudUserRepo;

/**
 * @author: Bill Branan Date: 2/17/12
 */
public class UserFinderUtil {

    private DuracloudRepoMgr repoMgr;

    public UserFinderUtil(DuracloudRepoMgr repoMgr) {
        this.repoMgr = repoMgr;
    }

    /**
     * Retrieves the users associated with the account
     * 
     * @param account
     *            from which the cluster it to be determined
     * @return the set of users associated with all accounts in the cluster
     */
    public Set<DuracloudUser> getAccountUsers(AccountInfo account) {

        DuracloudRightsRepo rightsRepo = repoMgr.getRightsRepo();
        DuracloudUserRepo userRepo = repoMgr.getUserRepo();

        Set<DuracloudUser> users = new HashSet<DuracloudUser>();
        List<AccountRights> acctRights = rightsRepo.findByAccountId(account
                .getId());
        for (AccountRights rights : acctRights) {
            DuracloudUser user = rights.getUser();

            // make sure only the rights for this account are set
            user.getAccountRights().clear();
            user.getAccountRights().add(rights);

            users.add(user);
        }
        return users;
    }

}
