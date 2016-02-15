/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.storereporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.repo.DuracloudAccountRepo;
import org.duracloud.account.db.util.GlobalPropertiesConfigService;
import org.duracloud.account.monitor.storereporter.domain.StoreReporterInfo;
import org.duracloud.account.monitor.storereporter.domain.StoreReporterReport;
import org.duracloud.account.monitor.storereporter.util.StoreReporterUtil;
import org.duracloud.account.monitor.storereporter.util.StoreReporterUtilFactory;
import org.duracloud.common.model.Credential;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrew Woods
 *         Date: 5/18/12
 */
public class StoreReporterMonitorTest {

    private StoreReporterMonitor monitor;

    private DuracloudAccountRepo acctRepo;
    private GlobalPropertiesConfigService configService;
    private StoreReporterUtilFactory factory;

    private static final String ROOT_PASS = "root-pass";

    private List<AccountInfo> accts;
    private static final int NUM_ACCTS = 5;

    @Before
    public void setUp() throws Exception {
        accts = new ArrayList<AccountInfo>();
        for (int i = 0; i < NUM_ACCTS; ++i) {
            accts.add(createAccount(new Long(i)));
        }

        acctRepo = EasyMock.createMock("DuracloudAccountRepo",
                                       DuracloudAccountRepo.class);
        configService = EasyMock.createMock("GlobalPropertiesConfigService",
                                           GlobalPropertiesConfigService.class);
        factory = EasyMock.createMock("StoreReporterUtilFactory",
                                      StoreReporterUtilFactory.class);

        monitor = new StoreReporterMonitor(acctRepo,
                                           configService,
                                           factory);
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(acctRepo, configService, factory);
    }

    private void replayMocks() {
        EasyMock.replay(acctRepo, configService, factory);
    }

    @Test
    public void testMonitorInstances() throws Exception {
        createMockExpectations(true);
        replayMocks();

        StoreReporterReport report = monitor.monitorStoreReporters();
        Assert.assertNotNull(report);

        Map<AccountInfo, StoreReporterInfo> infos = report.getReporterInfos();
        Assert.assertNotNull(infos);
        Assert.assertEquals(NUM_ACCTS, infos.size());

        Assert.assertFalse(report.hasErrors());
    }

    @Test
    public void testMonitorInstancesError() throws Exception {
        createMockExpectations(false);
        replayMocks();

        StoreReporterReport report = monitor.monitorStoreReporters();
        Assert.assertNotNull(report);

        Map<AccountInfo, StoreReporterInfo> infos = report.getReporterInfos();
        Assert.assertNotNull(infos);
        Assert.assertEquals(NUM_ACCTS, infos.size());

        Assert.assertTrue(report.hasErrors());
        Map<AccountInfo, StoreReporterInfo> errors = report.getReporterErrors();
        Assert.assertNotNull(errors);
        Assert.assertEquals(1, errors.size());
    }

    private void createMockExpectations(boolean valid) throws Exception {
        Credential credential = new Credential("root",ROOT_PASS);

        List<AccountInfo> instances = new ArrayList<>();
        EasyMock.expect(acctRepo.findAll())
        .andReturn(instances)
        .times(1);
        
        for (AccountInfo acct : accts) {
            Long id = acct.getId();
            EasyMock.expect(acct.getStatus()).andReturn(AccountInfo.AccountStatus.ACTIVE);
            StoreReporterUtil util = createStoreReporterUtil(valid, id);
            EasyMock.expect(factory.getStoreReporterUtil(acct, credential))
                    .andReturn(util);
        }
    }



    private StoreReporterUtil createStoreReporterUtil(boolean valid, Long id) {
        StoreReporterUtil util = EasyMock.createMock("StoreReporterUtil",
                                                     StoreReporterUtil.class);
        if (valid || id != 0) {
            EasyMock.expect(util.pingStorageReporter()).andReturn(
                createStoreReporterInfo(valid));

        } else {
            EasyMock.expect(util.pingStorageReporter())
                    .andThrow(new RuntimeException("canned-exception"));
        }
        EasyMock.replay(util);

        return util;
    }

    private StoreReporterInfo createStoreReporterInfo(boolean valid) {
        StoreReporterInfo info = EasyMock.createMock("StoreReporterInfo",
                                                     StoreReporterInfo.class);

        int times = valid ? 1 : 2;
        EasyMock.expect(info.hasErrors()).andReturn(false).times(times);

        EasyMock.replay(info);
        return info;
    }

    private AccountInfo createAccount(Long id) {
        AccountInfo account = new AccountInfo();
        account.setId(id);
        account.setSubdomain("subdomain-" + id);
        account.setAcctName("acctName-" + id);
        return account;
    }

}
