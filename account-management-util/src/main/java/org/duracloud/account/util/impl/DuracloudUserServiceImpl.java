/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.InitUserCredential;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.common.domain.ServerImage;
import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudUserInvitationRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.IdUtil;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.db.error.DBUninitializedException;
import org.duracloud.account.db.error.UserAlreadyExistsException;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.error.AccountRequiresOwnerException;
import org.duracloud.account.util.error.InvalidPasswordException;
import org.duracloud.account.util.error.InvalidRedemptionCodeException;
import org.duracloud.account.util.error.UnsentEmailException;
import org.duracloud.account.util.notification.NotificationMgr;
import org.duracloud.account.util.usermgmt.UserDetailsPropagator;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.common.model.Credential;
import org.duracloud.common.util.ChecksumUtil;
import org.duracloud.notification.Emailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: Oct 9, 2010
 */
public class DuracloudUserServiceImpl implements DuracloudUserService, UserDetailsService {
    
	private Logger log = LoggerFactory.getLogger(DuracloudUserServiceImpl.class);

    private DuracloudRepoMgr repoMgr;
    private UserDetailsPropagator propagator;
    private NotificationMgr notificationMgr;
    
    public DuracloudUserServiceImpl(DuracloudRepoMgr duracloudRepoMgr,
                                    NotificationMgr notificationMgr,
                                    UserDetailsPropagator propagator) {
        this.repoMgr = duracloudRepoMgr;
        this.notificationMgr = notificationMgr;
        this.propagator = propagator;
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        if (isReservedName(username)) {
            return false;
        }

        try {
            getUserRepo().findByUsername(username);
            return false;
        } catch (DBNotFoundException e) {
            return true;
        }
    }

    private boolean isReservedName(String username) {
        Credential init = new InitUserCredential();
        return ServerImage.DC_ROOT_USERNAME.equalsIgnoreCase(username) ||
            init.getUsername().equalsIgnoreCase(username);
    }

    @Override
    public DuracloudUser createNewUser(String username,
                                       String password,
                                       String firstName,
                                       String lastName,
                                       String email)
        throws DBConcurrentUpdateException, UserAlreadyExistsException {
        ChecksumUtil util = new ChecksumUtil(ChecksumUtil.Algorithm.SHA_256);

        int newUserId = getIdUtil().newUserId();
        DuracloudUser user = new DuracloudUser(newUserId,
                                               username,
                                               util.generateChecksum(password),
                                               firstName,
                                               lastName,
                                               email
                                               );
        
        throwIfUserExists(user);
        getUserRepo().save(user);
        
        
        log.info("created new user [{}]", username);
        return user;
    }

    private void throwIfUserExists(DuracloudUser user)
        throws UserAlreadyExistsException {

        if (!isUsernameAvailable(user.getUsername())) {
            throw new UserAlreadyExistsException(user.getUsername());
        }
    }

    @Override
    public boolean setUserRights(int acctId, int userId, Role... roles) {
        Set<Role> roleSet = new HashSet<Role>();
        for (Role role : roles) {
            roleSet.add(role);
        }

        boolean result = doSetUserRights(acctId, userId, roleSet);
        if(result) {
            log.debug("Propagating user update for: " + acctId + ", " +
                      userId + ", " + asString(roleSet));
            propagator.propagateRights(acctId, userId, roleSet);
        }
        return result;
    }

    private boolean doSetUserRights(int acctId, int userId, Set<Role> roles) {
        if(null == roles) {
            throw new IllegalArgumentException("Role may not be null");
        }

        Set<Role> oldRoles = null;
        Set<Role> newRoles = new HashSet<Role>();
        for (Role role : roles) {
            newRoles.addAll(role.getRoleHierarchy());
        }

        DuracloudRightsRepo rightsRepo = getRightsRepo();
        AccountRights rights = null;
        try {
            rights = rightsRepo.findByAccountIdAndUserId(acctId, userId);
            oldRoles = rights.getRoles();

        } catch (DBNotFoundException e) {
            log.info("Will add new rights for " + userId + ", " + acctId);
        }

        boolean updatedNeeded = !newRoles.equals(oldRoles);
        if (updatedNeeded) {
            if(oldRoles != null &&
               oldRoles.contains(Role.ROLE_OWNER) &&
               !newRoles.contains(Role.ROLE_OWNER)) {

                verifyAccountOwnerExists(acctId, userId, rightsRepo);
            }
            
            retryUpdateRights(acctId, userId, newRoles, rights);
        }
        return updatedNeeded;
    }

