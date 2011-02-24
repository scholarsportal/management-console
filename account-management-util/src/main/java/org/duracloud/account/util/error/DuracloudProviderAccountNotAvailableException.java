/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.error;

import org.duracloud.common.error.DuraCloudRuntimeException;

/**
 * @author: Bill Branan
 * Date: Feb 21, 2011
 */
public class DuracloudProviderAccountNotAvailableException extends DuraCloudRuntimeException {

    private static final long serialVersionUID = 1L;

    public DuracloudProviderAccountNotAvailableException(String message) {
        super(message);
    }

    public DuracloudProviderAccountNotAvailableException(String message,
                                                         Throwable cause) {
        super(message, cause);
    }
}
