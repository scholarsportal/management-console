/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
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
