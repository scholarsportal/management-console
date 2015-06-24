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
 * Date: 2/10/11
 */
public class AmazonComputeProviderFactory implements ComputeProviderFactory {

    @Override
    public DuracloudComputeProvider createComputeProvider(String username,
                                                          String password) {
        return new AmazonComputeProvider(username, password);
    }

}
