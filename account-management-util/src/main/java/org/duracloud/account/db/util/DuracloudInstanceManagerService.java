/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util;

import java.util.Set;

import org.duracloud.account.compute.error.DuracloudInstanceNotAvailableException;
import org.duracloud.account.db.model.InstanceType;
import org.springframework.security.access.annotation.Secured;

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
     * @param instanceType
     */
    @Secured({"role:ROLE_ADMIN, scope:SELF_ACCT"})
    public DuracloudInstanceService createInstance(Long accountId, String version, InstanceType instanceType);

    /**
     * Retrieves all of the active DuraCloud versions
     * @return set of version options
     */
    @Secured({"role:ROLE_USER, scope:ANY"})
    public Set<String> getVersions();

    /**
     * Retrieves the latest DuraCloud version
     * @return latest version
     */
    @Secured({"role:ROLE_USER, scope:ANY"})
    public String getLatestVersion();

    /**
     * Retrieves the DuraCloud instance with the given ID wrapped in a class
     * which provides services on the instance.
     *
     * @return
     * @throws DuracloudInstanceNotAvailableException
     *
     */
    @Secured({"role:ROLE_USER, scope:ANY"})
    public DuracloudInstanceService getInstanceService(Long instanceId)
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
    @Secured({"role:ROLE_ANONYMOUS, scope:ANY"})
    public Set<DuracloudInstanceService> getInstanceServices(Long accountId);


}
