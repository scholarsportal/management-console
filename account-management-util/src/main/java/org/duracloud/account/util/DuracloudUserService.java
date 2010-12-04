/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.db.error.UserAlreadyExistsException;

/**
 * @author Andrew Woods
 *         Date: Oct 8, 2010
 */
public interface DuracloudUserService {

    public boolean isUsernameAvailable(String username);

    public DuracloudUser createNewUser(String username,
                                       String password,
                                       String firstName,
                                       String lastName,
                                       String email)
        throws DBConcurrentUpdateException,
               UserAlreadyExistsException;

    public void addUserToAccount(int acctId, int userId);

    public void removeUserFromAccount(int acctId, int userId);

    public void grantAdminRights(int acctId, int userId);

    public void revokeAdminRights(int acctId, int userId);

	public void grantOwnerRights(int acctId, int userId);
    
	public void revokeOwnerRights(int acctId, int userId);

    public void sendPasswordReminder(int userId);

    public void changePassword(int userId,
                               String oldPassword,
                               String newPassword);

	public DuracloudUser loadDuracloudUserByUsername(String username)
			throws DBNotFoundException;
    
}
