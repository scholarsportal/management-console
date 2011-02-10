/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.compute;

/**
 * @author: Bill Branan
 * Date: 2/9/11
 */
public class ComputeProviderUtil {

    private ComputeProviderFactory providerFactory;

    public ComputeProviderUtil(ComputeProviderFactory providerFactory) {
        this.providerFactory = providerFactory;
    }

    public DuracloudComputeProvider getComputeProvider(String username,
                                                       String password) {
        return providerFactory.createComputeProvider(username, password);
    }

}
