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
