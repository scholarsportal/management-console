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
 * Date: 4/1/11
 */
public class DuracloudInstanceCreationException extends DuraCloudRuntimeException {

    public DuracloudInstanceCreationException(String message) {
        super(message);
    }

    public DuracloudInstanceCreationException(String message,
                                              Throwable throwable) {
        super(message, throwable);
    }

}
