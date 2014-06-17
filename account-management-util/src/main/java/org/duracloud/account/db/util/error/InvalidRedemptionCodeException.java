/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.db.util.error;

import org.duracloud.common.error.DuraCloudCheckedException;

/**
 * @author: Bill Branan
 * Date: Dec 15, 2010
 */
public class InvalidRedemptionCodeException extends DuraCloudCheckedException {

    private static final long serialVersionUID = 1L;

    public InvalidRedemptionCodeException(String redemptionCode) {
        super("No redemption code with value " + redemptionCode +
              " exists in the system.");
    }
}
