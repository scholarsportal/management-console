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
