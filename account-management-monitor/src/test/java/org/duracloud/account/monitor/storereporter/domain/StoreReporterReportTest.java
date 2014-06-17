/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.storereporter.domain;

import org.duracloud.account.common.domain.AccountInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * @author Andrew Woods
 *         Date: 5/18/12
 */
public class StoreReporterReportTest {

    private StoreReporterReport report;

    @Before
    public void setUp() throws Exception {
        report = new StoreReporterReport();
    }

    @Test
    public void testAddAcctError() throws Exception {
        Assert.assertFalse(report.hasErrors());

        int idSum = 0;
        String error = "error message-";
        int numErrors = 5;
        for (int i = 0; i < numErrors; ++i) {
            AccountInfo acct = createAccountInfo(new Long(i));
            report.addAcctError(acct, error + i);

            idSum += i;
        }

        // verify errors
        Assert.assertTrue(report.hasErrors());

        Map<AccountInfo, StoreReporterInfo> errors = report.getReporterErrors();
        Assert.assertNotNull(errors);
        Assert.assertEquals(numErrors, errors.size());

        for (AccountInfo acct : errors.keySet()) {
            Long id = acct.getId();

            idSum -= id;
            Assert.assertEquals(subdomain(id), acct.getSubdomain());

            StoreReporterInfo value = errors.get(acct);
            Assert.assertNotNull(value);

            Assert.assertTrue(value.hasErrors());

            String text = value.toString();
            Assert.assertTrue(text.contains(subdomain(id)));
            Assert.assertTrue(text.contains(error + id));
        }
        Assert.assertEquals(0, idSum);

        // verify successes
        Map<AccountInfo, StoreReporterInfo> infos = report.getReporterInfos();
        Assert.assertNotNull(infos);
        Assert.assertEquals(numErrors, infos.size());
    }

    @Test
    public void testAddAcctInfo() throws Exception {
        int numAccts = 8;
        for (int i = 0; i < numAccts; ++i) {
            AccountInfo acct = createAccountInfo(new Long(i));
            StoreReporterInfo instance = new StoreReporterInfo(subdomain(new Long(i)));
            report.addAcctInfo(acct, instance);
        }

        // verify errors
        Assert.assertFalse(report.hasErrors());

        Map<AccountInfo, StoreReporterInfo> errors = report.getReporterErrors();
        Assert.assertNotNull(errors);
        Assert.assertEquals(0, errors.size());

        // verify successes
        Map<AccountInfo, StoreReporterInfo> infos = report.getReporterInfos();
        Assert.assertNotNull(infos);
        Assert.assertEquals(numAccts, infos.size());

        for (AccountInfo acct : infos.keySet()) {
            StoreReporterInfo info = infos.get(acct);
            Assert.assertNotNull(info);

            Assert.assertFalse(info.hasErrors());

            String text = info.toString();
            Assert.assertTrue(text.contains(acct.getSubdomain()));
            Assert.assertTrue(text.contains("OK"));
        }
    }

    private AccountInfo createAccountInfo(Long id) {
        return new AccountInfo(id,
                               subdomain(id),
                               "acctName-" + id,
                               null,
                               null,
                               -1L,
                               -1L,
                               -1L,
                               null,
                               null);
    }

    private String subdomain(Long id) {
        return "subdomain-" + id;
    }

}