    private void verifyAccountOwnerExists(int acctId,
                                          int userId,
                                          DuracloudRightsRepo rightsRepo) {
        try {
            Set<AccountRights> acctRightsSet =
                rightsRepo.findByAccountId(acctId);

            // Determine the list of root users
            Set<Integer> roots = new HashSet<Integer>();
            for(AccountRights acctRights : acctRightsSet) {
                if(acctRights.getRoles().contains(Role.ROLE_ROOT)) {
                    roots.add(acctRights.getUserId());
                }
            }

            // Determine the list of owners who are not root users
            Set<Integer> owners = new HashSet<Integer>();
            for(AccountRights acctRights : acctRightsSet) {
                if(acctRights.getRoles().contains(Role.ROLE_OWNER) &&
                   !roots.contains(acctRights.getUserId())) {
                    owners.add(acctRights.getUserId());
                }
            }

            // Ensure at least one non-root owner is maintained on the account
            if(owners.size() == 1 && owners.contains(userId)) {
                String err = "Cannot remove owner rights from user with ID " +
                    userId + " from account with ID " + acctId +
                    ". This account must maintain at least one owner.";
                throw new AccountRequiresOwnerException(err);
            }
        } catch(DBNotFoundException e) {
            log.error("Could not find rights associated with account " + acctId);
        }
    }

    private String asString(Set<Role> roles) {
        StringBuilder sb = new StringBuilder();
        for (Role role : roles) {
            sb.append(role.name());
            sb.append(",");
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }    

    private void retryUpdateRights(int acctId,
                                   int userId,
                                   Set<Role> roles,
                                   AccountRights rights) {
        int maxAttempts = 5;
        int attempts = 0;
        boolean success = false;
        while(!success && attempts < maxAttempts) {
            try {
                saveRights(acctId, userId, roles, rights);
                success = true;
            } catch(DBConcurrentUpdateException e) {
            }
            attempts++;
        }

        if(!success && attempts >= maxAttempts) {
            String error = "Failure attempting to update user with ID" +
                           userId + " to roles " + roles +
                           " for Account with ID" + acctId;
            throw new DuraCloudRuntimeException(error);
        }
    }

    private void saveRights(int acctId,
                            int userId,
                            Set<Role> roles,
                            AccountRights rights)
        throws DBConcurrentUpdateException {
        DuracloudRightsRepo rightsRepo = getRightsRepo();

        int rightsId;
        int counter;
        if(null == rights) {
            rightsId = getIdUtil().newRightsId();
            counter = 0;
        } else {
            rightsId = rights.getId();
            counter = rights.getCounter();
        }

        rights = new AccountRights(rightsId, acctId, userId, roles, counter);
        rightsRepo.save(rights);
    }

    @Override
    public void revokeUserRights(int acctId, int userId) {
        doRevokeUserRights(acctId, userId);

        log.debug("Propagating revocation for: " + acctId + ", " + userId);
        propagator.propagateRevocation(acctId, userId);
    }

    public void doRevokeUserRights(int acctId, int userId) {
        DuracloudRightsRepo rightsRepo = getRightsRepo();
        verifyAccountOwnerExists(acctId, userId, rightsRepo);
        try {
            AccountRights rights =
                rightsRepo.findByAccountIdAndUserId(acctId, userId);
            getRightsRepo().delete(rights.getId());
        } catch (DBNotFoundException e) {
            // User has no rights in this account
        }
    }    

    @Override
    public void changePassword(int userId,
                               String oldPassword,
                               boolean oldPasswordEncoded,
                               String newPassword)
        throws DBNotFoundException,InvalidPasswordException,DBConcurrentUpdateException {
        if(null != newPassword && !newPassword.equals(oldPassword)) {
            ChecksumUtil util = new ChecksumUtil(ChecksumUtil.Algorithm.SHA_256);

            DuracloudUser user = getUserRepo().findById(userId);
            if(!oldPasswordEncoded) {
                oldPassword = util.generateChecksum(oldPassword);
            }
            if(!user.getPassword().equals(oldPassword)){
                throw new InvalidPasswordException(userId);
            }

            user.setPassword(util.generateChecksum(newPassword));
            getUserRepo().save(user);

            log.debug("Propagating update for user: " + userId);
            Set<AccountRights> rightsSet =
                repoMgr.getRightsRepo().findByUserId(userId);
            // Propagate changes for each of the user's accounts
            for(AccountRights rights : rightsSet) {
                propagator.propagatePasswordUpdate(rights.getAccountId(),
                                                   userId);
            }
        }
    }

    @Override
    public void forgotPassword(String username)
        throws DBNotFoundException, InvalidPasswordException,
               DBConcurrentUpdateException, UnsentEmailException {
        DuracloudUser user = loadDuracloudUserByUsernameInternal(username);

        Random r = new Random();
        String generatedPassword = Long.toString(Math.abs(r.nextLong()), 36);

        changePassword(user.getId(),
                       user.getPassword(),
                       true,
                       generatedPassword);

        try {
            Emailer emailer = notificationMgr.getEmailer();

            emailer.send("Duracloud Account Management - Forgot Password",
                         "Please use the following password to login: "
                         + generatedPassword, user.getEmail());
        } catch (Exception e) {
            String msg =
                "Error: Unable to send email to user: " + username;
            log.error(msg);
            throw new UnsentEmailException(msg, e);
        }
    }

    @Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
        try {
            return loadDuracloudUserByUsername(username);
        } catch(DBNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
		} catch (DBUninitializedException ue) {
            log.warn("UserRepo is uninitialized");
            throw new UsernameNotFoundException(ue.getMessage());
        }
    }

