/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.mockimpl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;

import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: Dec 8, 2010
 */
public class MockDuracloudRightsRepo implements DuracloudRightsRepo {
    @Override
    public Set<AccountRights> findByUserId(int userId)
        throws DBNotFoundException {
        // Default method body
        return null;
    }

    @Override
    public Set<AccountRights> findByAccountId(int accountId)
        throws DBNotFoundException {
        // Default method body
        return null;
    }

    @Override
    public AccountRights findByAccountIdAndUserId(int accountId, int userId)
        throws DBNotFoundException {
        // Default method body
        return null;
    }

    @Override
    public AccountRights findById(int id) throws DBNotFoundException {
        // Default method body
        return null;
    }

    @Override
    public void save(AccountRights item) throws DBConcurrentUpdateException {
        // Default method body

    }

    @Override
    public Set<Integer> getIds() {
        // Default method body
        return null;
    }
}
