/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.util;

import org.duracloud.common.error.DuraCloudCheckedException;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class DuracloudInstanceNotAvailableException extends DuraCloudCheckedException {

    private static final long serialVersionUID = 1L;

    public DuracloudInstanceNotAvailableException(String message,
                                                  Throwable cause) {
        super(message, cause);
    }
}
