/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.error;

import org.duracloud.common.error.DuraCloudCheckedException;
import org.duracloud.common.error.DuraCloudRuntimeException;

/**
 * @author Andrew Woods
 *         Date: Dec 9, 2010
 */
public class DBUninitializedException extends DuraCloudRuntimeException {

    public DBUninitializedException(String msg) {
        super(msg);
    }
}
