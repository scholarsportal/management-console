/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.error;

import org.duracloud.common.error.DuraCloudRuntimeException;

/**
 * @author Andrew Woods
 *         Date: Feb 3, 2011
 */
public class DuracloudInstanceUpdateException extends DuraCloudRuntimeException {

    public DuracloudInstanceUpdateException(String message) {
        super(message);
    }
}
