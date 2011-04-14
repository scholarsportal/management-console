/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.error;

import org.duracloud.common.error.DuraCloudCheckedException;

/**
 * @author Andrew Woods
 *         Date: Oct 8, 2010
 */
public class DBNotFoundException extends DuraCloudCheckedException {
    public DBNotFoundException(String msg) {
        super(msg);
    }

    public DBNotFoundException(Exception e) {
        super(e);
    }
}
