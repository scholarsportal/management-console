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
 * Date: Dec 15, 2010
 */
public class InvalidRedemptionCodeException extends DuraCloudCheckedException {

    private static final long serialVersionUID = 1L;

    public InvalidRedemptionCodeException(String redemptionCode) {
        super("No redemption code with value " + redemptionCode +
              " exists in the system.");
    }
}
