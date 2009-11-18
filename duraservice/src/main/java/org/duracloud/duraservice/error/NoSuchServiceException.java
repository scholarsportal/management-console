package org.duracloud.duraservice.error;

import org.duracloud.common.util.error.DuraCloudException;

/**
 * @author: Bill Branan
 * Date: Nov 13, 2009
 */
public class NoSuchServiceException extends DuraCloudException {

    public NoSuchServiceException(int serviceId) {
        super("There is no service with service ID  " +  serviceId,
              "duracloud.error.duraservice.nosuchservice");
        setArgs(new Integer(serviceId).toString());
    }
}