/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.storereporter.util.impl;

import org.duracloud.account.db.model.AccountInfo;
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
        AccountInfo acct0 = createAccount(0L);
        AccountInfo acct1 = createAccount(1L);

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

    private AccountInfo createAccount(Long id) {
        AccountInfo account = new AccountInfo();
        account.setId(id);
        account.setSubdomain("subdomain-" + id);
        account.setAcctName("acctName-" + id);
        return account;
    }

}
