/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.repo;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.AccountRights;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.model.Role;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author Erik Paulsson
 *         Date: 7/8/13
 */
public class DuracloudRightsRepoImpl implements DuracloudRightsRepoCustom {

    @Autowired
    private DuracloudRightsRepo rightsRepo;

    @Autowired
    private DuracloudUserRepo userRepo;

    @Autowired
    private DuracloudAccountRepo accountRepo;

    @Override
    public Set<AccountRights> findByAccountIdCheckRoot(Long acctId) {
        Set<AccountRights> acctRights = doFindByAccountId(acctId);
        Set<AccountRights> rootRights = findRootAccountRights(acctId);

        // Add user rights to root rights list
        for (AccountRights acctRight : acctRights) {
            if (!inRootRights(acctRight, rootRights)) {
                rootRights.add(acctRight);
            }
        }

        return rootRights;
    }

    @Override
    public Set<AccountRights> findByUserIdCheckRoot(Long userId) {
        List<AccountRights> rightsList = rightsRepo.findByUserId(userId);
        Set<AccountRights> rights = new HashSet(rightsList);
        if(isRootRights(rights)) {
            Set<Role> rootRoles = Role.ROLE_ROOT.getRoleHierarchy();
            rights = getAllAccountRights(userId, rootRoles);
        }
        return rights;
    }

    @Override
    public AccountRights findByAccountIdAndUserIdCheckRoot(Long accountId, Long userId) {
        AccountRights rights = rightsRepo.findByAccountIdAndUserId(accountId, userId);
        if(rights == null) {
            rights = getRightsIfUserIsRoot(accountId, userId);
        }
        return rights;
    }

    @Override
    public Set<AccountRights> findByAccountIdSkipRoot(Long accountId) {
        return doFindByAccountId(accountId);
    }

    private Set<AccountRights> doFindByAccountId(Long acctId) {
        return new HashSet(rightsRepo.findByAccountId(acctId));
    }

    private boolean isRootRights(Set<AccountRights> rights) {
        for (AccountRights r : rights) {
            if (r.getRoles().contains(Role.ROLE_ROOT)) {
                return true;
            }
        }
        return false;
    }

    private boolean inRootRights(AccountRights userRight,
                                 Set<AccountRights> rootRights) {
        for (AccountRights rootRight : rootRights) {
            if (userRight.getUser().getId().equals(rootRight.getUser().getId()) &&
                    userRight.getAccount().getId().equals(rootRight.getAccount().getId())) {
                return true;
            }
        }
        return false;
    }

    private Set<AccountRights> getAllAccountRights(Long userId,
                                                   Set<Role> rootRoles) {
        DuracloudUser user = userRepo.findOne(userId);
        List<AccountRights > allRights = rightsRepo.findAll();
        for (AccountRights r : allRights) {
            r.setUser(user);
            r.setRoles(rootRoles);
        }
        return new HashSet(allRights);
    }

    private AccountRights getRightsIfUserIsRoot(Long accountId, Long userId) {
        AccountRights rootRights = null;
        Set<AccountRights> rights = doFindByAccountId(accountId);
        if (rights.size() > 0) { // account exists
            for (AccountRights root : findRootAccountRights(accountId)) {
                // was the target user a root user?
                if (root.getUser().getId().equals(userId)) {
                    AccountInfo accountInfo = accountRepo.findOne(accountId);
                    rootRights = root;
                    rootRights.setAccount(accountInfo);
                    break;
                }
            }
        }
        return rootRights;
    }

    private Set<AccountRights> findRootAccountRights(Long accountId) {
        List<AccountRights> rootAccountRights = rightsRepo.findByAccountId(0L);
        AccountInfo account = accountRepo.findOne(accountId);
        Set<AccountRights> rootRights = new HashSet<AccountRights>();
        for(AccountRights rights: rootAccountRights) {
            if(rights.getRoles().contains(Role.ROLE_ROOT)) {
                rights.setAccount(account);
                rootRights.add(rights);
            }
        }
        return rootRights;
    }

    @Override
    public AccountRights findAccountRightsForUser(Long accountId,
                                                  Long userId) {
        AccountRights accountRights = null;
        AccountInfo account = accountRepo.findOne(accountId);
        List<AccountRights> rights =  rightsRepo.findByUserId(userId);
        for(AccountRights r: rights) {
            //Is user root?
            if(r.getRoles().contains(Role.ROLE_ROOT)) {
                accountRights = r;
                accountRights.setAccount(account);
                break;
            } else if(r.getAccount().equals(account)) {
                accountRights = r;
            }
        }
        return accountRights;
    }


}
