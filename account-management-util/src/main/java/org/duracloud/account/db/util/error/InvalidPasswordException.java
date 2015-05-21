/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
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
