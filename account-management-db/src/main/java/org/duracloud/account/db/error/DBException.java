/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.error;

import org.duracloud.common.error.DuraCloudRuntimeException;

/**
 * @author Andrew Woods
 *         Date: Oct 8, 2010
 */
public class DBException extends DuraCloudRuntimeException {
    public DBException(Throwable t) {
        super(t);
    }

    public DBException(String msg) {
        super(msg);
    }
}
