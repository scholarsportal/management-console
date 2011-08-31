/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.compute;

import org.duracloud.account.compute.error.DuracloudInstanceNotAvailableException;

/**
 * @author Bill Branan
 *         Date: Feb 7, 2011
 */
public interface DuracloudComputeProvider{

    /**
     * Starts a new instance based on the given server image.
     *
     * @param providerImageId the ID of the server image to use in creating
     *                        a new instance
     * @param securityGroup the security group which is used to define the
     *                      firewall settings for an instance
     * @param keyname the name of the key pair which can be used to access
     *                the instance server
     * @param elasticIp the IP address to be associated with this instance
     * @return provider instance ID
     */
    public String start(String providerImageId,
                        String securityGroup,
                        String keyname,
                        String elasticIp);

    /**
     * Stops a running instance.
     *
     * @param providerInstanceId the ID of the instance as known by the provider
     */
    public void stop(String providerInstanceId);

    /**
     * Restarts a running instance.
     *
     * @param providerInstanceId the ID of the instance as known by the provider
     */
    public void restart(String providerInstanceId);

    /**
     * Retrieves the status of a running instance
     *
     * @param providerInstanceId the ID of the instance as known by the provider
     * @return the current status of the instance
     */
    public String getStatus(String providerInstanceId)
        throws DuracloudInstanceNotAvailableException;

}
