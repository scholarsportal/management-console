/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.error;

import org.duracloud.common.error.DuraCloudCheckedException;

/**
 * @author Andrew Woods
 *         Date: Oct 8, 2010
 */
public class DBConcurrentUpdateException extends DuraCloudCheckedException {
    public DBConcurrentUpdateException(Throwable t) {
        super(t);
    }
}
