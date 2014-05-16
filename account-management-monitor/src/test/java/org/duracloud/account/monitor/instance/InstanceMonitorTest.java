/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.monitor.instance;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.db.DuracloudAccountRepo;
import org.duracloud.account.db.DuracloudInstanceRepo;
import org.duracloud.account.monitor.instance.domain.InstanceInfo;
import org.duracloud.account.monitor.instance.domain.InstanceReport;
import org.duracloud.account.monitor.instance.util.InstanceUtil;
import org.duracloud.account.monitor.instance.util.InstanceUtilFactory;
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
 *         Date: 7/15/11
 */
public class InstanceMonitorTest {

    private InstanceMonitor monitor;

    private DuracloudAccountRepo acctRepo;
    private DuracloudInstanceRepo instanceRepo;
    private InstanceUtilFactory factory;

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
        factory = EasyMock.createMock("InstanceUtilFactory",
                                      InstanceUtilFactory.class);

        monitor = new InstanceMonitor(acctRepo, instanceRepo, factory);
    }

    @After
    public void tearDown() throws Exception {
        EasyMock.verify(acctRepo, instanceRepo, factory);
    }

    private void replayMocks() {
        EasyMock.replay(acctRepo, instanceRepo, factory);
    }

    @Test
    public void testMonitorInstances() throws Exception {
        createMockExpectations(true);
        replayMocks();

        InstanceReport report = monitor.monitorInstances();
        Assert.assertNotNull(report);

        Map<AccountInfo, InstanceInfo> infos = report.getInstanceInfos();
        Assert.assertNotNull(infos);
        Assert.assertEquals(NUM_ACCTS, infos.size());

        Assert.assertFalse(report.hasErrors());
    }

    @Test
    public void testMonitorInstancesError() throws Exception {
        createMockExpectations(false);
        replayMocks();

        InstanceReport report = monitor.monitorInstances();
        Assert.assertNotNull(report);

        Map<AccountInfo, InstanceInfo> infos = report.getInstanceInfos();
        Assert.assertNotNull(infos);
        Assert.assertEquals(NUM_ACCTS, infos.size());

        Assert.assertTrue(report.hasErrors());
        Map<AccountInfo, InstanceInfo> errors = report.getInstanceErrors();
        Assert.assertNotNull(errors);
        Assert.assertEquals(1, errors.size());
    }

    private void createMockExpectations(boolean valid) throws Exception {
        Set<Long> ids = new HashSet<Long>();
        for (AccountInfo acct : accts) {
            Long id = acct.getId();
            ids.add(id);

            EasyMock.expect(instanceRepo.findByAccountId(id)).andReturn(ids);

            EasyMock.expect(acctRepo.findById(id)).andReturn(acct);

            InstanceUtil util = createInstanceUtil(valid, id);
            EasyMock.expect(factory.getInstanceUtil(acct)).andReturn(util);
        }
        EasyMock.expect(acctRepo.getIds()).andReturn(ids);
    }

    private InstanceUtil createInstanceUtil(boolean valid, Long id) {
        InstanceUtil util = EasyMock.createMock("InstanceUtil",
                                                InstanceUtil.class);
        if (valid || id != 0) {
            EasyMock.expect(util.pingWebApps()).andReturn(createInstanceInfo(
                valid));

        } else {
            EasyMock.expect(util.pingWebApps()).andThrow(new RuntimeException(
                "canned-exeption"));
        }
        EasyMock.replay(util);

        return util;
    }

    private InstanceInfo createInstanceInfo(boolean valid) {
        InstanceInfo info = EasyMock.createMock("InstanceInfo",
                                                InstanceInfo.class);

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
