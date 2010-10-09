/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
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
