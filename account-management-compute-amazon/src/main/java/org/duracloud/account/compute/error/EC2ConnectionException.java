/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.compute.error;

import org.duracloud.common.error.DuraCloudRuntimeException;

/**
 * @author: Bill Branan
 * Date: Feb 7, 2011
 */
public class EC2ConnectionException extends DuraCloudRuntimeException {

    private static final long serialVersionUID = 1L;

    public EC2ConnectionException(String message) {
        super(message);
    }

    public EC2ConnectionException(String message, Throwable throwable) {
        super(message, throwable); 
    }

}
