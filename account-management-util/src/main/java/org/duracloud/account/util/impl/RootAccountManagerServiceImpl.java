/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.apache.commons.lang.NotImplementedException;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.ComputeProviderAccount;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudStorageProviderAccountRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.RootAccountManagerService;
import org.duracloud.account.util.error.UnsentEmailException;
import org.duracloud.account.util.notification.NotificationMgr;
import org.duracloud.account.util.notification.Notifier;
import org.duracloud.account.util.usermgmt.UserDetailsPropagator;
import org.duracloud.common.util.ChecksumUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private Notifier notifier;
    
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

        getNotifier().sendNotificationPasswordReset(user, generatedPassword);
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
	public void deleteAccount(int id) {
        try {
            Set<AccountRights> rights =
                    getRightsRepo().findByAccountId(id);
            for(AccountRights right : rights) {
                getRightsRepo().delete(right.getId());
                propagator.propagateRevocation(id, right.getUserId());
            }
        } catch (DBNotFoundException ex) {
            log.warn("No AccountRights found for account[{}]: error message: {}",
                     id,
                     ex.getMessage());
        }

        getAccountRepo().delete(id);
	}

    @Override
    public List<StorageProviderAccount> getSecondaryStorageProviders(int id) {
        AccountInfo account = getAccount(id);

        List<StorageProviderAccount> accounts =
            new ArrayList<StorageProviderAccount>();

        try {
            for(int secId : account.getSecondaryStorageProviderAccountIds()) {
                accounts.add(getStorageRepo().findById(secId));
            }
        } catch(DBNotFoundException e) {

        }

        return accounts;
    }

	@Override
	public void setupStorageProvider(int id, String username, String password)
        throws DBConcurrentUpdateException {
        try {
            StorageProviderAccount primaryStorageAcct =
                getStorageRepo().findById(id);
            primaryStorageAcct.setUsername(username);
            primaryStorageAcct.setPassword(password);
            getStorageRepo().save(primaryStorageAcct);
        } catch (DBNotFoundException ex) {
            log.warn(
                "No StorageProviderAccount found for id[{}]: error message: {}",
                id,
                ex.getMessage());
        }
	}

	@Override
	public void setupComputeProvider(int id, String username, String password,
                                     String elasticIp, String keypair, String securityGroup)
        throws DBConcurrentUpdateException {
        try {
            ComputeProviderAccount computeStorageAcct =
                repoMgr.getComputeProviderAccountRepo().findById(id);
            computeStorageAcct.setUsername(username);
            computeStorageAcct.setPassword(password);
            computeStorageAcct.setElasticIp(elasticIp);
            computeStorageAcct.setKeypair(keypair);
            computeStorageAcct.setSecurityGroup(securityGroup);
            repoMgr.getComputeProviderAccountRepo().save(computeStorageAcct);
        } catch (DBNotFoundException ex) {
            log.warn(
                "No ComputeProviderAccount found for id[{}]: error message: {}",
                id, ex.getMessage());
        }
	}

	@Override
	public AccountInfo getAccount(int id) {
        try{
            return getAccountRepo().findById(id);
        }catch(DBNotFoundException ex){
            log.error("account[{}] not found; skipping.", id, ex);
        }
        return null;
	}

	@Override
	public void activateAccount(int id)
        throws DBConcurrentUpdateException {
        try{
            AccountInfo accountInfo = getAccountRepo().findById(id);
            accountInfo.setStatus(AccountInfo.AccountStatus.ACTIVE);
            getAccountRepo().save(accountInfo);
        }catch(DBNotFoundException ex){
            log.error("account[{}] not found; skipping.", id, ex);
        }
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

    private DuracloudStorageProviderAccountRepo getStorageRepo() {
        return repoMgr.getStorageProviderAccountRepo();
    }

    private Notifier getNotifier() {
        if(null == notifier) {
            notifier = new Notifier(notificationMgr.getEmailer());
        }
        return notifier;
    }
  
}
