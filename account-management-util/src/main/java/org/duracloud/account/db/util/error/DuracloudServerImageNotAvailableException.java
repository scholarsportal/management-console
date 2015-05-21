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
 * Date: Feb 23, 2011
 */
public class DuracloudServerImageNotAvailableException extends DuraCloudRuntimeException {

    private static final long serialVersionUID = 1L;

    public DuracloudServerImageNotAvailableException(String message) {
        super(message);
    }

    public DuracloudServerImageNotAvailableException(String message,
                                                     Throwable cause) {
        super(message, cause);
    }

}
