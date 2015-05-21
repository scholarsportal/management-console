/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
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
