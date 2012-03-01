/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudGroup;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.InitUserCredential;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.common.domain.ServerImage;
import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.db.DuracloudGroupRepo;
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
import org.duracloud.account.util.error.InvalidUsernameException;
import org.duracloud.account.util.error.ReservedPrefixException;
import org.duracloud.account.util.error.ReservedUsernameException;
import org.duracloud.account.util.error.UnsentEmailException;
import org.duracloud.account.util.notification.NotificationMgr;
import org.duracloud.account.util.notification.Notifier;
import org.duracloud.account.util.usermgmt.UserDetailsPropagator;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.duracloud.common.model.Credential;
import org.duracloud.common.util.ChecksumUtil;
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
    private Notifier notifier;
    
    public DuracloudUserServiceImpl(DuracloudRepoMgr duracloudRepoMgr,
                                    NotificationMgr notificationMgr,
                                    UserDetailsPropagator propagator) {
        this.repoMgr = duracloudRepoMgr;
        this.notificationMgr = notificationMgr;
        this.propagator = propagator;
    }

    @Override
    public void checkUsername(String username)
        throws InvalidUsernameException,
            UserAlreadyExistsException {

        if (!isValidUsername(username)) {
            throw new InvalidUsernameException(username);
        }
        
        if (isReservedPrefix(username)) {
            throw new ReservedPrefixException(username);
        }

        if (isReservedName(username)) {
            throw new ReservedUsernameException(username);
        }

        try {
            getUserRepo().findByUsername(username);
            throw new UserAlreadyExistsException(username);
        } catch (DBNotFoundException e) {
            //do nothing - all's well
        }
    }

    private boolean isValidUsername(String username) {
        if(username == null){
            return false;
        }
        
        return username.matches("\\A(?![_.@\\-])[a-z0-9_.@\\-]+(?<![_.@\\-])\\Z");
        
    }

    private boolean isReservedName(String username) {
        Credential init = new InitUserCredential();
        return ServerImage.DC_ROOT_USERNAME.equalsIgnoreCase(username) ||
            init.getUsername().equalsIgnoreCase(username);
    }

    private boolean isReservedPrefix(String username) {
        if(username.startsWith(DuracloudGroup.PREFIX)){
            return true;
        }
        
        return false;
    }

    
    @Override
    public DuracloudUser createNewUser(String username,
                                       String password,
                                       String firstName,
                                       String lastName,
                                       String email,
                                       String securityQuestion,
                                       String securityAnswer)
        throws DBConcurrentUpdateException, UserAlreadyExistsException, InvalidUsernameException {


        checkUsername(username);
        
        ChecksumUtil util = new ChecksumUtil(ChecksumUtil.Algorithm.SHA_256);
        int newUserId = getIdUtil().newUserId();
        
        DuracloudUser user = new DuracloudUser(newUserId,
                                               username,
                                               util.generateChecksum(password),
                                               firstName,
                                               lastName,
                                               email,
                                               securityQuestion,
                                               securityAnswer
                                               );
        

        getUserRepo().save(user);

        log.info("New user created with username {}", username);
        
        getNotifier().sendNotificationCreateNewUser(user);

        return user;
    }


    @Override
    public boolean setUserRights(int acctId, int userId, Role... roles) {
        return setUserRightsInternal(acctId, userId, roles);
    }

    private boolean setUserRightsInternal(int acctId, int userId, Role... roles) {

        Set<Role> roleSet = new HashSet<Role>();
        for (Role role : roles) {
            roleSet.add(role);
        }

        log.info("Updating user rights for user {} on account {} to roles " +
                 asString(roleSet), userId, acctId);

        boolean result = doSetUserRights(acctId, userId, roleSet);
        if(result) {
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
            log.info("New rights will be added for user {} on account {}",
                     userId, acctId);
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
        log.info("Revoking rights for user {} on account {}", userId, acctId);

        doRevokeUserRights(acctId, userId);
        removeUserFromAccountGroups(acctId, userId);
        propagator.propagateRevocation(acctId, userId);
    }

    private void doRevokeUserRights(int acctId, int userId) {
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

    private void removeUserFromAccountGroups(int acctId, int userId) {
        DuracloudGroupRepo groupRepo = getGroupRepo();
        Set<DuracloudGroup> acctGroups = groupRepo.findByAccountId(acctId);
        for(DuracloudGroup group : acctGroups) {
            Set<Integer> groupUserIds = group.getUserIds();
            if(groupUserIds.contains(userId)) {
                groupUserIds.remove(userId);
                group.setUserIds(groupUserIds);
                try {
                    groupRepo.save(group);
                } catch (DBConcurrentUpdateException e) {
                    String error = "Could not remove user with ID " +
                        userId + " from group with ID " + group.getId() +
                        " due to a DBConcurrentUpdateException";
                    throw new DuraCloudRuntimeException(error, e);
                }
            }
        }
    }

    @Override
    public void changePassword(int userId,
                               String oldPassword,
                               boolean oldPasswordEncoded,
                               String newPassword)
        throws DBNotFoundException,InvalidPasswordException,DBConcurrentUpdateException {
        if(null != newPassword && !newPassword.equals(oldPassword)) {
            log.info("Changing password for user with ID {}", userId);

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

            propagateUserUpdate(userId, user.getUsername());
        }
    }

    private void propagateUserUpdate(int userId, String username) {
        Set<AccountRights> rightsSet =
            repoMgr.getRightsRepo().findByUserId(userId);
        // Propagate changes for each of the user's accounts
        if(!isUserRoot(rightsSet)) { // Do no propagate if user is root
            for(AccountRights rights : rightsSet) {
                propagator.propagateUserUpdate(rights.getAccountId(), userId);
            }
        }
    }

    private boolean isUserRoot(Set<AccountRights> rightsSet) {
        for(AccountRights rights : rightsSet) {
            if(rights.getRoles().contains(Role.ROLE_ROOT)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void forgotPassword(String username,
                               String securityQuestion,
                               String securityAnswer)
        throws DBNotFoundException, InvalidPasswordException,
               DBConcurrentUpdateException, UnsentEmailException {
        log.info("Resolving forgotten password for user {}", username);

        DuracloudUser user = loadDuracloudUserByUsernameInternal(username);

        if(!user.getSecurityQuestion().equalsIgnoreCase(securityQuestion) ||
           !user.getSecurityAnswer().equalsIgnoreCase(securityAnswer)) {
            throw new InvalidPasswordException(user.getId());
        }

        Random r = new Random();
        String generatedPassword = Long.toString(Math.abs(r.nextLong()), 36);

        changePassword(user.getId(),
                       user.getPassword(),
                       true,
                       generatedPassword);

        getNotifier().sendNotificationPasswordReset(user, generatedPassword);
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
        loadRights(user);
        return user;
    }

    private void loadRights(DuracloudUser user) {
        user.setAccountRights(getRightsRepo().findByUserId(user.getId()));
    }
    

    @Override
    public DuracloudUser loadDuracloudUserByIdInternal(Integer userId) throws DBNotFoundException {
        DuracloudUser user = getUserRepo().findById(userId);
        loadRights(user);
        return user;
    }    

    @Override
    public int redeemAccountInvitation(int userId, String redemptionCode)
        throws InvalidRedemptionCodeException {
        log.info("Redeeming account invitation for user with ID {}", userId);

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

        try {
            DuracloudUser user = getUserRepo().findById(userId);
            DuracloudUser adminUser =
                getUserRepo().findByUsername(invitation.getAdminUsername());
            getNotifier().
                sendNotificationRedeemedInvitation(user, adminUser.getEmail());
        } catch(DBNotFoundException e) {
            String msg = "Exception encountered attempting to send admin user " +
                invitation.getAdminUsername() + " notice that user with id " +
                userId + " accepted their account invitation";
            throw new UnsentEmailException(msg, e);
        }

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

    private DuracloudGroupRepo getGroupRepo() {
        return repoMgr.getGroupRepo();
    }

    private DuracloudRightsRepo getRightsRepo() {
        return repoMgr.getRightsRepo();
    }

    private IdUtil getIdUtil() {
        return repoMgr.getIdUtil();
    }

    @Override
    public void storeUserDetails(
        int userId, String firstName, String lastName, String email,
        String securityQuestion, String securityAnswer)
        throws DBNotFoundException, DBConcurrentUpdateException {
        log.info("Updating user details for user with ID {}", userId);

        DuracloudUser user = getUserRepo().findById(userId);
        boolean emailUpdate = !user.getEmail().equals(email);

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setSecurityQuestion(securityQuestion);
        user.setSecurityAnswer(securityAnswer);
        getUserRepo().save(user);

        if(emailUpdate) {
            propagateUserUpdate(userId, user.getUsername());
        }
    }

    private Notifier getNotifier() {
        if(null == notifier) {
            notifier = new Notifier(notificationMgr.getEmailer());
        }
        return notifier;
    }

    @Override
    public boolean addUserToAccount(int acctId, int userId) throws DBNotFoundException {
        boolean added = setUserRightsInternal(acctId, userId, Role.ROLE_USER);
        if(added){
            DuracloudUser user;
            user = loadDuracloudUserByIdInternal(userId);
            AccountInfo accountInfo =
                this.repoMgr.getAccountRepo().findById(acctId);
            getNotifier().sendNotificationUserAddedToAccount(user, accountInfo);
        }
        
        return added;
    }

}
