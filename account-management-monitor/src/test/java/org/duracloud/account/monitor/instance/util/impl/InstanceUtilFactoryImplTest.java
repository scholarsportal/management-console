/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.instance.util.impl;

import org.duracloud.account.db.model.AccountInfo;
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
        AccountInfo acct0 = createAccount(0L);
        AccountInfo acct1 = createAccount(1L);

        InstanceUtil instanceUtil0 = factory.getInstanceUtil(acct0);
        Assert.assertNotNull(instanceUtil0);

        InstanceUtil instanceUtil1 = factory.getInstanceUtil(acct1);
        Assert.assertNotNull(instanceUtil1);

        InstanceUtil instanceUtilX = factory.getInstanceUtil(acct0);
        Assert.assertNotNull(instanceUtilX);
        Assert.assertEquals(instanceUtil0, instanceUtilX);
    }

    private AccountInfo createAccount(Long id) {
        AccountInfo account = new AccountInfo();
        account.setId(id);
        account.setSubdomain("subdomain-" + id);
        account.setAcctName("acctName-" + id);
        return account;
    }
}
