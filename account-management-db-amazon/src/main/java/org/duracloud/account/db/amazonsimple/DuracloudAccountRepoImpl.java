/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.amazonsimple;

import java.util.List;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;

/**
 * @author Andrew Woods
 *         Date: Oct 10, 2010
 */
public class DuracloudAccountRepoImpl implements DuracloudAccountRepo {
    @Override
    public AccountInfo findById(String id) throws DBNotFoundException {
        // Default method body
        return null;
    }

    @Override
    public void save(AccountInfo item) throws DBConcurrentUpdateException {
        // Default method body

    }

    @Override
    public List<String> getIds() {
        // Default method body
        return null;
    }
}
