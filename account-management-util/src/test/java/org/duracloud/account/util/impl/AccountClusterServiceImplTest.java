/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountCluster;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author: Bill Branan
 * Date: 2/22/12
 */
public class AccountClusterServiceImplTest extends DuracloudServiceTestBase {

    private int clusterId = 55;
    private String clusterName = "cluster55";
    private AccountCluster cluster;
    private AccountClusterServiceImpl clusterService;

    @Before
    @Override
    public void before() throws Exception {
        super.before();

        cluster = new AccountCluster(clusterId,
                                     clusterName,
                                     new HashSet<Integer>());
        clusterService =
            new AccountClusterServiceImpl(cluster, repoMgr, clusterUtil);
    }

    @Test
    public void testRetrieveAccountCluster() {
        replayMocks();

        AccountCluster retCluster =
            clusterService.retrieveAccountCluster();
        assertEquals(cluster, retCluster);
    }

    @Test
    public void testRenameAccountCluster() throws Exception {
        Capture<AccountCluster> capturedCluster = new Capture<AccountCluster>();
        accountClusterRepo.save(EasyMock.capture(capturedCluster));
        EasyMock.expectLastCall();

        replayMocks();

        String newName = "clusterific";
        clusterService.renameAccountCluster(newName);
        AccountCluster newCluster = capturedCluster.getValue();
        assertNotNull(newCluster);
        assertEquals(newName, newCluster.getClusterName());
    }

    @Test
    public void testAddAccountToCluster() {
        int accountId = 90;
        clusterUtil.setAccountCluster(accountId, clusterId);
        EasyMock.expectLastCall();
        clusterUtil.addAccountToCluster(accountId, clusterId);
        EasyMock.expectLastCall();

        replayMocks();

        clusterService.addAccountToCluster(accountId);
    }

    @Test
    public void testRemoveAccountFromCluster() {
        int accountId = 92;
        clusterUtil.setAccountCluster(accountId, -1);
        EasyMock.expectLastCall();
        clusterUtil.removeAccountFromCluster(accountId, clusterId);
        EasyMock.expectLastCall();

        replayMocks();

        clusterService.removeAccountFromCluster(accountId);
    }

}
