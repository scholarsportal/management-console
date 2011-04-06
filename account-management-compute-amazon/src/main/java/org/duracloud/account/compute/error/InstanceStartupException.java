/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.compute.error;

import org.duracloud.common.error.DuraCloudRuntimeException;

/**
 * @author: Bill Branan
 * Date: 4/6/11
 */
public class InstanceStartupException extends DuraCloudRuntimeException {

    public InstanceStartupException(String message) {
        super(message);
    }

    public InstanceStartupException(String message,
                                    Throwable throwable) {
        super(message, throwable);
    }
    
}
