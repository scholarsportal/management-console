
package org.duraspace.serviceprovider.mgmt;

public interface ComputeProviderFactory {

    public abstract ServiceProvider getComputeProvider(String providerId)
            throws Exception;

}