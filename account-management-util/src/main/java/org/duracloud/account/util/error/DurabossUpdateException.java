/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.error;

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
