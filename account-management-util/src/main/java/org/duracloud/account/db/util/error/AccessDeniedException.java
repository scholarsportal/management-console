/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.error;

import org.duracloud.common.error.DuraCloudRuntimeException;

/**
 * @author Andrew Woods
 *         Date: 4/8/11
 */
public class AccessDeniedException extends DuraCloudRuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }
}
