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
     *
     * @param userId of user
     * @return set of rights
     * @throws org.duracloud.account.db.error.DBNotFoundException if no item found
     */
    public Set<AccountRights> findByUserId(int userId) throws DBNotFoundException;

    /**
     * This method returns the set of rights for a given account
     *
     * @param accountId of account
     * @return set of rights
     * @throws org.duracloud.account.db.error.DBNotFoundException if no item found
     */
    public Set<AccountRights> findByAccountId(int accountId) throws DBNotFoundException;

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
     * This method returns the set of rights for a given account not including
     * the root users
     *
     * @param accountId of account
     * @return set of rights
     * @throws org.duracloud.account.db.error.DBNotFoundException if no item found
     */
    public Set<AccountRights> findByAccountIdSkipRoot(int accountId) throws DBNotFoundException;

}
