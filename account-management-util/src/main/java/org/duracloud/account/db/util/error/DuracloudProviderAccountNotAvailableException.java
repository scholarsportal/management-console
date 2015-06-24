/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util.error;

import org.duracloud.common.error.DuraCloudRuntimeException;

/**
 * @author: Bill Branan
 * Date: Feb 21, 2011
 */
public class DuracloudProviderAccountNotAvailableException extends DuraCloudRuntimeException {

    private static final long serialVersionUID = 1L;

    public DuracloudProviderAccountNotAvailableException(int providerAccountId) {
        super("Compute Provider Account with ID " + providerAccountId +
              " could not be found in the database");
    }

    public DuracloudProviderAccountNotAvailableException(String message) {
        super(message);
    }

    public DuracloudProviderAccountNotAvailableException(String message,
                                                         Throwable cause) {
        super(message, cause);
    }
}
