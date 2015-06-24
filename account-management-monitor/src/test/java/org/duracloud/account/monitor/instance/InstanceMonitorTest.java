/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.monitor.instance;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.DuracloudInstance;
import org.duracloud.account.db.repo.DuracloudAccountRepo;
import org.duracloud.account.db.repo.DuracloudInstanceRepo;
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
import java.util.List;
import java.util.Map;

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
        List<DuracloudInstance> instances = new ArrayList<>();
        for (AccountInfo acct : accts) {
            Long id = acct.getId();

            DuracloudInstance instance = new DuracloudInstance();
            instance.setId(id);
            instance.setAccount(acct);
            instances.add(instance);
            InstanceUtil util = createInstanceUtil(valid, id);
            EasyMock.expect(factory.getInstanceUtil(acct)).andReturn(util);
        }
        EasyMock.expect(instanceRepo.findAll()).andReturn(instances);
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
        AccountInfo account = new AccountInfo();
        account.setId(id);
        account.setSubdomain("subdomain-" + id);
        account.setAcctName("acctName-" + id);
        return account;
    }

}
