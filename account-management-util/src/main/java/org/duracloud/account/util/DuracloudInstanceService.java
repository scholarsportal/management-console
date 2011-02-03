/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util;

import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.DuracloudUser;

import java.util.Set;

/**
 * An interface for controlling a deployed duracloud instance
 *
 * @author: Bill Branan
 * Date: Feb 3, 2011
 */
public interface DuracloudInstanceService {

    public static enum InstanceState {
        STARTING,
        RUNNING,
        STOPPING;
    }

    /**
     * Gets information about the underlying Duracloud instance.
     */
    public DuracloudInstance getInstanceInfo();

    /**
     * Returns the state of the Duracloud instance.
     */
    public InstanceState getState();

    /**
     * Stops the instance.
     * Stopped instances cannot be restarted (stop == terminate).
     */
    public void stop();

    /**
     * Restarts the instance.
     */
    public void restart();

    /**
     * Pushes user role info to the running instance.
     * Note: This set of users will replace the existing configuration on the
     * running instance.
     * @param users to update
     */
    public void setUserRoles(Set<DuracloudUser> users);    

}
