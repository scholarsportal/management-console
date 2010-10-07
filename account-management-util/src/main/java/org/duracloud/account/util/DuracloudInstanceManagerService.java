/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.util;

import org.duracloud.account.util.error.DuracloudInstanceNotAvailableException;
import org.duracloud.account.util.error.DuracloudInvalidVersionException;

/**
 * Lifecycle operations for Duracloud instances.
 *
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public interface DuracloudInstanceManagerService {
    /**
     * Removes and stops the current instance if there is one. Then creates a
     * new instance.
     *
     * @param version FIXME specify format of version parameter
     * @throws DuracloudInvalidVersionException
     *
     */
    public void createNewInstance(String version)
        throws DuracloudInvalidVersionException;

    /**
     * Stops and removes the instance (if there is one).
     */
    public void removeInstance();

    /**
     * @return
     * @throws org.duracloud.account.util.error.DuracloudInstanceNotAvailableException
     *
     */
    public DuracloudInstanceService getInstance()
        throws DuracloudInstanceNotAvailableException;

}
