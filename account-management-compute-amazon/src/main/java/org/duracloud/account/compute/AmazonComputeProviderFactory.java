/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.compute;

/**
 * @author: Bill Branan
 * Date: 2/10/11
 */
public class AmazonComputeProviderFactory implements ComputeProviderFactory {

    @Override
    public DuracloudComputeProvider createComputeProvider(String username,
                                                          String password) {
        return new AmazonComputeProvider(username, password);
    }

}
