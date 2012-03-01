/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.ServerImage;
import org.duracloud.account.common.domain.ServicePlan;
import org.duracloud.account.common.domain.ServiceRepository;
import org.duracloud.account.common.domain.ServiceRepository.ServiceRepositoryType;
import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.error.UnsentEmailException;
import org.springframework.security.access.annotation.Secured;

import java.util.List;
import java.util.Set;

/**
 * An interface for the account management application administrator.
 *
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */

public interface RootAccountManagerService {

    /**
     * @param filter optional filter on org name
     * @return
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public Set<AccountInfo> listAllAccounts(String filter);

    /**
     * @param filter optional filter on username
     * @return
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public Set<DuracloudUser> listAllUsers(String filter);

    /**
     * @param filter optional filter on provider image id
     * @return
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public Set<ServerImage> listAllServerImages(String filter);

    /**
     * @param filter optional filter on host name
     * @return
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public Set<ServiceRepository> listAllServiceRepositories(String filter);

    /**
     * 
     * @param imageId
     * @param version
     * @param description
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public void addDuracloudImage(String imageId,
                                  String version,
                                  String description);

    /**
     * Delete a user from the system
     *
     * @param id
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public void deleteUser(int id);

    /**
     * Delete an account cluster from the system
     *
     * @param id
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public void deleteAccountCluster(int id);

    /**
     * Delete an account from the system
     *
     * @param id
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public void deleteAccount(int id);

    /**
     * Gets an account from the system
     *
     * @param id
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public AccountInfo getAccount(int id);

    /**
     * Creates a service image in the system
     *
     * @param providerAccountId
     * @param providerImageId
     * @param version
     * @param description
     * @param password
     * @param latest
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public void createServerImage(int providerAccountId,
                                  String providerImageId,
                                  String version,
                                  String description,
                                  String password,
                                  boolean latest);
    /**
     * Edits a service image in the system
     *
     * @param id
     * @param providerAccountId
     * @param providerImageId
     * @param version
     * @param description
     * @param password
     * @param latest
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public void editServerImage(int id,
                                int providerAccountId,
                                String providerImageId,
                                String version,
                                String description,
                                String password,
                                boolean latest);

    /**
     * Gets a server image from the system
     *
     * @param id
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public ServerImage getServerImage(int id);

    /**
     * Deletes a server image from the system
     *
     * @param id
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public void deleteServerImage(int id);

    /**
     * Creates a service repo in the system
     *
     * @param serviceRepositoryType
     * @param servicePlan
     * @param hostName
     * @param spaceId
     * @param serviceXmlId
     * @param version
     * @param username
     * @param password
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public void createServiceRepository(ServiceRepositoryType serviceRepositoryType,
                                        ServicePlan servicePlan,
                                        String hostName,
                                        String spaceId,
                                        String serviceXmlId,
                                        String version,
                                        String username,
                                        String password);
    /**
     * Updates a service repo in the system
     *
     * @param id
     * @param serviceRepositoryType
     * @param servicePlan
     * @param hostName
     * @param spaceId
     * @param serviceXmlId
     * @param version
     * @param username
     * @param password
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public void editServiceRepository(int id,
                                      ServiceRepositoryType serviceRepositoryType,
                                      ServicePlan servicePlan,
                                      String hostName,
                                      String spaceId,
                                      String serviceXmlId,
                                      String version,
                                      String username,
                                      String password);

    /**
     * Gets a service repo from the system
     *
     * @param id
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public ServiceRepository getServiceRepository(int id);

    /**
     * Deletes a service repo from the system
     *
     * @param id
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public void deleteServiceRepository(int id);

    /**
     * Gets secondary storage providers for an account from the system
     *
     * @param id
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public List<StorageProviderAccount> getSecondaryStorageProviders(int id);

    /**
     * Sets an account in the system to active
     *
     * @param id
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public void activateAccount(int id)
        throws DBConcurrentUpdateException;

    /**
     * Sets up a storage provider
     *
     * @param id
     * @param username
     * @param password
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public void setupStorageProvider(int id, String username, String password)
        throws DBConcurrentUpdateException;

    /**
     * Sets up a compute provider
     *
     * @param id
     * @param username
     * @param password
     * @param elasticIp
     * @param keypair
     * @param securityGroup
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public void setupComputeProvider(int id, String username, String password,
                                     String elasticIp, String keypair, String securityGroup)
        throws DBConcurrentUpdateException;

    /**
     * Reset a user's password
     *
     * @param userId
     * @throws DBNotFoundException
     * @throws DBConcurrentUpdateException
     * @throws UnsentEmailException
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public void resetUsersPassword(int userId)
        throws DBNotFoundException, DBConcurrentUpdateException,
               UnsentEmailException;
}
