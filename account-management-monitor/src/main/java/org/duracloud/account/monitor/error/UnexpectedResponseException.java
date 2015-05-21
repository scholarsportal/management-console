/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.error;

import org.duracloud.common.error.DuraCloudCheckedException;

/**
 * @author Andrew Woods
 *         Date: 7/17/11
 */
public class UnexpectedResponseException extends DuraCloudCheckedException {

    public UnexpectedResponseException(int statusCode, int responseCode) {
        super("expected:" + statusCode + " found:" + responseCode);
    }
}
