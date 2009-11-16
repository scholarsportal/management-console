package org.duracloud.duraservice.error;

import org.duracloud.common.util.error.DuraCloudException;

/**
 * @author: Bill Branan
 * Date: Nov 11, 2009
 */
public class NoSuchServiceComputeInstanceException extends DuraCloudException {

    public NoSuchServiceComputeInstanceException(String instanceHost) {
        super("No service compute instance is available on host " +  instanceHost,
              "duracloud.error.duraservice.nosuchservicecomputeinstance");
        setArgs(instanceHost);
    }
}
