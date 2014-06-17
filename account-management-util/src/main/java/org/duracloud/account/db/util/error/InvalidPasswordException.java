/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.error;

import org.duracloud.common.error.DuraCloudCheckedException;

/**
 * 
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
public class InvalidPasswordException extends DuraCloudCheckedException {
    private static final long serialVersionUID = 1L;

    public InvalidPasswordException(Long userId) {
        super("Password does not match for  User(id=" + userId+")");
    }
}
