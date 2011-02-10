/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.compute;

import org.duracloud.account.compute.AmazonComputeProvider;
import org.duracloud.account.compute.DuracloudComputeProvider;

/**
 * @author: Bill Branan
 * Date: 2/9/11
 */
public class ComputeProviderUtil {

    public DuracloudComputeProvider getComputeProvider(String username,
                                                       String password) {
        return new AmazonComputeProvider(username, password);
    }

}
