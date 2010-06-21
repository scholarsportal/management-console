package org.duracloud.unittestdb.error;

import org.duracloud.common.error.DuraCloudRuntimeException;

/**
 * @author Andrew Woods
 *         Date: Mar 15, 2010
 */
public class UnknownResourceTypeException extends DuraCloudRuntimeException {

    public UnknownResourceTypeException(String msg) {
        super(msg);
    }
}
