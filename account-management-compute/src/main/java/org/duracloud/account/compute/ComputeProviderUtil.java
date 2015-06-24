/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
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
