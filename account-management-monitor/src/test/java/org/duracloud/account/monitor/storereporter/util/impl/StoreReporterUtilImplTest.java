/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.storereporter.util.impl;

import org.duracloud.account.monitor.storereporter.domain.StoreReporterInfo;
import org.duracloud.account.monitor.storereporter.util.StoreReporterUtil;
import org.duracloud.client.report.StorageReportManager;
import org.duracloud.client.report.error.ReportException;
import org.duracloud.common.model.Credential;
import org.duracloud.reportdata.storage.StorageReportInfo;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: 5/18/12
 */
public class StoreReporterUtilImplTest {

    private StoreReporterUtil util;
    private StorageReportManager reportManager;

    private String subdomain = "subdomain";
    private Credential credential = new Credential("user", "pass");
    private int thresholdDays = 3;

    @Before
    public void setUp() throws Exception {
        reportManager = EasyMock.createMock("StorageReportManager",
                                            StorageReportManager.class);

        util = new StoreReporterUtilImpl(subdomain,
                                         credential,
                                         thresholdDays,
                                         reportManager);
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(reportManager);
    }

    private void replayMocks() {
        EasyMock.replay(reportManager);
    }

    @Test
    public void testPingStorageReporter() throws Exception {
        doTestPingStorageReporter(MODE.SUCCESS);
    }

    @Test
    public void testPingStorageReporterException() throws Exception {
        doTestPingStorageReporter(MODE.EXCEPTION);
    }

    @Test
    public void testPingStorageReporterNull() throws Exception {
        doTestPingStorageReporter(MODE.NULL);
    }

    @Test
    public void testPingStorageReporterHung() throws Exception {
        doTestPingStorageReporter(MODE.HUNG);
    }

    private void doTestPingStorageReporter(MODE mode) throws Exception {
        createMockExpectations(mode);
        replayMocks();

        StoreReporterInfo info = util.pingStorageReporter();
        Assert.assertNotNull(info);
        Assert.assertEquals(!mode.valid, info.hasErrors());
        Assert.assertTrue(info.toString(), info.toString().contains(mode.text));
    }

    private void createMockExpectations(MODE mode) throws Exception {
        long now = System.currentTimeMillis();
        StorageReportInfo info = new StorageReportInfo();

        switch (mode) {
            case SUCCESS:
                info.setNextScheduledStartTime(now);
                EasyMock.expect(reportManager.getStorageReportInfo()).andReturn(
                    info);
                break;
            case EXCEPTION:
                EasyMock.expect(reportManager.getStorageReportInfo()).andThrow(
                    new ReportException(MODE.EXCEPTION.text));
                break;
            case NULL:
                EasyMock.expect(reportManager.getStorageReportInfo()).andReturn(
                    null);
                break;
            case HUNG:
                long threeDaysAgo = now - (3 * 25 * 3600 * 1000);
                info.setNextScheduledStartTime(threeDaysAgo);
                EasyMock.expect(reportManager.getStorageReportInfo()).andReturn(
                    info);
                break;
            default:
                Assert.fail("Unexpected mode: " + mode);
        }
    }

    private enum MODE {
        SUCCESS(true, "OK"),
        EXCEPTION(false, "canned-exception"),
        NULL(false, "ReportInfo: null"),
        HUNG(false, "3 days AGO");

        private boolean valid;
        private String text;

        private MODE(boolean valid, String text) {
            this.valid = valid;
            this.text = text;
        }
    }

}
