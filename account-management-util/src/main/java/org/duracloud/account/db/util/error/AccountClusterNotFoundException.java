/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.error;

import org.duracloud.common.error.DuraCloudCheckedException;

/**
 * @author: Bill Branan
 * Date: 2/21/12
 */
public class AccountClusterNotFoundException extends DuraCloudCheckedException {

    private static final long serialVersionUID = 1L;

    public AccountClusterNotFoundException() {
        super();
    }

    public AccountClusterNotFoundException(Long accountClusterId) {
        super("Account Cluster with ID " + accountClusterId +
              " could not be found in the database");
    }

}
