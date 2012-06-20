/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.ldap.error;

import org.duracloud.common.error.DuraCloudRuntimeException;

/**
 * @author Andrew Woods
 *         Date: 6/7/12
 */
public class ContextMapperException extends DuraCloudRuntimeException {

    public ContextMapperException(String msg) {
        super(msg);
    }

    public ContextMapperException(String msg, Throwable e) {
        super(msg, e);
    }
}
