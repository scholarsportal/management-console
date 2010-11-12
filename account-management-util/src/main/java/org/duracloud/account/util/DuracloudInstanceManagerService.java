/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
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
    public DuracloudInstanceService createNewInstance(String acctId,
                                                      String version)
        throws DuracloudInvalidVersionException;

    /**
     * Stops and removes the instance (if there is one).
     */
    public void removeInstance(String acctId);

    /**
     * @return
     * @throws org.duracloud.account.util.error.DuracloudInstanceNotAvailableException
     *
     */
    public DuracloudInstanceService getInstance(String acctId)
        throws DuracloudInstanceNotAvailableException;

}
