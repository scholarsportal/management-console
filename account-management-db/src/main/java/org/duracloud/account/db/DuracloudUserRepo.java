/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db;

import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.error.DBNotFoundException;

/**
 * @author Andrew Woods
 *         Date: Oct 8, 2010
 */
public interface DuracloudUserRepo extends BaseRepo<DuracloudUser> {

    /**
     * This method returns a single user with the given username
     *
     * @param username of user
     * @return user
     * @throws org.duracloud.account.db.error.DBNotFoundException if no item found
     */
    public DuracloudUser findByUsername(String username) throws DBNotFoundException;

}
