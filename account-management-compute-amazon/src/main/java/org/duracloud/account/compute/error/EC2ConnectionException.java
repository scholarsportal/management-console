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
