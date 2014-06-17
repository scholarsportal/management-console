/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.error;

import org.duracloud.common.error.DuraCloudRuntimeException;

/**
 * @author Andrew Woods
 *         Date: 3/18/11
 */
public class UnsentEmailException extends DuraCloudRuntimeException {

    public UnsentEmailException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
