/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.error;

import org.duracloud.common.error.DuraCloudRuntimeException;

/**
 * @author: Bill Branan
 * Date: 4/8/11
 */
public class AccountRequiresOwnerException extends DuraCloudRuntimeException {

    public AccountRequiresOwnerException(String message) {
        super(message);
    }

}
