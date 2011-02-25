/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.error.DuracloudInstanceNotAvailableException;

import java.util.Set;

/**
 * Lifecycle operations for Duracloud instances.
 *
 * @author: Bill Branan
 * Date: Feb 3, 2011
 */
public interface DuracloudInstanceManagerService {

    /**
     * Starts a new instance for the given account from the given image.
     */
    public DuracloudInstanceService createInstance(int accountId, int imageId);

    /**
     * Retrieves the DuraCloud instance with the given ID and associated with
     * the given account, wrapped in a class which provides services on the
     * instance.
     *
     * @return
     * @throws DuracloudInstanceNotAvailableException
     *
     */
    public DuracloudInstanceService getInstanceService(int accountId,
                                                       int instanceId)
        throws AccountNotFoundException, DuracloudInstanceNotAvailableException;

    /**
     * Retrieves the Set of DuraCloud instances associated with a given account,
     * wrapped in a class which provides services on the instances.
     *
     * @param accountId
     * @return
     * @throws DuracloudInstanceNotAvailableException
     *
     */
    public Set<DuracloudInstanceService> getInstanceServices(int accountId)
        throws AccountNotFoundException;

}
