/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.error;

import org.duracloud.common.error.DuraCloudCheckedException;
import org.duracloud.storage.domain.StorageProviderType;

/**
 * @author Andrew Woods
 *         Date: 7/11/11
 */
public class UnsupportedStorageProviderException extends DuraCloudCheckedException {

    public UnsupportedStorageProviderException(StorageProviderType type) {
        super(type.name());
    }
}
