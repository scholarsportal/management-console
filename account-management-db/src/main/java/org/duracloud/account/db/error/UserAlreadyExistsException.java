/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.error;

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
