/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.apache.commons.lang.NotImplementedException;
import org.duracloud.account.common.domain.AccountCluster;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.AccountType;
import org.duracloud.account.common.domain.ComputeProviderAccount;
import org.duracloud.account.common.domain.DuracloudGroup;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.common.domain.ServerDetails;
import org.duracloud.account.common.domain.ServerImage;
import org.duracloud.account.common.domain.ServicePlan;
import org.duracloud.account.common.domain.ServiceRepository;
import org.duracloud.account.common.domain.ServiceRepository.ServiceRepositoryType;
import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.common.domain.UserInvitation;
import org.duracloud.account.db.DuracloudAccountClusterRepo;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudGroupRepo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.DuracloudRightsRepo;
import org.duracloud.account.db.DuracloudServerImageRepo;
import org.duracloud.account.db.DuracloudServiceRepositoryRepo;
import org.duracloud.account.db.DuracloudStorageProviderAccountRepo;
import org.duracloud.account.db.DuracloudUserRepo;
import org.duracloud.account.db.IdUtil;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.DuracloudInstanceManagerService;
import org.duracloud.account.util.DuracloudInstanceService;
import org.duracloud.account.util.RootAccountManagerService;
import org.duracloud.account.util.error.UnsentEmailException;
import org.duracloud.account.util.notification.NotificationMgr;
import org.duracloud.account.util.notification.Notifier;
import org.duracloud.account.util.usermgmt.UserDetailsPropagator;
import org.duracloud.account.util.util.AccountUtil;
import org.duracloud.common.error.DuraCloudRuntimeException;
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
    private AccountUtil accountUtil;
    private DuracloudInstanceManagerService instanceManagerService;

    public RootAccountManagerServiceImpl(DuracloudRepoMgr duracloudRepoMgr,
                                    NotificationMgr notificationMgr,
                                    UserDetailsPropagator propagator,
                                    AccountUtil accountUtil,
                                    DuracloudInstanceManagerService instanceManagerService) {
        this.repoMgr = duracloudRepoMgr;
        this.notificationMgr = notificationMgr;
        this.propagator = propagator;
        this.accountUtil = accountUtil;
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

        // Remove all user rights
        Set<AccountRights> accountRights =
            getRightsRepo().findByUserId(userId);
        for(AccountRights accountRight : accountRights) {
            getRightsRepo().delete(accountRight.getId());

            log.debug("Propagating rights revocation for user {} " +
                      "to account {}", userId, accountRight.getAccountId());

            propagator.propagateRevocation(accountRight.getAccountId(),
                                           userId);
        }

        // Remove user from all groups
        DuracloudGroupRepo groupRepo = getGroupRepo();
        Set<DuracloudGroup> allGroups = groupRepo.findAllGroups();
        for(DuracloudGroup group : allGroups) {
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

        // Remove the user
        getUserRepo().delete(userId);
	}

    @Override
    public void deleteAccountCluster(int clusterId) {
        log.info("Deleting account cluster with ID {}", clusterId);

        // Find the cluster
        DuracloudAccountClusterRepo clusterRepo = getClusterRepo();
        AccountCluster cluster = getCluster(clusterId, clusterRepo);

        // Reset the cluster ID of all accounts in this cluster
        Set<Integer> clusterAccounts = cluster.getClusterAccountIds();
        DuracloudAccountRepo accountRepo = getAccountRepo();
        for(int accountId : clusterAccounts) {
            try {
                AccountInfo account = accountRepo.findById(accountId);
                account.setAccountClusterId(-1);
                accountRepo.save(account);
            } catch(DBNotFoundException e) {
                log.warn("Account with ID " + accountId + " not found in the " +
                         "DB, cannot update cluster membership");
            } catch (DBConcurrentUpdateException e) {
                String error = "Could not remove account with ID " + accountId +
                    " from cluster due to a DBConcurrentUpdateException";
                throw new DuraCloudRuntimeException(error, e);
            }
        }

        // Remove the cluster
        clusterRepo.delete(clusterId);
    }

    private AccountCluster getCluster(int clusterId,
                                      DuracloudAccountClusterRepo clusterRepo) {
        try {
            return clusterRepo.findById(clusterId);
        } catch(DBNotFoundException e) {
            String error = "Cluster with ID " + clusterId + " was not found " +
                           "in the database, so could not be deleted.";
            throw new DuraCloudRuntimeException(error);
        }
    }

    @Override
	public void deleteAccount(int accountId) {
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

            ServerDetails serverDetails = accountUtil.getServerDetails(account);

            // Delete the primary storage provider
            getStorageRepo()
                .delete(serverDetails.getPrimaryStorageProviderAccountId());

            // Delete any secondary storage providers
            for(int secId : serverDetails.getSecondaryStorageProviderAccountIds()) {
                getStorageRepo().delete(secId);
            }

            // Delete the compute provider
            repoMgr.getComputeProviderAccountRepo()
                .delete(serverDetails.getComputeProviderAccountId());
            
            // Delete the server details
            repoMgr.getServerDetailsRepo().delete(serverDetails.getId());
        }

        // Delete the account rights
        Set<AccountRights> rights =
            getRightsRepo().findByAccountIdSkipRoot(accountId);
        for(AccountRights right : rights) {
            getRightsRepo().delete(right.getId());
        }

        // Delete the groups associated with the account
        DuracloudGroupRepo groupRepo = repoMgr.getGroupRepo();
        Set<DuracloudGroup> groups = groupRepo.findByAccountId(accountId);
        for(DuracloudGroup group : groups) {
            groupRepo.delete(group.getId());
        }

        // Delete any user invitations
        for(UserInvitation invitation :
            repoMgr.getUserInvitationRepo().findByAccountId(accountId)) {
            repoMgr.getUserInvitationRepo().delete(invitation.getId());
        }

        // Update cluster if necessary
        removeAccountFromCluster(account);

        // Delete account
        getAccountRepo().delete(accountId);
	}

    private void removeAccountFromCluster(AccountInfo account) {
        int accountId = account.getId();
        int clusterId = account.getAccountClusterId();
        if(clusterId > -1) { // Account is part of a cluster
            // Update the cluster to no longer include this account
            DuracloudAccountClusterRepo clusterRepo = getClusterRepo();
            AccountCluster cluster = getCluster(clusterId, clusterRepo);
            Set<Integer> clusterAcctIds = cluster.getClusterAccountIds();
            clusterAcctIds.remove(accountId);
            cluster.setClusterAccountIds(clusterAcctIds);
            try {
                clusterRepo.save(cluster);
            } catch (DBConcurrentUpdateException e) {
                String error = "Could not remove account with ID " + accountId +
                    " from cluster with ID " + clusterId +
                    " due to a DBConcurrentUpdateException";
                throw new DuraCloudRuntimeException(error, e);
            }

            // Propagate any changes to cluster users/groups
            propagator.propagateClusterUpdate(accountId, clusterId);
        }
    }

    @Override
    public List<StorageProviderAccount> getSecondaryStorageProviders(int accountId) {
        ServerDetails serverDetails =
            accountUtil.getServerDetails(getAccount(accountId));

        List<StorageProviderAccount> accounts =
            new ArrayList<StorageProviderAccount>();

        try {
            for(int secId : serverDetails.getSecondaryStorageProviderAccountIds()) {
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
                user.setAccountRights(
                    getRightsRepo().findByUserId(user.getId()));
                users.add(user);
            }
		}
		return users;

	}

	@Override
	public Set<ServerImage> listAllServerImages(String filter) {
		Set<Integer> imageIds = getServerImageRepo().getIds();
		Set<ServerImage> images = new HashSet<ServerImage>();
        ServerImage image = null;
		for(int id : imageIds){
            try{
				image = getServerImageRepo().findById(id);
			}catch(DBNotFoundException ex){
				log.error("No Server Image found with ID {}", id);
                continue;
			}

            if(filter == null ||
                (image.getProviderImageId().startsWith(filter))
            ){

                images.add(image);
            }
		}
		return images;
	}

	@Override
	public Set<ServiceRepository> listAllServiceRepositories(String filter) {
		Set<Integer> repoIds = getServiceRepositoryRepo().getIds();
		Set<ServiceRepository> repositories = new HashSet<ServiceRepository>();
        ServiceRepository repository = null;
		for(int id : repoIds){
            try{
				repository = getServiceRepositoryRepo().findById(id);
			}catch(DBNotFoundException ex){
				log.error("No Service Repository found with ID {}", id);
                continue;
			}

            if(filter == null ||
                (repository.getHostName().startsWith(filter))
            ){

                repositories.add(repository);
            }
		}
		return repositories;

	}

	@Override
	public void createServerImage(int providerAccountId,
                                  String providerImageId,
                                  String version,
                                  String description,
                                  String password,
                                  boolean latest) {
        try{
            
            DuracloudServerImageRepo imageRepo = getServerImageRepo();
            if(latest) {
                //Remove current latest
                ServerImage latestImage = imageRepo.findLatest();
                latestImage.setLatest(!latest);
                imageRepo.save(latestImage);
            }
            
            //make the new server image the 'latest' if it is 
            //the first image.
            Set<Integer> servers = imageRepo.getIds();
            if(servers == null ||  servers.size() == 0){
                latest = true;
            }
            

            int id = getIdUtil().newServerImageId();

            ServerImage serverImage =
                new ServerImage(id,
                                providerAccountId,
                                providerImageId,
                                version,
                                description,
                                password,
                                latest);

            getServerImageRepo().save(serverImage);
        }catch(DBConcurrentUpdateException ex){
            log.error("Error creating Server Image");
        }
	}

	@Override
	public void editServerImage(int id,
                                int providerAccountId,
                                String providerImageId,
                                String version,
                                String description,
                                String password,
                                boolean latest) {
        try{
            if(latest) {
                //Remove current latest
                ServerImage latestImage = getServerImageRepo().findLatest();
                if(latestImage.getId() != id) {
                    latestImage.setLatest(!latest);
                    getServerImageRepo().save(latestImage);
                }
            }

            ServerImage serverImage = getServerImageRepo().findById(id);

            serverImage.setProviderAccountId(providerAccountId);
            serverImage.setProviderImageId(providerImageId);
            serverImage.setVersion(version);
            serverImage.setDescription(description);
            serverImage.setDcRootPassword(password);
            serverImage.setLatest(latest);
            
            getServerImageRepo().save(serverImage);
        }catch(DBConcurrentUpdateException ex){
            log.error("Error creating Server Image");
        }catch(DBNotFoundException ex){
            log.error("No Server Image found with ID {}", id);
        }
	}

	@Override
	public ServerImage getServerImage(int id) {
        try{
            return getServerImageRepo().findById(id);
        }catch(DBNotFoundException ex){
            log.error("No Server Image found with ID {}", id);
        }
        return null;
	}

	@Override
	public void deleteServerImage(int id) {
        log.info("Deleting server image with ID {}", id);

        getServerImageRepo().delete(id);
	}

	@Override
	public void createServiceRepository(ServiceRepositoryType serviceRepositoryType,
                                        ServicePlan servicePlan,
                                        String hostName,
                                        String spaceId,
                                        String serviceXmlId,
                                        String version,
                                        String username,
                                        String password) {
        try{
            int id = getIdUtil().newServiceRepositoryId();

            ServiceRepository serviceRepo =
                new ServiceRepository(id,
                                      serviceRepositoryType,
                                      servicePlan,
                                      hostName,
                                      spaceId,
                                      serviceXmlId,
                                      version,
                                      username,
                                      password);

            getServiceRepositoryRepo().save(serviceRepo);
        }catch(DBConcurrentUpdateException ex){
            log.error("Error creating Service Repo");
        }
	}

	@Override
	public void editServiceRepository(int id,
                                      ServiceRepositoryType serviceRepositoryType,
                                      ServicePlan servicePlan,
                                      String hostName,
                                      String spaceId,
                                      String serviceXmlId,
                                      String version,
                                      String username,
                                      String password) {
        try{
            ServiceRepository serviceRepo = getServiceRepositoryRepo().findById(id);

            serviceRepo.setServiceRepositoryType(serviceRepositoryType);
            serviceRepo.setServicePlan(servicePlan);
            serviceRepo.setHostName(hostName);
            serviceRepo.setSpaceId(spaceId);
            serviceRepo.setServiceXmlId(serviceXmlId);
            serviceRepo.setVersion(version);
            serviceRepo.setUsername(username);
            serviceRepo.setPassword(password);

            getServiceRepositoryRepo().save(serviceRepo);
        }catch(DBNotFoundException ex){
            log.error("No Service Repo found with ID {}", id);
        }catch(DBConcurrentUpdateException ex){
            log.error("Error updating Service Repo");
        }
	}

	@Override
	public ServiceRepository getServiceRepository(int id) {
        try{
            return getServiceRepositoryRepo().findById(id);
        }catch(DBNotFoundException ex){
            log.error("No Service Repo found with ID {}", id);
        }
        return null;
	}

	@Override
	public void deleteServiceRepository(int id) {
        log.info("Deleting service repo with ID {}", id);

        getServiceRepositoryRepo().delete(id);
	}

    private DuracloudServerImageRepo getServerImageRepo() {
        return repoMgr.getServerImageRepo();
    }

    private DuracloudServiceRepositoryRepo getServiceRepositoryRepo() {
        return repoMgr.getServiceRepositoryRepo();
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

    private IdUtil getIdUtil() {
        return repoMgr.getIdUtil();
    }

    private Notifier getNotifier() {
        if(null == notifier) {
            notifier = new Notifier(notificationMgr.getEmailer());
        }
        return notifier;
    }
  
}
