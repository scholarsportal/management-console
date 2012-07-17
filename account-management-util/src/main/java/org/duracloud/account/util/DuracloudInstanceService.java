/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.InstanceType;
import org.duracloud.account.compute.error.DuracloudInstanceNotAvailableException;
import org.springframework.security.access.annotation.Secured;

import java.util.Set;

/**
 * An interface for controlling a deployed duracloud instance
 *
 * @author: Bill Branan
 * Date: Feb 3, 2011
 */
public interface DuracloudInstanceService {

    /**
     * Gets the id of the account
     *
     * @return acctId
     */
    @Secured({"role:ROLE_ANONYMOUS, scope:ANY"})
    public int getAccountId();

    /**
     * Gets information about the underlying Duracloud instance.
     */
    @Secured({"role:ROLE_ANONYMOUS, scope:ANY"})
    public DuracloudInstance getInstanceInfo();

    /**
     * Gets the version of this instance
     */
    @Secured({"role:ROLE_ANONYMOUS, scope:ANY"})
    public String getInstanceVersion();

    /**
     * Returns the state of the Duracloud instance.
     */
    @Secured({"role:ROLE_USER, scope:SELF_ACCT"})
    public String getStatus() throws DuracloudInstanceNotAvailableException;

    /**
     * Returns the hardware type of the instance.
     * @return
     * @throws DuracloudInstanceNotAvailableException
     */
    @Secured({"role:ROLE_ANONYMOUS, scope:ANY"})
    public InstanceType getInstanceType()
        throws DuracloudInstanceNotAvailableException;

    /**
     * 
     * Returns the state of the Duracloud with relaxed security.
     */
    @Secured({"role:ROLE_ANONYMOUS, scope:ANY"})
    public String getStatusInternal() throws DuracloudInstanceNotAvailableException;

    /**
     * Stops the instance.
     * Stopped instances cannot be restarted (stop == terminate).
     */
    @Secured({"role:ROLE_ADMIN, scope:SELF_ACCT"})
    public void stop();

    /**
     * Restarts the server instance and calls initialize
     */
    @Secured({"role:ROLE_ADMIN, scope:SELF_ACCT"})
    public void restart();

    /**
     * Collects all of the necessary information and initializes a
     * Duracloud instance
     */
    @Secured({"role:ROLE_ADMIN, scope:SELF_ACCT"})
    public void initialize();

    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public void reInitializeUserRoles();

    @Secured({"role:ROLE_ROOT, scope:ANY"})
    public void reInitialize();

    /**
     * Pushes user role info to the running instance.
     * Note: This set of users will replace the existing configuration on the
     * running instance.
     * @param users to update
     */
    @Secured({"role:ROLE_ANONYMOUS, scope:ANY"}) // should be admin
    public void setUserRoles(Set<DuracloudUser> users);

}
