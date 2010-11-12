/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.db.error.UserAlreadyExistsException;
import org.duracloud.account.util.DuracloudUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author Andrew Woods
 *         Date: Oct 9, 2010
 */
public class DuracloudUserServiceImpl implements DuracloudUserService, UserDetailsService {
	private Logger log = LoggerFactory.getLogger(getClass());
    private DuracloudUserRepo userRepo;
    private DuracloudAccountRepo accountRepo;
    public DuracloudUserServiceImpl(DuracloudUserRepo userRepo,
                                    DuracloudAccountRepo accountRepo) {
        this.userRepo = userRepo;
        this.accountRepo = accountRepo;
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        try {
            userRepo.findById(username);
            return false;

        } catch (DBNotFoundException e) {
            return true;
        }
    }

    @Override
    public String createNewUser(String username,
                                String password,
                                String firstName,
                                String lastName,
                                String email)
        throws DBConcurrentUpdateException, UserAlreadyExistsException {
        DuracloudUser user = new DuracloudUser(username,
                                               password,
                                               firstName,
                                               lastName,
                                               email);
        throwIfUserExists(user);
        userRepo.save(user);
        log.info("created new user [{}]", username);
        return user.getId();
    }

    private void throwIfUserExists(DuracloudUser user)
        throws UserAlreadyExistsException {

        if (!isUsernameAvailable(user.getId())) {
            throw new UserAlreadyExistsException(user.getId());
        }
    }

    @Override
    public void addUserToAccount(String acctId, String username)
        throws DBNotFoundException {
        DuracloudUser user = userRepo.findById(username);
        user.addAccount(acctId);
    }

    @Override
    public void removeUserFromAccount(String acctId, String username) {
    	throw new NotImplementedException();
    }

    @Override
    public void grantAdminRights(String acctId, String username) {
		try {
			DuracloudUser user = this.userRepo.findById(username);
			Map<String,List<String>> acctToRoles = user.getAcctToRoles();
			List<String> roles = acctToRoles.get(acctId);
			if(roles == null){
				roles = new LinkedList<String>();
				acctToRoles.put(acctId, roles);
			}
			
			if(!roles.contains(Role.ROLE_ADMIN.name())){
				roles.add(Role.ROLE_ADMIN.name());
			}
			user.setAcctToRoles(acctToRoles);
			log.info("granted admin rights to {} on {}", username, acctId);

		} catch (DBNotFoundException e) {
			log.error("user {} not found", username, e);
		}
	}

	@Override
	public void grantOwnerRights(String acctId, String username) {
		try {
			DuracloudUser user = this.userRepo.findById(username);
			Map<String,List<String>> acctToRoles = user.getAcctToRoles();
			List<String> roles = acctToRoles.get(acctId);
			if(roles == null){
				roles = new LinkedList<String>();
				acctToRoles.put(acctId, roles);
			}
			
			if(!roles.contains(Role.ROLE_OWNER.name())){
				roles.add(Role.ROLE_OWNER.name());
			}
			user.setAcctToRoles(acctToRoles);
			log.info("granted owner rights to {} on {}", username, acctId);

		} catch (DBNotFoundException e) {
			log.error("user {} not found", username, e);
		}		
	}




    @Override
    public void revokeAdminRights(String acctId, String username) {
		try {
			DuracloudUser user = this.userRepo.findById(username);
			Map<String,List<String>> acctToRoles = user.getAcctToRoles();
			List<String> roles = acctToRoles.get(acctId);
			if(roles != null){
				roles.remove(Role.ROLE_ADMIN.name());
			}
			user.setAcctToRoles(acctToRoles);
			log.info("revoked admin rights from {} on {}", username, acctId);
		} catch (DBNotFoundException e) {
			log.error("user {} not found", username, e);
		}
	}

    @Override
    public void sendPasswordReminder(String username) {
        // Default method body

    }

    @Override
    public void changePassword(String username,
                               String oldPassword,
                               String newPassword) {
        // Default method body

    }

	@Override
	public DuracloudUser loadDuracloudUserByUsername(String username)
			throws DBNotFoundException {
		return  this.userRepo.findById(username);
	}

    @Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		try {
			return this.userRepo.findById(username);

		} catch (DBNotFoundException e) {
			throw new UsernameNotFoundException(e.getMessage(), e);
		}
	}

	@Override
	public void revokeOwnerRights(String id, String username) {
		// TODO Auto-generated method stub
		
	}

}
