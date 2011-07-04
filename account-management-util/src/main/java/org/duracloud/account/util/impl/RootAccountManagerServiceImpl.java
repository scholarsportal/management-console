/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.apache.commons.lang.NotImplementedException;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.RootAccountManagerService;
import org.duracloud.account.util.error.UnsentEmailException;
import org.duracloud.account.util.notification.NotificationMgr;
import org.duracloud.account.util.usermgmt.UserDetailsPropagator;
import org.duracloud.common.util.ChecksumUtil;
import org.duracloud.notification.Emailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: Oct 9, 2010
 */
public class RootAccountManagerServiceImpl implements RootAccountManagerService {

	private Logger log = LoggerFactory.getLogger(RootAccountManagerServiceImpl.class);

    private DuracloudRepoMgr repoMgr;
    private UserDetailsPropagator propagator;
    private NotificationMgr notificationMgr;
    
    public RootAccountManagerServiceImpl(DuracloudRepoMgr duracloudRepoMgr,
                                    NotificationMgr notificationMgr,
                                    UserDetailsPropagator propagator) {
        this.repoMgr = duracloudRepoMgr;
        this.notificationMgr = notificationMgr;
        this.propagator = propagator;
    }

	@Override
	public void addDuracloudImage(String imageId, String version,
			String description) {
		throw new NotImplementedException("addDuracloudImage not implemented");
	}

	@Override
	public void resetUsersPassword(int userId)
        throws DBNotFoundException, DBConcurrentUpdateException,
               UnsentEmailException {
        DuracloudUser user = getUserRepo().findById(userId);

        ChecksumUtil util = new ChecksumUtil(ChecksumUtil.Algorithm.SHA_256);
        String generatedPassword = Long.toString(Math.abs(new Random().nextLong()), 36);
        user.setPassword(util.generateChecksum(generatedPassword));

        getUserRepo().save(user);

        log.debug("Propagating update for user: " + userId);
        try {
            Set<AccountRights> rightsSet =
                repoMgr.getRightsRepo().findByUserId(userId);
            // Propagate changes for each of the user's accounts
            if(!isUserRoot(rightsSet)) { // Do no propagate if user is root
                for(AccountRights rights : rightsSet) {
                    propagator.propagatePasswordUpdate(rights.getAccountId(),
                                                       userId);
                }
            }
        } catch (DBNotFoundException e) {
            // Not all users are associated with an account.
            log.debug("No account rights found for {}", user.getUsername());
        }

        try {
            Emailer emailer = notificationMgr.getEmailer();

            emailer.send("Duracloud Account Management - Reset Password",
                         "Please use the following password to login: "
                         + generatedPassword, user.getEmail());
        } catch (Exception e) {
            String msg =
                "Error: Unable to send email to user: " + user.getUsername();
            log.error(msg);
            throw new UnsentEmailException(msg, e);
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
	public void deleteUser(int id) {
        try{
            Set<AccountRights> accountRights = getRightsRepo().findByUserId(id);
            for(AccountRights accountRight : accountRights) {
                getRightsRepo().delete(accountRight.getId());

                log.debug("Propagating revocation for: " +
                          accountRight.getAccountId() + ", " + id);
                propagator.propagateRevocation(accountRight.getAccountId(), id);
            }
        }catch(DBNotFoundException ex){
            log.error("account[{}] not found; skipping.", id, ex);
        }
        getUserRepo().delete(id);
	}

	@Override
	public Set<AccountInfo> listAllAccounts(String filter) {
		Set<Integer> accountIds = getAccountRepo().getIds();
		Set<AccountInfo> accountInfos = new HashSet<AccountInfo>();
		for(int acctId : accountIds){
			try{
				AccountInfo accountInfo = getAccountRepo().findById(acctId);
				if(filter == null || accountInfo.getOrgName().startsWith(filter)){
					accountInfos.add(accountInfo);
				}
			}catch(DBNotFoundException ex){
				log.error("account[{}] not found; skipping.", acctId, ex);
			}
		}
		return accountInfos;
	}

	@Override
	public Set<DuracloudUser> listAllUsers(String filter) {
		Set<Integer> userIds = getUserRepo().getIds();
		Set<DuracloudUser> users = new HashSet<DuracloudUser>();
        DuracloudUser user = null;
		for(int id : userIds){
            try{
				user = getUserRepo().findById(id);
			}catch(DBNotFoundException ex){
				log.error("user[{}] not found; skipping.", id, ex);
                continue;
			}
				
            if(filter == null || (user.getUsername().startsWith(filter)
                    || user.getFirstName().startsWith(filter)
                    || user.getLastName().startsWith(filter)
                    || user.getEmail().startsWith(filter))
            ){
                try{
                    user.setAccountRights(
                        getRightsRepo().findByUserId(user.getId()));
                }catch(DBNotFoundException ex){
                    log.error("account[{}] not found; skipping.", id, ex);
                }
                
                users.add(user);
            }
		}
		return users;

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
  
}
