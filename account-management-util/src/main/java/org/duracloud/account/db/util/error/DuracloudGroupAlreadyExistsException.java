/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */

package org.duracloud.account.db.util.error;

import org.duracloud.common.error.DuraCloudCheckedException;

/**
 * 
 * @author Daniel Bernstein 
 *         Date: Nov 11, 2011
 *
 */
public class DuracloudGroupAlreadyExistsException extends DuraCloudCheckedException {
    private static final long serialVersionUID = 1L;

    public DuracloudGroupAlreadyExistsException(String message) {
        super(message);
    }

    public DuracloudGroupAlreadyExistsException(String message,
                                              Throwable throwable) {
        super(message, throwable);
    }
}
