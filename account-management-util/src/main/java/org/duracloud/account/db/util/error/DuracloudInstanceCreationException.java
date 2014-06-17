/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.error;

import org.duracloud.common.error.DuraCloudRuntimeException;

/**
 * @author: Bill Branan
 * Date: 4/1/11
 */
public class DuracloudInstanceCreationException extends DuraCloudRuntimeException {

    public DuracloudInstanceCreationException(String message) {
        super(message);
    }

    public DuracloudInstanceCreationException(String message,
                                              Throwable throwable) {
        super(message, throwable);
    }
    
}
