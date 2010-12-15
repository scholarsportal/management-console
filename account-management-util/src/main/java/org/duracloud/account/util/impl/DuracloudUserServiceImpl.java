/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.IdUtil;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.db.error.UserAlreadyExistsException;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.common.error.DuraCloudRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: Oct 9, 2010
 */
public class DuracloudUserServiceImpl implements DuracloudUserService, UserDetailsService {
    
	private Logger log = LoggerFactory.getLogger(DuracloudUserServiceImpl.class);

    private DuracloudRepoMgr repoMgr;
    
    public DuracloudUserServiceImpl(DuracloudRepoMgr duracloudRepoMgr) {
        this.repoMgr = duracloudRepoMgr;
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        try {
            getUserRepo().findByUsername(username);
            return false;
        } catch (DBNotFoundException e) {
            return true;
        }
    }

    @Override
    public DuracloudUser createNewUser(String username,
                                       String password,
                                       String firstName,
                                       String lastName,
                                       String email)
        throws DBConcurrentUpdateException, UserAlreadyExistsException {
        int newUserId = getIdUtil().newUserId();
        DuracloudUser user = new DuracloudUser(newUserId,
                                               username,
                                               password,
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
    public void grantUserRights(int acctId, int userId) {
        retryUpdateRights(acctId, userId, Role.ROLE_USER, true);
    }

    @Override
    public void grantAdminRights(int acctId, int userId) {
        retryUpdateRights(acctId, userId, Role.ROLE_ADMIN, true);
	}

	@Override
	public void grantOwnerRights(int acctId, int userId) {
        retryUpdateRights(acctId, userId, Role.ROLE_OWNER, true);
	}

    @Override
    public void revokeUserRights(int acctId, int userId) {
    	retryUpdateRights(acctId, userId, Role.ROLE_USER, false);
    }    

    @Override
    public void revokeAdminRights(int acctId, int userId) {
        retryUpdateRights(acctId, userId, Role.ROLE_ADMIN, false);
	}

	@Override
	public void revokeOwnerRights(int acctId, int userId) {
		retryUpdateRights(acctId, userId, Role.ROLE_OWNER, false);
	}

    private void retryUpdateRights(int acctId,
                                   int userId,
                                   Role role,
                                   boolean grant) {
        int maxAttempts = 5;
        int attempts = 0;
        boolean success = false;
        while(!success && attempts < maxAttempts) {
            try {
                if(grant) {
                    grantRights(acctId, userId, role);
                } else {
                    revokeRights(acctId, userId, role);
                }
                success = true;
            } catch(DBConcurrentUpdateException e) {
            }
            attempts++;
        }

        if(!success && attempts >= maxAttempts) {
            String type = grant ? "grant" : "remoke";
            String error = "Failure attempting to " + type + " role " +
                           role.name() + " to User ID " + userId +
                           " for Account ID" + acctId;
            throw new DuraCloudRuntimeException(error);
        }
    }

    private void grantRights(int acctId, int userId, Role role)
        throws DBConcurrentUpdateException {
        AccountRights rights = null;
        int rightsId = -1;
        Set<Role> roles = null;
        try {
            rights = getRightsRepo().findByAccountIdAndUserId(acctId, userId);
            rightsId = rights.getId();
            roles = rights.getRoles();
            if(roles != null && roles.contains(role)) {
                return; // User already has access to expected role
            }
        } catch (DBNotFoundException e) {
        }

        if(roles == null) {
            roles = new HashSet<Role>();
        }
        roles.add(Role.ROLE_USER);
        if(role.equals(Role.ROLE_ADMIN)) {
            roles.add(Role.ROLE_ADMIN);
        } else if(role.equals(Role.ROLE_OWNER)) {
            roles.add(Role.ROLE_ADMIN);
            roles.add(Role.ROLE_OWNER);
        }

        if(rightsId < 0) {
            rightsId = getIdUtil().newRightsId();
        }

        rights = new AccountRights(rightsId, acctId, userId, roles);
        getRightsRepo().save(rights);
    }

    private void revokeRights(int acctId, int userId, Role role)
        throws DBConcurrentUpdateException {
        try {
            AccountRights rights =
                getRightsRepo().findByAccountIdAndUserId(acctId, userId);

            // Removing user rights is equivalent to removing all rights
            if(Role.ROLE_USER.equals(role)) {
                getRightsRepo().delete(rights.getId());
                return;
            }

            Set<Role> roles = rights.getRoles();
            if(roles != null && roles.contains(role)) {
                roles.remove(Role.ROLE_OWNER);
                if(Role.ROLE_ADMIN.equals(role)) {
                    roles.remove(Role.ROLE_ADMIN);
                }
                getRightsRepo().save(rights);
            } else {
                return; // Role does not exist for user on account
            }
        } catch (DBNotFoundException e) {
            return; // No rights exist for the given user on the given account
        }
    }

    @Override
    public void sendPasswordReminder(int userId) {
        // Default method body
    }

    @Override
    public void changePassword(int userId,
                               String oldPassword,
                               String newPassword) {
        // Default method body
    }

	@Override
	public DuracloudUser loadDuracloudUserByUsername(String username)
			throws DBNotFoundException {
		return  this.getUserRepo().findByUsername(username);
	}

    @Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
    	DuracloudUser user;
		try {
            user = getUserRepo().findByUsername(username);
		} catch (DBNotFoundException e) {
			throw new UsernameNotFoundException(e.getMessage(), e);
		}

		//not all users are associated with an account.
		try {
			user.setAccountRights(getRightsRepo().findByUserId(user.getId()));
		} catch (DBNotFoundException e) {
			log.debug("no account rights for {}", user.getUsername());
		}
		
		return user;
    }

    private DuracloudUserRepo getUserRepo() {
        return repoMgr.getUserRepo();
    }

    private DuracloudAccountRepo getAccountRepo() {
        return repoMgr.getAccountRepo();
    }

    private DuracloudRightsRepo getRightsRepo() {
        return repoMgr.getRightsRepo();
    }

    private IdUtil getIdUtil() {
        return repoMgr.getIdUtil();
    }
}
