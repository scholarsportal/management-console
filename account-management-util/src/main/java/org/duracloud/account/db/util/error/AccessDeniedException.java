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
 * Date: 4/8/11
 */
public class AccessDeniedException extends DuraCloudRuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }
}
