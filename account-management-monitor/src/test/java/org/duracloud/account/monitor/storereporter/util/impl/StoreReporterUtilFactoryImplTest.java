/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.storereporter.util.impl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.monitor.storereporter.util.StoreReporterUtil;
import org.duracloud.account.monitor.storereporter.util.StoreReporterUtilFactory;
import org.duracloud.common.model.Credential;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: 5/18/12
 */
public class StoreReporterUtilFactoryImplTest {

    private StoreReporterUtilFactory factory;
    private Credential credential = new Credential("user", "pass");
    private int thresholdDays = 3;

    @Before
    public void setUp() throws Exception {
        factory = new StoreReporterUtilFactoryImpl(thresholdDays);
    }

    @Test
    public void testGetInstanceUtil() throws Exception {
        AccountInfo acct0 = createAccount(0);
        AccountInfo acct1 = createAccount(1);

        StoreReporterUtil reporterUtil0 = factory.getStoreReporterUtil(acct0,
                                                                       credential);
        Assert.assertNotNull(reporterUtil0);

        StoreReporterUtil reporterUtil1 = factory.getStoreReporterUtil(acct1,
                                                                       credential);
        Assert.assertNotNull(reporterUtil1);

        StoreReporterUtil reporterUtilX = factory.getStoreReporterUtil(acct0,
                                                                       credential);
        Assert.assertNotNull(reporterUtilX);
        Assert.assertEquals(reporterUtil0, reporterUtilX);
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
