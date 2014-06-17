/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.error;

import org.duracloud.common.error.DuraCloudRuntimeException;

/**
 * @author: Bill Branan
 * Date: 3/29/11
 */
public class InstanceAccountNotFoundException extends DuraCloudRuntimeException {

    private static final long serialVersionUID = 1L;

    public InstanceAccountNotFoundException() {
        super();
    }

    public InstanceAccountNotFoundException(Long instanceId, Long accountId) {
        super("Account with ID " + accountId +
              " (associated with Instance with ID " + instanceId +
              ") could not be found in the database");
    }

}
