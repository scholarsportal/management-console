/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db;

import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.db.error.DBNotFoundException;

import java.util.Set;

/**
 * @author: Bill Branan
 * Date: Dec 2, 2010
 */
public interface DuracloudRightsRepo extends BaseRepo<AccountRights> {

    /**
     * This method returns the set of rights for a given user
     * The set may be of 0 length
     *
     * @param userId of user
     * @return set of rights
     */
    public Set<AccountRights> findByUserId(int userId);

    /**
     * This method returns the set of rights for a given account
     * The set may be of 0 length
     *
     * @param accountId of account
     * @return set of rights
     */
    public Set<AccountRights> findByAccountId(int accountId);

    /**
     * This method returns the set of rights for a given user in a given account
     *
     * @param accountId of account
     * @param userId of user
     * @return rights
     * @throws org.duracloud.account.db.error.DBNotFoundException if no item found
     */
    public AccountRights findByAccountIdAndUserId(int accountId,
                                                  int userId) throws DBNotFoundException;

    /**
     * This method returns the set of rights for a given user in a given account.
     * If the user is root then those rights will be returned no matter what.
     *
     * @param accountId of account
     * @param userId of user
     * @return rights
     * @throws org.duracloud.account.db.error.DBNotFoundException if no item found
     */
    public AccountRights findAccountRightsForUser(int accountId,
                                                  int userId) throws DBNotFoundException;

    /**
     * This method returns the set of rights for a given account not including
     * the root users
     * The set may be of 0 length
     *
     * @param accountId of account
     * @return set of rights
     */
    public Set<AccountRights> findByAccountIdSkipRoot(int accountId);

}
