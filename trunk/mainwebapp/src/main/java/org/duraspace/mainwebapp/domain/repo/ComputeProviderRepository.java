
package org.duraspace.mainwebapp.domain.repo;

import java.util.List;

import org.duraspace.mainwebapp.domain.model.ComputeProvider;
import org.duraspace.serviceprovider.domain.ComputeProviderType;

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