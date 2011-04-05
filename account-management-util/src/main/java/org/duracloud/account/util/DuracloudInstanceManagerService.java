/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import org.duracloud.account.util.error.DuracloudInstanceNotAvailableException;
import org.springframework.security.access.annotation.Secured;

import java.util.Set;

/**
 * Lifecycle operations for Duracloud instances.
 *
 * @author: Bill Branan
 * Date: Feb 3, 2011
 */
public interface DuracloudInstanceManagerService {

    /**
     * Starts a new instance for the given account based on the given
     * DuraCloud software version
     */
    @Secured({"role:ROLE_ADMIN, scope:self-acct"})
    public DuracloudInstanceService createInstance(int accountId, String version);

    /**
     * Retrieves all of the active DuraCloud versions
     * @return set of version options
     */
    @Secured({"role:ROLE_ADMIN, scope:self-acct"})
    public Set<String> getVersions();

    /**
     * Retrieves the DuraCloud instance with the given ID wrapped in a class
     * which provides services on the instance.
     *
     * @return
     * @throws DuracloudInstanceNotAvailableException
     *
     */
    @Secured({"role:ROLE_USER, scope:any"})
    public DuracloudInstanceService getInstanceService(int instanceId)
        throws DuracloudInstanceNotAvailableException;

    /**
     * Retrieves the Set of DuraCloud instances associated with a given account,
     * wrapped in a class which provides services on the instances. If no
     * instances exist for the account, an empty set is returned.
     *
     * @param accountId
     * @return
     *
     */
    @Secured({"role:ROLE_ADMIN, scope:self-acct"})
    public Set<DuracloudInstanceService> getInstanceServices(int accountId);

}
