/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.error;

import org.duracloud.common.error.DuraCloudRuntimeException;

/**
 * @author: Bill Branan
 * Date: 2/8/12
 */
public class DuracloudServerDetailsNotAvailableException
    extends DuraCloudRuntimeException {

    private static final long serialVersionUID = 1L;

    public DuracloudServerDetailsNotAvailableException(Long serverDetailsId) {
        super("ServerDetails with ID " + serverDetailsId +
              " could not be found in the database");
    }

}
