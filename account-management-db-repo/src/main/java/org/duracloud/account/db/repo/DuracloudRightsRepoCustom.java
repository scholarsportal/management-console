/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.repo;

import org.duracloud.account.db.model.AccountRights;

import java.util.Set;

/**
 * @author Erik Paulsson
 *         Date: 7/8/13
 */
public interface DuracloudRightsRepoCustom {

    /**
     * This method returns the set of rights for a given user
     * The set may be of 0 length
     *
     * @param userId of user
     * @return set of rights
     */
    public Set<AccountRights> findByUserIdCheckRoot(Long userId);

    public Set<AccountRights> findByAccountIdCheckRoot(Long accountId);

    public AccountRights findByAccountIdAndUserIdCheckRoot(Long accountId, Long userId);

    /**
     * This method returns the set of rights for a given user in a given account.
     * If the user is root then those rights will be returned no matter what.
     *
     * @param accountId
     * @param userId
     * @return rights
     */
    public AccountRights findAccountRightsForUser(Long accountId,
                                                  Long userId);

    /**
     * This method returns the set of rights for a given account not including
     * the root users
     * The set may be of 0 length
     *
     * @param accountId of account
     * @return set of rights
     */
    public Set<AccountRights> findByAccountIdSkipRoot(Long accountId);

}
