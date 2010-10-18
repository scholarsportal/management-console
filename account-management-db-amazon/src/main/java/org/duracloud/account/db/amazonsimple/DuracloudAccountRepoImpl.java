/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
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
