/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.db.error.UserAlreadyExistsException;
import org.duracloud.account.util.error.InvalidPasswordException;
import org.duracloud.account.util.error.InvalidRedemptionCodeException;
import org.duracloud.account.util.error.InvalidUsernameException;
import org.duracloud.account.util.error.UnsentEmailException;
import org.springframework.security.access.annotation.Secured;

/**
 * This interface defines the contract for loading, storing, and managing
 * DuracloudUser entities.
 *
 * @author Andrew Woods Date: Oct 8, 2010
 */
public interface DuracloudUserService {

    /**
     * This method throws an exception is the username is non-unique or 
     * it is not a valid username.
     *
     * @param username sought
     * @return true if not already used
     */
    @Secured({"role:ROLE_ANONYMOUS, scope:ANY"})
    public void checkUsername(String username)
        throws InvalidUsernameException,
            UserAlreadyExistsException;

    /**
     * This method creates and persists a new user.
     *
     * @param username  of new user
     * @param password  of new user
     * @param firstName of new user
     * @param lastName  of new user
     * @param email     of new user
     * @param securityQuestion of new user
     * @param securityAnswer of new user
     * @return DuracloudUser
     * @throws DBConcurrentUpdateException
     * @throws UserAlreadyExistsException
     * @throws InvalidUsernameException 
     */
    @Secured({"role:ROLE_ANONYMOUS, scope:ANY"})
    public DuracloudUser createNewUser(String username,
                                       String password,
                                       String firstName,
                                       String lastName,
                                       String email,
                                       String securityQuestion,
                                       String securityAnswer)
        throws DBConcurrentUpdateException,
            UserAlreadyExistsException,
            InvalidUsernameException;

    /**
     * This method sets the roles of a user in an account.
     * <p/>
     * Note that this method only sets a user to new roles. To remove a user
     * from an account, use revokeAllRights().
     *
     * @return true if an update was performed.
     */
    @Secured({"role:ROLE_ADMIN, scope:SELF_ACCT_PEER_UPDATE"})
    public boolean setUserRights(int acctId, int userId, Role... roles);

    
    /**
     * This method sets the roles of a user in an account.
     * <p/>
     * Note that this method only sets a user to new roles. To remove a user
     * from an account, use revokeAllRights().
     *
     * @return true if an update was performed.
     */
    @Secured({"role:ROLE_ADMIN, scope:SELF_ACCT"})
    public boolean addUserToAccount(int acctId, int userId)
        throws DBNotFoundException;
    
    /**
     * This method removes all rights to an account for a given user.
     *
     * @param acctId on which rights will be revoked
     * @param userId of user whose rights will be revoked
     */
    @Secured({"role:ROLE_ADMIN, scope:SELF_ACCT_PEER"})
    public void revokeUserRights(int acctId, int userId);

    /**
     * This method changes the password of the user with the arg userId from
     * the oldPassword to the newPassword.
     *
     * @param userId             of user who is seeking a password change
     * @param oldPassword
     * @param oldPasswordEncoded flag noting if the password is hashed
     * @param newPassword
     * @throws DBNotFoundException
     * @throws InvalidPasswordException
     * @throws DBConcurrentUpdateException
     */
    @Secured({"role:ROLE_USER, scope:SELF_ID"})
    public void changePassword(int userId,
                               String oldPassword,
                               boolean oldPasswordEncoded,
                               String newPassword)
        throws DBNotFoundException, InvalidPasswordException, DBConcurrentUpdateException;


    /**
     * @param userId
     * @param oldPassword
     * @param oldPasswordEncoded
     * @param newPassword
     * @throws DBNotFoundException
     * @throws InvalidPasswordException
     * @throws DBConcurrentUpdateException
     */
    @Secured({"role:ROLE_ANONYMOUS, scope:ANY"})
    public void changePasswordInternal(int userId,
                               String oldPassword,
                               boolean oldPasswordEncoded,
                               String newPassword)
        throws DBNotFoundException, InvalidPasswordException, DBConcurrentUpdateException;

    
    /**
     * This method generates a random password, replaces the existing password
     * with the randomly generated one, and sends an email to the address on
     * file for the given username.
     *
     * @param username of user who forgot their password
     * @param securityQuestion of user who forgot their password
     * @param securityAnswer of user who forgot their password
     * @throws DBNotFoundException
     * @throws InvalidPasswordException
     * @throws DBConcurrentUpdateException
     * @throws UnsentEmailException
     */
    @Secured({"role:ROLE_ANONYMOUS, scope:ANY"})
    public void forgotPassword(String username,
                               String securityQuestion,
                               String securityAnswer)
        throws DBNotFoundException, InvalidPasswordException, DBConcurrentUpdateException, UnsentEmailException;

    /**
     * Returns a user password change invitation
     * @param redemptionCode
     * @return
     * @throws DBNotFoundException
     */
    @Secured({"role:ROLE_ANONYMOUS, scope:ANY"})
    UserInvitation retrievePassordChangeInvitation(String redemptionCode)
        throws DBNotFoundException;
    
    /**
     * This method loads a DuracloudUser from the persistence layer and
     * populate its rights info.
     *
     * @param username of user to load
     * @return DuracloudUser
     * @throws DBNotFoundException
     */
    @Secured({"role:ROLE_USER, scope:SELF_NAME"})
    public DuracloudUser loadDuracloudUserByUsername(String username)
        throws DBNotFoundException;

    @Secured({"role:ROLE_ANONYMOUS, scope:ANY"})
    public DuracloudUser loadDuracloudUserByUsernameInternal(String username)
        throws DBNotFoundException;

    @Secured({ "role:ROLE_USER, scope:ANY" })
    public DuracloudUser loadDuracloudUserByIdInternal(Integer userId)
        throws DBNotFoundException;

    /**
     * This method redeems an invitation to add this user to a DuraCloud
     * account.
     *
     * @param userId         the id of the user which will be added to the account
     *                       indicated in the invitation
     * @param redemptionCode code which was sent to the user as part of the invitation to
     *                       become part of an account
     * @return the account id associated with the newly redeemed invitation.
     */
    @Secured({"role:ROLE_USER, scope:SELF_ID"})
    public int redeemAccountInvitation(int userId, String redemptionCode)
        throws InvalidRedemptionCodeException;

    
    @Secured({"role:ROLE_ANONYMOUS, scope:ANY"})
    public void redeemPasswordChangeRequest(int userId, String redemptionCode)
        throws InvalidRedemptionCodeException;

    /**
     * This method persists the arg user details.
     *
     * @param userId    of user
     * @param firstName of user
     * @param lastName  of user
     * @param email     of user
     * @param securityQuestion of user
     * @param securityAnswer of user
     * @throws DBConcurrentUpdateException
     */
    @Secured({"role:ROLE_USER, scope:SELF_ID"})
    public void storeUserDetails(int userId,
                                 String firstName,
                                 String lastName,
                                 String email,
                                 String securityQuestion,
                                 String securityAnswer)
        throws DBNotFoundException, DBConcurrentUpdateException;

}
