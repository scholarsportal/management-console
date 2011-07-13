/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
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
