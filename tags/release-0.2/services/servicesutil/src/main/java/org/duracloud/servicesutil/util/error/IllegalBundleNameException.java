package org.duracloud.servicesutil.util.error;

import org.duracloud.common.error.DuraCloudRuntimeException;

/**
 * @author Andrew Woods
 *         Date: Dec 11, 2009
 */
public class IllegalBundleNameException extends DuraCloudRuntimeException {

    public IllegalBundleNameException(String msg) {
        super(msg);
    }
}
