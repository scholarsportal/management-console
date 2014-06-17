/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.error;

import org.duracloud.common.error.DuraCloudCheckedException;

/**
 * @author Andrew Woods
 *         Date: Oct 9, 2010
 */
public class UserAlreadyExistsException extends DuraCloudCheckedException {
    public UserAlreadyExistsException(String msg) {
        super(msg);
    }
}
