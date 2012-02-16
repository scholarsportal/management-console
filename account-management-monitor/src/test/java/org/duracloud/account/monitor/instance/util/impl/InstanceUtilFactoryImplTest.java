/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.instance.util.impl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.monitor.instance.util.InstanceUtil;
import org.duracloud.account.monitor.instance.util.InstanceUtilFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: 7/15/11
 */
public class InstanceUtilFactoryImplTest {

    private InstanceUtilFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new InstanceUtilFactoryImpl();
    }

    @Test
    public void testGetInstanceUtil() throws Exception {
        AccountInfo acct0 = createAccount(0);
        AccountInfo acct1 = createAccount(1);

        InstanceUtil instanceUtil0 = factory.getInstanceUtil(acct0);
        Assert.assertNotNull(instanceUtil0);

        InstanceUtil instanceUtil1 = factory.getInstanceUtil(acct1);
        Assert.assertNotNull(instanceUtil1);

        InstanceUtil instanceUtilX = factory.getInstanceUtil(acct0);
        Assert.assertNotNull(instanceUtilX);
        Assert.assertEquals(instanceUtil0, instanceUtilX);
    }

    private AccountInfo createAccount(int id) {
        return new AccountInfo(id,
                               "subdomain-" + id,
                               "acctName-" + id,
                               null,
                               null,
                               -1,
                               -1,
                               -1,
                               null,
                               null);
    }
}
