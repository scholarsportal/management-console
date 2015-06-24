/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.db.util.error;

import org.duracloud.common.error.DuraCloudRuntimeException;

/**
 * @author Andrew Woods
 *         Date: 4/5/12
 */
public class DurabossUpdateException extends DuraCloudRuntimeException {

    public DurabossUpdateException(String host, String msg) {
        super("Unable to update DuraBoss at host: " + host + " due to: " + msg);
    }

    public DurabossUpdateException(String msg, Throwable e) {
        super("Unable to update DuraBoss : " + msg + " due to: " +
                  e.getMessage());
    }

}
