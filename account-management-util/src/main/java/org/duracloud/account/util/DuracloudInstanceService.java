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

    /**
     * Gets information about the underlying Duracloud instance.
     */
    public DuracloudInstance getInstanceInfo();

    /**
     * Returns the state of the Duracloud instance.
     */
    public String getStatus();

    /**
     * Stops the instance.
     * Stopped instances cannot be restarted (stop == terminate).
     */
    public void stop();

    /**
     * Restarts the server instance and calls initialize
     */
    public void restart();

    /**
     * Collects all of the necessary information and initializes a
     * Duracloud instance
     */
    public void initialize();

    /**
     * Pushes user role info to the running instance.
     * Note: This set of users will replace the existing configuration on the
     * running instance.
     * @param users to update
     */
    public void setUserRoles(Set<DuracloudUser> users);    

}
