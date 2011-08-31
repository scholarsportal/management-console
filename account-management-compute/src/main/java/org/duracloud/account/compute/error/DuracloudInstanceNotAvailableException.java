/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.compute.error;

import org.duracloud.common.error.DuraCloudCheckedException;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class DuracloudInstanceNotAvailableException extends DuraCloudCheckedException {

    private static final long serialVersionUID = 1L;

    public DuracloudInstanceNotAvailableException(String message) {
        super(message);
    }

    public DuracloudInstanceNotAvailableException(String message,
                                                  Throwable cause) {
        super(message, cause);
    }
}
