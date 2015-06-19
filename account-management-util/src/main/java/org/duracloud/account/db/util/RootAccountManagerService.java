/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.DuracloudUser;
import org.duracloud.account.db.model.ServerImage;
import org.duracloud.account.db.model.StorageProviderAccount;
import org.duracloud.account.db.util.error.DBNotFoundException;
import org.duracloud.account.db.util.error.UnsentEmailException;
import org.springframework.security.access.annotation.Secured;


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
    public void deleteUser(Long id);

    /**
     * Delete an account from the system
     *
     * @param id
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public void deleteAccount(Long id);

    /**
     * Gets an account from the system
     *
     * @param id
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public AccountInfo getAccount(Long id);

    /**
     * Creates a service image in the system
     *
     * @param providerImageId
     * @param version
     * @param description
     * @param password
     * @param latest
     * @param iamRole 
     * @param cfKeyPath 
     * @param cfAccountId 
     * @param cfKeyId 
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public void createServerImage(String providerImageId,
                                  String version,
                                  String description,
                                  String password,
                                  boolean latest, 
                                  String iamRole, 
                                  String cfKeyPath, 
                                  String cfAccountId, 
                                  String cfKeyId);
    /**
     * Edits a service image in the system
     *
     * @param id
     * @param providerImageId
     * @param version
     * @param description
     * @param password
     * @param latest
     * @param iamRole
     * @param cfKeyPath 
     * @param cfAccountId 
     * @param cfKeyId 
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public void editServerImage(Long id,
                                String providerImageId,
                                String version,
                                String description,
                                String password,
                                boolean latest,
                                String iamRole, 
                                String cfKeyPath, 
                                String cfAccountId, 
                                String cfKeyId);

    /**
     * Gets a server image from the system
     *
     * @param id
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public ServerImage getServerImage(Long id);

    /**
     * Deletes a server image from the system
     *
     * @param id
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public void deleteServerImage(Long id);

    /**
     * Gets secondary storage providers for an account from the system
     *
     * @param id
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public List<StorageProviderAccount> getSecondaryStorageProviders(Long id);

    /**
     * Sets an account in the system to active
     *
     * @param id
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public void activateAccount(Long id);

    /**
     * Sets up a storage provider
     *
     * @param id
     * @param username
     * @param password
     * @param storageLimit 
     * @parma properties
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public void setupStorageProvider(Long id, String username, String password, Map<String,String> properties, int storageLimit);

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
    public void setupComputeProvider(Long id, String username, String password,
                                     String elasticIp, String keypair, String securityGroup);

    /**
     * Reset a user's password
     *
     * @param userId
     * @throws UnsentEmailException
     */
    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public void resetUsersPassword(Long userId)
        throws DBNotFoundException, UnsentEmailException;
}
