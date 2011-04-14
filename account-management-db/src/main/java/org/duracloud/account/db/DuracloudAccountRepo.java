/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.db.error.DBNotFoundException;

/**
 * @author Andrew Woods
 *         Date: Oct 8, 2010
 */
public interface DuracloudAccountRepo extends BaseRepo<AccountInfo> {

    /**
     * This method returns the account with the given subdomain
     *
     * @param subdomain of account
     * @return account info
     * @throws org.duracloud.account.db.error.DBNotFoundException if no item found
     */
    public AccountInfo findBySubdomain(String subdomain) throws DBNotFoundException;
    
}
