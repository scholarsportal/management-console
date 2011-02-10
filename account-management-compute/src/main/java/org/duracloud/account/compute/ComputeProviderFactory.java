/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.compute;

/**
 * @author: Bill Branan
 * Date: 2/10/11
 */
public interface ComputeProviderFactory {

    /**
     * Creates a DuracloudComputeProvider which connects to a specific
     * underlying compute provider.
     *
     * @return a new DuracloudComputeProvider
     */
    public DuracloudComputeProvider createComputeProvider(String username,
                                                          String password);

}
