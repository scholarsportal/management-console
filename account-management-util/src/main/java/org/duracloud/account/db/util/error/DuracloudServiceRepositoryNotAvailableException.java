/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.error;

import org.duracloud.common.error.DuraCloudRuntimeException;

/**
 * @author: Bill Branan
 * Date: Feb 23, 2011
 */
public class DuracloudServiceRepositoryNotAvailableException extends DuraCloudRuntimeException {

    private static final long serialVersionUID = 1L;

    public DuracloudServiceRepositoryNotAvailableException(String message) {
        super(message);
    }

    public DuracloudServiceRepositoryNotAvailableException(String message,
                                                     Throwable cause) {
        super(message, cause);
    }

}
