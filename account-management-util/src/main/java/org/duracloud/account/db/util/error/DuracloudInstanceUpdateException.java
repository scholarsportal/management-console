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
 * @author Andrew Woods
 *         Date: Feb 3, 2011
 */
public class DuracloudInstanceUpdateException extends DuraCloudRuntimeException {

    public DuracloudInstanceUpdateException(String message) {
        super(message);
    }
}
