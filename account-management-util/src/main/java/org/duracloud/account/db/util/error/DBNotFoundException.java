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
 * @author Andrew Woods
 * Date: Oct 8, 2010
 */
public class DBNotFoundException extends DuraCloudCheckedException {
    public DBNotFoundException(String msg) {
        super(msg);
    }

    public DBNotFoundException(Exception e) {
        super(e);
    }
}
