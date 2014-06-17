/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.storereporter;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudInstance;
import org.duracloud.account.common.domain.ServerImage;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.db.DuracloudServerImageRepo;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: 5/18/12
 */
public class StoreReporterMonitorTest {

    private StoreReporterMonitor monitor;

    private DuracloudAccountRepo acctRepo;
    private DuracloudInstanceRepo instanceRepo;
    private DuracloudServerImageRepo imageRepo;
    private StoreReporterUtilFactory factory;

    private static final Long IMAGE_ID = 345L;
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
        instanceRepo = EasyMock.createMock("DuracloudInstanceRepo",
                                           DuracloudInstanceRepo.class);
        imageRepo = EasyMock.createMock("DuracloudServerImageRepo",
                                        DuracloudServerImageRepo.class);
        factory = EasyMock.createMock("StoreReporterUtilFactory",
                                      StoreReporterUtilFactory.class);

        monitor = new StoreReporterMonitor(acctRepo,
                                           instanceRepo,
                                           imageRepo,
                                           factory);
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(acctRepo, instanceRepo, imageRepo, factory);
    }

    private void replayMocks() {
        EasyMock.replay(acctRepo, instanceRepo, imageRepo, factory);
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
        Set<Long> ids = new HashSet<Long>();
        Set<Long> instanceIds = new HashSet<Long>();
        Long instanceId = 7L;
        instanceIds.add(instanceId);
        Credential credential = new Credential(ServerImage.DC_ROOT_USERNAME,
                                               ROOT_PASS);

        for (AccountInfo acct : accts) {
            Long id = acct.getId();
            ids.add(id);

            EasyMock.expect(instanceRepo.findByAccountId(id))
                    .andReturn(new HashSet<Long>(instanceIds))
                    .times(2);

            DuracloudInstance instance = createMockDuracloudInstance(id);
            EasyMock.expect(instanceRepo.findById(instanceId)).andReturn(
                instance);

            ServerImage image = createMockServerImage();
            EasyMock.expect(imageRepo.findById(IMAGE_ID)).andReturn(image);

            EasyMock.expect(acctRepo.findById(id)).andReturn(acct);

            StoreReporterUtil util = createStoreReporterUtil(valid, id);
            EasyMock.expect(factory.getStoreReporterUtil(acct, credential))
                    .andReturn(util);
        }
        EasyMock.expect(acctRepo.getIds()).andReturn(ids);
    }

    private DuracloudInstance createMockDuracloudInstance(Long id) {
        Long accountId = 9L;
        String hostName = "hostname";
        String providerInstanceId = "8";
        boolean initialized = true;
        return new DuracloudInstance(id,
                                     IMAGE_ID,
                                     accountId,
                                     hostName,
                                     providerInstanceId,
                                     initialized);
    }

    private ServerImage createMockServerImage() {
        Long providerAccountId = 789L;
        String providerImageId = "providerImageId";
        String version = "version";
        String description = "description";
        boolean latest = true;
        return new ServerImage(IMAGE_ID,
                               providerAccountId,
                               providerImageId,
                               version,
                               description,
                               ROOT_PASS,
                               latest);
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
        return new AccountInfo(id,
                               "subdomain-" + id,
                               "acctName-" + id,
                               null,
                               null,
                               -1L,
                               -1L,
                               -1L,
                               null,
                               null);
    }

}
