/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.impl;

import org.apache.commons.lang.NotImplementedException;
import org.duracloud.account.db.model.*;
import org.duracloud.account.db.repo.*;
import org.duracloud.account.db.util.DuracloudInstanceManagerService;
import org.duracloud.account.db.util.DuracloudInstanceService;
import org.duracloud.account.db.util.DuracloudUserService;
import org.duracloud.account.db.util.RootAccountManagerService;
import org.duracloud.account.db.util.error.DBNotFoundException;
import org.duracloud.account.db.util.error.InvalidPasswordException;
import org.duracloud.account.db.util.error.UnsentEmailException;
import org.duracloud.account.db.util.notification.NotificationMgr;
import org.duracloud.account.db.util.notification.Notifier;
import org.duracloud.account.db.util.usermgmt.UserDetailsPropagator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private DuracloudUserService userService;

    public RootAccountManagerServiceImpl(DuracloudRepoMgr duracloudRepoMgr,
                                         NotificationMgr notificationMgr,
                                         UserDetailsPropagator propagator,
                                         DuracloudInstanceManagerService instanceManagerService,
                                         DuracloudUserService userService) {
        this.repoMgr = duracloudRepoMgr;
        this.notificationMgr = notificationMgr;
        this.propagator = propagator;
        this.instanceManagerService = instanceManagerService;
        this.userService = userService;
    }

	@Override
	public void addDuracloudImage(String imageId,
                                  String version,
			                      String description) {
		throw new NotImplementedException("addDuracloudImage not implemented");
	}

	@Override
	public void resetUsersPassword(Long userId)
        throws DBNotFoundException, UnsentEmailException {
        log.info("Resetting password for user with ID {}", userId);

        DuracloudUser user = getUserRepo().findOne(userId);
        if(user == null) {
            throw new DBNotFoundException("User with ID: "+userId+" does not exist");
        }

        try {
            userService.forgotPassword(user.getUsername(), user.getSecurityQuestion(), user.getSecurityAnswer());
        } catch (InvalidPasswordException e) {
            log.error("This should never happen!",e);
        }
	}


	@Override
	public void deleteUser(Long userId) {
        log.info("Deleting user with ID {}", userId);

        // Remove all user rights
        List<AccountRights> accountRights =
            getRightsRepo().findByUserId(userId);
        getRightsRepo().deleteInBatch(accountRights);
        for(AccountRights accountRight : accountRights) {
            log.debug("Propagating rights revocation for user {} " +
                      "to account {}", userId, accountRight.getAccount().getId());
            propagator.propagateRevocation(accountRight.getAccount().getId(), userId);
        }

        // Remove user from all groups
        DuracloudUser user = repoMgr.getUserRepo().findOne(userId);
        DuracloudGroupRepo groupRepo = getGroupRepo();
        List<DuracloudGroup> allGroups = groupRepo.findAll();
        for(DuracloudGroup group : allGroups) {
            Set<DuracloudUser> groupUsers = group.getUsers();
            if(groupUsers.contains(user)) {
                groupUsers.remove(user);
                groupRepo.save(group);
            }
        }

        // Remove the user
        getUserRepo().delete(userId);
	}

    @Override
    public void deleteAccountCluster(Long clusterId) {
        log.info("Deleting account cluster with ID {}", clusterId);

        // Find the cluster
        DuracloudAccountClusterRepo clusterRepo = getClusterRepo();
        AccountCluster cluster = clusterRepo.findOne(clusterId);

        // Reset the cluster ID of all accounts in this cluster
        Set<AccountInfo> clusterAccounts = cluster.getClusterAccounts();
        DuracloudAccountRepo accountRepo = getAccountRepo();
        for(AccountInfo account : clusterAccounts) {
            account.setAccountCluster(null);
            accountRepo.save(account);
        }

        // Propagate user/group changes for all accounts in this cluster
        for(AccountInfo account : clusterAccounts) {
            propagator.propagateClusterUpdate(account.getId(), clusterId);
        }

        // Remove the cluster
        clusterRepo.delete(clusterId);
    }

