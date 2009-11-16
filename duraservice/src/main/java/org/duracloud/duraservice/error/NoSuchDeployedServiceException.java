package org.duracloud.duraservice.error;

import org.duracloud.common.util.error.DuraCloudException;

/**
 * @author: Bill Branan
 * Date: Nov 12, 2009
 */
public class NoSuchDeployedServiceException extends DuraCloudException {

    public NoSuchDeployedServiceException(String serviceId, int deploymentId) {
        super("There is no deployed service with service ID  " +  serviceId +
              " and deployment ID " + deploymentId,
              "duracloud.error.duraservice.nosuchdeployedservice");
        setArgs(serviceId, new Integer(deploymentId).toString());
    }
}