/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.error.UnsentEmailException;

import java.util.Set;

/**
 * An interface for the account management application administrator.
 *
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */

public interface RootAccountManagerService {

    /**
     * @param filter optional filter on org name
     * @return
     */
    public Set<AccountInfo> listAllAccounts(String filter);

    /**
     * @param filter optional filter on username
     * @return
     */
    public Set<DuracloudUser> listAllUsers(String filter);

    /**
     * 
     * @param imageId
     * @param version
     * @param description
     */
    public void addDuracloudImage(String imageId,
                                  String version,
                                  String description);

    /**
     * Delete a user from the system
     *
     * @param id
     */
    public void deleteUser(int id);

    /**
     * Reset a user's password
     *
     * @param userId
     * @throws DBNotFoundException
     * @throws DBConcurrentUpdateException
     * @throws UnsentEmailException
     */
    public void resetUsersPassword(int userId)
        throws DBNotFoundException, DBConcurrentUpdateException,
               UnsentEmailException;
}