    @Override
    public DuracloudUser loadDuracloudUserByUsername(String username)
        throws DBNotFoundException {
        return loadDuracloudUserByUsernameInternal(username);
    }

    @Override
    public DuracloudUser loadDuracloudUserByUsernameInternal(String username)
        throws DBNotFoundException {
        DuracloudUser user = getUserRepo().findByUsername(username);

        try {
            user.setAccountRights(getRightsRepo().findByUserId(user.getId()));
        } catch (DBNotFoundException e) {
            // Not all users are associated with an account.
            log.debug("No account rights found for {}", user.getUsername());
        }

        return user;
    }

    @Override
    public int redeemAccountInvitation(int userId, String redemptionCode)
        throws InvalidRedemptionCodeException {

        DuracloudUserInvitationRepo invRepo = repoMgr.getUserInvitationRepo();

        // Retrieve the invitation
        UserInvitation invitation;
        try {
            invitation = invRepo.findByRedemptionCode(redemptionCode);
        } catch(DBNotFoundException e) {
            throw new InvalidRedemptionCodeException(redemptionCode);
        }

        // Add the user to the account if they are not already a member
        if(!userHasAccountRights(userId, invitation.getAccountId())) {
            setUserRights(invitation.getAccountId(), userId, Role.ROLE_USER);
        }

        // Delete the invitation
        invRepo.delete(invitation.getId());

        //return accountId
        return invitation.getAccountId();
    }

    private boolean userHasAccountRights(int userId, int accountId) {
        DuracloudRightsRepo rightsRepo = repoMgr.getRightsRepo();
        try {
            rightsRepo.findByAccountIdAndUserId(accountId, userId);
            return true;
        } catch(DBNotFoundException e) {
            return false;
        }
    }

    private DuracloudUserRepo getUserRepo() {
        return repoMgr.getUserRepo();
    }

    private DuracloudRightsRepo getRightsRepo() {
        return repoMgr.getRightsRepo();
    }

    private IdUtil getIdUtil() {
        return repoMgr.getIdUtil();
    }

    @Override
    public void storeUserDetails(
        int userId, String firstName, String lastName, String email)
        throws DBNotFoundException, DBConcurrentUpdateException {
        DuracloudUser user = getUserRepo().findById(userId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        getUserRepo().save(user);
    }

}
