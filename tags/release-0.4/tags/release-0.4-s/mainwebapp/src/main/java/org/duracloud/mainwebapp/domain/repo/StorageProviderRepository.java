/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.domain.repo;

import java.util.List;

import org.duracloud.mainwebapp.domain.model.StorageProvider;
import org.duracloud.storage.domain.StorageProviderType;

public interface StorageProviderRepository {

    /**
     * {@inheritDoc}
     */
    public abstract StorageProvider findStorageProviderById(int id)
            throws Exception;

    /**
     * {@inheritDoc}
     */
    public abstract List<Integer> getStorageProviderIds() throws Exception;

    public abstract int saveStorageProvider(StorageProvider storageProvider)
            throws Exception;

    public abstract int findStorageProviderIdByProviderType(StorageProviderType providerType)
            throws Exception;

}