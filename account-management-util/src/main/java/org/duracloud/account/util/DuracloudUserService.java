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

    public String createNewUser(String username,
                                String password,
                                String firstName,
                                String lastName,
                                String email)
        throws DBConcurrentUpdateException, DBNotFoundException, UserAlreadyExistsException;

    public void addUserToAccount(String acctId, String username)
        throws DBNotFoundException;

    public void removeUserFromAccount(String acctId, String username);

    public void grantAdminRights(String acctId, String username);

    public void revokeAdminRights(String acctId, String username);

    public void sendPasswordReminder(String username);

    public void changePassword(String username,
                               String oldPassword,
                               String newPassword);

	public DuracloudUser loadDuracloudUserByUsername(String username)
			throws DBNotFoundException;

	public void grantOwnerRights(String id, String username);
	public void revokeOwnerRights(String id, String username);
    
}
