/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.error;

import org.duracloud.common.error.DuraCloudCheckedException;

/**
 * @author "Daniel Bernstein (dbernstein@duraspace.org)"
 */
public class AccountNotFoundException extends DuraCloudCheckedException {

    private static final long serialVersionUID = 1L;

    public AccountNotFoundException() {
        super();
    }

    public AccountNotFoundException(Long accountId) {
        super("Account with ID " + accountId +
              " could not be found in the database");
    }

}
