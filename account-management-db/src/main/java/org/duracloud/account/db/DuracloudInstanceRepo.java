/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db;

import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.db.error.DBNotFoundException;

import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: Oct 8, 2010
 */
public interface DuracloudInstanceRepo extends BaseRepo<DuracloudInstance> {

    /**
     * This method returns the set of instances associated with a given account
     *
     * @param accountId of account
     * @return set of instance IDs
     * @throws org.duracloud.account.db.error.DBNotFoundException if no item found
     */
    public Set<Integer> findByAccountId(int accountId) throws DBNotFoundException;

}
