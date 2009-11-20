package org.duracloud.duraservice.error;

import org.duracloud.common.error.DuraCloudCheckedException;

/**
 * @author: Bill Branan
 * Date: Nov 12, 2009
 */
public class NoSuchDeployedServiceException extends DuraCloudCheckedException {

    private static final String messageKey =
        "duracloud.error.duraservice.nosuchdeployedservice";

    public NoSuchDeployedServiceException(int serviceId, int deploymentId) {
        super("There is no deployed service with service ID  " +  serviceId +
              " and deployment ID " + deploymentId, messageKey);
        setArgs(new Integer(serviceId).toString(),
                new Integer(deploymentId).toString());
    }
}