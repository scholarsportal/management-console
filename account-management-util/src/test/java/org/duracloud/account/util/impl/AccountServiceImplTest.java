/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.db.DuracloudRepoMgr;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.storage.domain.StorageProviderType;
import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Andrew Woods
 *         Date: Dec 10, 2010
 */
public class AccountServiceImplTest extends DuracloudServiceTestBase {

    private AccountServiceImpl acctService;
    private AccountInfo acctInfo;

    private final int acctId = 1;
    private final String subdomain = "sub-domain";
    private Set<StorageProviderType> storageProviders;

    @Before
    public void before() throws Exception {
        super.before();

        storageProviders = new HashSet<StorageProviderType>();
        for (StorageProviderType provider : StorageProviderType.values()) {
            storageProviders.add(provider);
        }

        Set<StorageProviderType> emptySet = new HashSet<StorageProviderType>();
        acctInfo = new AccountInfo(acctId, subdomain, emptySet);
    }

    @Test
    public void testGetSetStorageProviders() throws Exception {
        setUpGetSetStorageProviders();

        acctService = new AccountServiceImpl(acctInfo, repoMgr);

        Set<StorageProviderType> providers = acctService.getStorageProviders();
        Assert.assertEquals(0, providers.size());

        acctService.setStorageProviders(storageProviders);
        providers = acctService.getStorageProviders();
        Assert.assertNotNull(providers);

        Assert.assertTrue(storageProviders.size() > 0);
        Assert.assertEquals(storageProviders.size(), providers.size());
        for (StorageProviderType provider : providers) {
            Assert.assertTrue(storageProviders.contains(provider));
        }
    }

    private void setUpGetSetStorageProviders() throws Exception {
        accountRepo.save(acctInfo);
        EasyMock.expectLastCall();

        replayMocks();
    }

    @Test
    public void testGetUsers() throws Exception {
        // FIXME: implement
        replayMocks();
    }

    @Test
    public void testStoreRetrieveAccountInfo() throws Exception {
        // FIXME: implement
        replayMocks();
    }

    @Test
    public void testStoreRetrievePaymentInfo() throws Exception {
        // FIXME: implement
        replayMocks();
    }

    @Test
    public void testStoreSubdomain() throws Exception {
        // FIXME: implement
        replayMocks();
    }
}
