/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.domain.repo;

import java.util.List;

import org.duracloud.computeprovider.domain.ComputeProviderType;
import org.duracloud.mainwebapp.domain.model.ComputeProvider;

public interface ComputeProviderRepository {

    /**
     * <pre>
     * This method returns the ComputeProvider of the provided ID.
     * If no ComputeProvider is found, an exception is thrown.
     * </pre> {@inheritDoc}
     */
    public abstract ComputeProvider findComputeProviderById(int id)
            throws Exception;

    /**
     * <pre>
     * This method returns all IDs in the table.
     * If no results are found, an exception is thrown.
     * </pre> {@inheritDoc}
     */
    public abstract List<Integer> getComputeProviderIds() throws Exception;

    public abstract int saveComputeProvider(ComputeProvider computeProvider)
            throws Exception;

    public abstract int findComputeProviderIdByProviderType(ComputeProviderType providerType)
            throws Exception;

}