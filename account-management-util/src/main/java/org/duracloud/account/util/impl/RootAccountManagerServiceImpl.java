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
import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudStorageProviderAccountRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.DuracloudInstanceManagerService;
import org.duracloud.account.util.DuracloudInstanceService;
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
    private DuracloudInstanceManagerService instanceManagerService;

    public RootAccountManagerServiceImpl(DuracloudRepoMgr duracloudRepoMgr,
                                    NotificationMgr notificationMgr,
                                    UserDetailsPropagator propagator,
                                    DuracloudInstanceManagerService instanceManagerService) {
        this.repoMgr = duracloudRepoMgr;
        this.notificationMgr = notificationMgr;
        this.propagator = propagator;
        this.instanceManagerService = instanceManagerService;
    }

	@Override
	public void addDuracloudImage(String imageId,
                                  String version,
			                      String description) {
		throw new NotImplementedException("addDuracloudImage not implemented");
	}

	@Override
	public void resetUsersPassword(int userId)
        throws DBNotFoundException, DBConcurrentUpdateException,
               UnsentEmailException {
        log.info("Resetting password for user with ID {}", userId);

        DuracloudUser user = getUserRepo().findById(userId);

        ChecksumUtil util = new ChecksumUtil(ChecksumUtil.Algorithm.SHA_256);
        String generatedPassword =
            Long.toString(Math.abs(new Random().nextLong()), 36);
        user.setPassword(util.generateChecksum(generatedPassword));

        getUserRepo().save(user);

        try {
            Set<AccountRights> rightsSet =
                repoMgr.getRightsRepo().findByUserId(userId);
            // Propagate changes for each of the user's accounts
            if(!isUserRoot(rightsSet)) { // Do no propagate if user is root
                for(AccountRights rights : rightsSet) {
                    log.debug("Propagating password update to account {}",
                              rights.getAccountId());
                    propagator.propagateUserUpdate(rights.getAccountId(),
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
	public void deleteUser(int userId) {
        log.info("Deleting user with ID {}", userId);

        try{
            Set<AccountRights> accountRights =
                getRightsRepo().findByUserId(userId);
            for(AccountRights accountRight : accountRights) {
                getRightsRepo().delete(accountRight.getId());

                log.debug("Propagating rights revocation for user {} " +
                          "to account {}", userId, accountRight.getAccountId());

                propagator.propagateRevocation(accountRight.getAccountId(),
                                               userId);
            }
        }catch(DBNotFoundException ex){
            log.error("Unable to find account rights for user " +
                      "with ID {} due to {}", userId, ex.getMessage());
        }
        getUserRepo().delete(userId);
	}

	@Override
	public void deleteAccount(int accountId) {
        log.info("Deleting account with ID {}", accountId);

        Set<DuracloudInstanceService> instanceServices =
            instanceManagerService.getInstanceServices(accountId);
        if (instanceServices.size() > 0) {
            log.error("Unable to delete account {} found an instance",
                      accountId);
            return;
        }

        AccountInfo account = getAccount(accountId);

        // Delete the primary storage provider
        getStorageRepo().delete(account.getPrimaryStorageProviderAccountId());

        // Delete any secondary storage providers
        for(int secId : account.getSecondaryStorageProviderAccountIds()) {
            getStorageRepo().delete(secId);
        }

        // Delete the compute provider
        repoMgr.getComputeProviderAccountRepo()
            .delete(account.getComputeProviderAccountId());

        // Delete the account rights
        try {
            Set<AccountRights> rights =
                    getRightsRepo().findByAccountIdSkipRoot(accountId);
            for(AccountRights right : rights) {
                getRightsRepo().delete(right.getId());
            }
        } catch (DBNotFoundException ex) {
            log.warn("No rights found for account {}, " +
                     "which is being deleted", accountId);
        }

        // Delete any user invitations
        for(UserInvitation invitation :
            repoMgr.getUserInvitationRepo().findByAccountId(accountId)) {
            repoMgr.getUserInvitationRepo().delete(invitation.getId());
        }

        // Delete account
        getAccountRepo().delete(accountId);
	}

    @Override
    public List<StorageProviderAccount> getSecondaryStorageProviders(int accountId) {
        AccountInfo account = getAccount(accountId);

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
	public void setupStorageProvider(int providerId,
                                     String username,
                                     String password)
        throws DBConcurrentUpdateException {
        log.info("Setting up storage provider with ID {}", providerId);
        try {
            StorageProviderAccount storageProviderAccount =
                getStorageRepo().findById(providerId);
            storageProviderAccount.setUsername(username);
            storageProviderAccount.setPassword(password);
            getStorageRepo().save(storageProviderAccount);
        } catch (DBNotFoundException ex) {
            log.warn("No StorageProviderAccount found with ID {}, " +
                     "could not set up storage provider", providerId);
        }
	}

	@Override
	public void setupComputeProvider(int providerId,
                                     String username,
                                     String password,
                                     String elasticIp,
                                     String keypair,
                                     String securityGroup)
        throws DBConcurrentUpdateException {
        log.info("Setting up compute provider with ID {}", providerId);
        try {
            ComputeProviderAccount computeStorageAcct =
                repoMgr.getComputeProviderAccountRepo().findById(providerId);
            computeStorageAcct.setUsername(username);
            computeStorageAcct.setPassword(password);
            computeStorageAcct.setElasticIp(elasticIp);
            computeStorageAcct.setKeypair(keypair);
            computeStorageAcct.setSecurityGroup(securityGroup);
            repoMgr.getComputeProviderAccountRepo().save(computeStorageAcct);
        } catch (DBNotFoundException ex) {
            log.warn("No ComputeProviderAccount found with ID {}, " +
                     "could not set up compute provider", providerId);
        }
	}

	@Override
	public AccountInfo getAccount(int id) {
        try{
            return getAccountRepo().findById(id);
        }catch(DBNotFoundException ex){
            log.error("No Account found with ID {}", id);
        }
        return null;
	}

	@Override
	public void activateAccount(int accountId)
        throws DBConcurrentUpdateException {
        log.info("Activating account with ID {}", accountId);
        try{
            AccountInfo accountInfo = getAccountRepo().findById(accountId);
            accountInfo.setStatus(AccountInfo.AccountStatus.ACTIVE);
            getAccountRepo().save(accountInfo);
        }catch(DBNotFoundException ex){
            log.error("No Account found with ID {}, could not activate account",
                      accountId);
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
				log.error("No Account found with ID {}, skipping", acctId);
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
				log.error("No User found with ID {}", id);
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
                    log.info("No AccountRights found for user with ID {}", id);
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
