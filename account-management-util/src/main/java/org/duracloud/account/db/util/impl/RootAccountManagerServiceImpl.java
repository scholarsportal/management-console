/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;
import org.duracloud.account.db.model.AccountCluster;
import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.AccountRights;
import org.duracloud.account.db.model.AccountType;
import org.duracloud.account.config.AmaEndpoint;
import org.duracloud.account.db.model.ComputeProviderAccount;
import org.duracloud.account.db.model.DuracloudGroup;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.model.ServerDetails;
import org.duracloud.account.db.model.ServerImage;
import org.duracloud.account.db.model.StorageProviderAccount;
import org.duracloud.account.db.repo.DuracloudAccountClusterRepo;
import org.duracloud.account.db.repo.DuracloudAccountRepo;
import org.duracloud.account.db.repo.DuracloudGroupRepo;
import org.duracloud.account.db.repo.DuracloudRepoMgr;
import org.duracloud.account.db.repo.DuracloudRightsRepo;
import org.duracloud.account.db.repo.DuracloudServerImageRepo;
import org.duracloud.account.db.repo.DuracloudStorageProviderAccountRepo;
import org.duracloud.account.db.repo.DuracloudUserInvitationRepo;
import org.duracloud.account.db.repo.DuracloudUserRepo;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

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
    private AmaEndpoint amaEndpoint;
    public RootAccountManagerServiceImpl(DuracloudRepoMgr duracloudRepoMgr,
                                         NotificationMgr notificationMgr,
                                         UserDetailsPropagator propagator,
                                         DuracloudInstanceManagerService instanceManagerService,
                                         DuracloudUserService userService,
                                         AmaEndpoint amaEndpoint) {
        this.repoMgr = duracloudRepoMgr;
        this.notificationMgr = notificationMgr;
        this.propagator = propagator;
        this.instanceManagerService = instanceManagerService;
        this.userService = userService;
        this.amaEndpoint = amaEndpoint;
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
        for(AccountRights right : accountRights){
            this.userService.revokeUserRights(right.getAccount().getId(), userId);
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

        }

        // Delete the account rights
        List<AccountRights > rightsList =
            getRightsRepo().findByAccountId(accountId);
        for(AccountRights rights : rightsList){
            DuracloudUser user = rights.getUser();
            user.getAccountRights().remove(rights);
            rights.getRoles().clear();
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
                                     String securityGroup,
                                     String auditQueue) {
        log.info("Setting up compute provider with ID {}", providerId);
        ComputeProviderAccount computeProviderAcct =
            repoMgr.getComputeProviderAccountRepo().findOne(providerId);
        computeProviderAcct.setUsername(username);
        computeProviderAcct.setPassword(password);
        computeProviderAcct.setElasticIp(elasticIp);
        computeProviderAcct.setKeypair(keypair);
        computeProviderAcct.setSecurityGroup(securityGroup);
        computeProviderAcct.setAuditQueue(auditQueue);
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
		List<AccountInfo> accounts = getAccountRepo().findAll(new Sort("acctName"));
		Set<AccountInfo> accountInfos = new LinkedHashSet<AccountInfo>();
		for(AccountInfo acct : accounts){
            if(filter == null || acct.getOrgName().startsWith(filter)){
                accountInfos.add(acct);
            }
		}
		return accountInfos;
	}

	@Override
	public Set<DuracloudUser> listAllUsers(String filter) {
		List<DuracloudUser> usersList = getUserRepo().findAll(new Sort("username"));
		Set<DuracloudUser> users = new LinkedHashSet<DuracloudUser>();
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
		List<ServerImage> imageList = getServerImageRepo().findAll(new Sort(Direction.DESC, "version"));
		Set<ServerImage> images = new LinkedHashSet<ServerImage>();
        for(ServerImage image : imageList){
            if(filter == null ||
                    (image.getProviderImageId().startsWith(filter))){
                images.add(image);
            }
		}
		return images;
	}


	@Override
	public void createServerImage(String providerImageId,
                                  String version,
                                  String description,
                                  String password,
                                  boolean latest,
                                  String iamRole) {
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


        ServerImage serverImage = new ServerImage();
        serverImage.setProviderImageId(providerImageId);
        serverImage.setVersion(version);
        serverImage.setDescription(description);
        serverImage.setDcRootPassword(password);
        serverImage.setLatest(latest);
        serverImage.setIamRole(iamRole);

        getServerImageRepo().save(serverImage);
    }

	@Override
	public void editServerImage(Long id,
                                String providerImageId,
                                String version,
                                String description,
                                String password,
                                boolean latest,
                                String iamRole) {

        ServerImage serverImage = getServerImageRepo().findOne(id);

	    //Remove current latest
        ServerImage latestImage = getServerImageRepo().findLatest();

        if (!latestImage.getId().equals(id)) {
            if(latest){
                latestImage.setLatest(false);
                getServerImageRepo().save(latestImage);
            }
            serverImage.setLatest(latest);
        }

        serverImage.setProviderImageId(providerImageId);
        serverImage.setVersion(version);
        serverImage.setDescription(description);
        serverImage.setDcRootPassword(password);
        serverImage.setIamRole(iamRole);
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
            notifier = new Notifier(notificationMgr.getEmailer(), amaEndpoint);
        }
        return notifier;
    }
  
}