//    private AccountCluster getCluster(Long clusterId,
//                                      DuracloudAccountClusterRepo clusterRepo) {
//        try {
//            return clusterRepo.findById(clusterId);
//        } catch(DBNotFoundException e) {
//            String error = "Cluster with ID " + clusterId + " was not found " +
//                           "in the database, so could not be deleted.";
//            throw new DuraCloudRuntimeException(error);
//        }
//    }

    @Override
	public void deleteAccount(Long accountId) {
        log.info("Deleting account with ID {}", accountId);
        AccountInfo account = getAccount(accountId);

        if(account.getType().equals(AccountType.FULL)) {
            Set<DuracloudInstanceService> instanceServices =
                instanceManagerService.getInstanceServices(accountId);
            if (instanceServices.size() > 0) {
                log.error("Unable to delete account {} found an instance",
                          accountId);
                return;
            }

            ServerDetails serverDetails = account.getServerDetails();

            // Delete the primary storage provider
            getStorageRepo()
                .delete(serverDetails.getPrimaryStorageProviderAccount());

            // Delete any secondary storage providers
            getStorageRepo().deleteInBatch(serverDetails.getSecondaryStorageProviderAccounts());

            // Delete the compute provider
            repoMgr.getComputeProviderAccountRepo()
                .delete(serverDetails.getComputeProviderAccount());
            
            // Delete the server details
            repoMgr.getServerDetailsRepo().delete(serverDetails);
        }

        // Delete the account rights
        List<AccountRights > rightsList =
            getRightsRepo().findByAccountId(accountId);
        for(AccountRights rights : rightsList){
            DuracloudUser user = rights.getUser();
            user.getAccountRights().remove(rights);
            getRightsRepo().save(rights);
        }
        
        getRightsRepo().deleteInBatch(rightsList);

        // Delete the groups associated with the account
        DuracloudGroupRepo groupRepo = repoMgr.getGroupRepo();
        List<DuracloudGroup> groups = groupRepo.findByAccountId(accountId);
        groupRepo.deleteInBatch(groups);

        // Delete any user invitations
        DuracloudUserInvitationRepo invRepo = repoMgr.getUserInvitationRepo();
        invRepo.deleteInBatch(invRepo.findByAccountId(accountId));

        // Update cluster if necessary
        removeAccountFromCluster(account);

        // Delete account
        getAccountRepo().delete(accountId);
	}

    private void removeAccountFromCluster(AccountInfo account) {
        Long accountId = account.getId();
        AccountCluster cluster = account.getAccountCluster();
        if(cluster != null) { // Account is part of a cluster
            // Update the cluster to no longer include this account
            cluster.getClusterAccounts().remove(account);
            repoMgr.getAccountClusterRepo().save(cluster);

            // Propagate any changes to cluster users/groups
            propagator.propagateClusterUpdate(accountId, cluster.getId());
        }
    }

    @Override
    public List<StorageProviderAccount> getSecondaryStorageProviders(Long accountId) {
        AccountInfo account = repoMgr.getAccountRepo().findOne(accountId);
        ServerDetails serverDetails = account.getServerDetails();
        return new ArrayList(serverDetails.getSecondaryStorageProviderAccounts());
    }

	@Override
	public void setupStorageProvider(Long providerId,
                                     String username,
                                     String password) {
        log.info("Setting up storage provider with ID {}", providerId);
        StorageProviderAccount storageProviderAccount =
            getStorageRepo().findOne(providerId);
        storageProviderAccount.setUsername(username);
        storageProviderAccount.setPassword(password);
        getStorageRepo().save(storageProviderAccount);
    }

	@Override
	public void setupComputeProvider(Long providerId,
                                     String username,
                                     String password,
                                     String elasticIp,
                                     String keypair,
                                     String securityGroup) {
        log.info("Setting up compute provider with ID {}", providerId);
        ComputeProviderAccount computeProviderAcct =
            repoMgr.getComputeProviderAccountRepo().findOne(providerId);
        computeProviderAcct.setUsername(username);
        computeProviderAcct.setPassword(password);
        computeProviderAcct.setElasticIp(elasticIp);
        computeProviderAcct.setKeypair(keypair);
        computeProviderAcct.setSecurityGroup(securityGroup);
        repoMgr.getComputeProviderAccountRepo().save(computeProviderAcct);
    }

	@Override
	public AccountInfo getAccount(Long id) {
        return getAccountRepo().findOne(id);
    }

	@Override
	public void activateAccount(Long accountId) {
        log.info("Activating account with ID {}", accountId);
        AccountInfo accountInfo = getAccountRepo().findOne(accountId);
        accountInfo.setStatus(AccountInfo.AccountStatus.ACTIVE);
        getAccountRepo().save(accountInfo);
    }

	@Override
	public Set<AccountInfo> listAllAccounts(String filter) {
		List<AccountInfo> accounts = getAccountRepo().findAll();
		Set<AccountInfo> accountInfos = new HashSet<AccountInfo>();
		for(AccountInfo acct : accounts){
            if(filter == null || acct.getOrgName().startsWith(filter)){
                accountInfos.add(acct);
            }
		}
		return accountInfos;
	}

	@Override
	public Set<DuracloudUser> listAllUsers(String filter) {
		List<DuracloudUser> usersList = getUserRepo().findAll();
		Set<DuracloudUser> users = new HashSet<DuracloudUser>();
        for(DuracloudUser user: usersList){
            if(filter == null || (user.getUsername().startsWith(filter)
                    || user.getFirstName().startsWith(filter)
                    || user.getLastName().startsWith(filter)
                    || user.getEmail().startsWith(filter))
            ){
                users.add(user);
            }
		}
		return users;
    }

	@Override
	public Set<ServerImage> listAllServerImages(String filter) {
		List<ServerImage> imageList = getServerImageRepo().findAll();
		Set<ServerImage> images = new HashSet<ServerImage>();
        for(ServerImage image : imageList){
            if(filter == null ||
                    (image.getProviderImageId().startsWith(filter))){
                images.add(image);
            }
		}
		return images;
	}


	@Override
	public void createServerImage(Long providerAccountId,
                                  String providerImageId,
                                  String version,
                                  String description,
                                  String password,
                                  boolean latest) {
        DuracloudServerImageRepo imageRepo = getServerImageRepo();
        if(latest) {
            //Remove current latest
            ServerImage latestImage = imageRepo.findLatest();
            latestImage.setLatest(!latest);
            imageRepo.save(latestImage);
        }

        //make the new server image the 'latest' if it is
        //the first image.
        Long imageCount = imageRepo.count();
        if(imageCount == 0){
            latest = true;
        }

        ComputeProviderAccount providerAccount =
                repoMgr.getComputeProviderAccountRepo().findOne(providerAccountId);

        ServerImage serverImage = new ServerImage();
        serverImage.setProviderAccount(providerAccount);
        serverImage.setProviderImageId(providerImageId);
        serverImage.setVersion(version);
        serverImage.setDescription(description);
        serverImage.setDcRootPassword(password);
        serverImage.setLatest(latest);

        getServerImageRepo().save(serverImage);
    }

	@Override
	public void editServerImage(Long id,
                Long providerAccountId,
                                String providerImageId,
                                String version,
                                String description,
                                String password,
                                boolean latest) {
        if(latest) {
            //Remove current latest
            ServerImage latestImage = getServerImageRepo().findLatest();
            if(latestImage.getId() != id) {
                latestImage.setLatest(!latest);
                getServerImageRepo().save(latestImage);
            }
        }

        ComputeProviderAccount providerAccount =
                repoMgr.getComputeProviderAccountRepo().findOne(providerAccountId);

        ServerImage serverImage = getServerImageRepo().findOne(id);
        serverImage.setProviderAccount(providerAccount);
        serverImage.setProviderImageId(providerImageId);
        serverImage.setVersion(version);
        serverImage.setDescription(description);
        serverImage.setDcRootPassword(password);
        serverImage.setLatest(latest);
        getServerImageRepo().save(serverImage);
    }

	@Override
	public ServerImage getServerImage(Long id) {
        return getServerImageRepo().findOne(id);
    }

	@Override
	public void deleteServerImage(Long id) {
        log.info("Deleting server image with ID {}", id);

        getServerImageRepo().delete(id);
	}


    private DuracloudServerImageRepo getServerImageRepo() {
        return repoMgr.getServerImageRepo();
    }


    private DuracloudUserRepo getUserRepo() {
        return repoMgr.getUserRepo();
    }

    private DuracloudGroupRepo getGroupRepo() {
        return repoMgr.getGroupRepo();
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

    private DuracloudAccountClusterRepo getClusterRepo() {
        return repoMgr.getAccountClusterRepo();
    }

    private Notifier getNotifier() {
        if(null == notifier) {
            notifier = new Notifier(notificationMgr.getEmailer());
        }
        return notifier;
    }
  
}
