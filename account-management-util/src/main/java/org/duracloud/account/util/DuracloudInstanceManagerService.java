/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import org.duracloud.account.util.error.DuracloudInstanceNotAvailableException;

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
     * Retrieves a DuraCloud instance wrapped in a class which provides
     * services on the instance.
     *
     * @return
     * @throws DuracloudInstanceNotAvailableException
     *
     */
    public DuracloudInstanceService getInstanceService(String instanceId)
        throws DuracloudInstanceNotAvailableException;

}
