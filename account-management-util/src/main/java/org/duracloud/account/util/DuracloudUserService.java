/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.db.error.UserAlreadyExistsException;
import org.duracloud.account.util.error.InvalidRedemptionCodeException;

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

    public void grantUserRights(int acctId, int userId);

    public void revokeUserRights(int acctId, int userId);

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

    /**
     * Redeems an invitation to add this user to a DuraCloud account.
     *
     * @param userId the id of the user which will be added to the account
     *               indicated in the invitation
     * @param redemptionCode code which was sent to the user as part of the
     *                       invitation to become part of an account
     * @return the account id associated with the newly redeemed invitation.
     */
    public int redeemAccountInvitation(int userId,
                                        String redemptionCode)
        throws InvalidRedemptionCodeException;
    
}
