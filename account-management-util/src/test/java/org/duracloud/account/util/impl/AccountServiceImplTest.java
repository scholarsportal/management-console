/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.util.impl;

import java.util.HashSet;
import java.util.Set;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.AccountRights;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.storage.domain.StorageProviderType;
import org.easymock.classextension.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Andrew Woods Date: Dec 10, 2010
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
        acctService = new AccountServiceImpl(acctInfo, repoMgr);

    }

    @Test
    public void testGetSetStorageProviders() throws Exception {
        setUpGetSetStorageProviders();

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
        Set<AccountRights> rights = new HashSet<AccountRights>();
        Set<Role> roles = new HashSet<Role>();
        roles.add(Role.ROLE_OWNER);
        roles.add(Role.ROLE_ADMIN);
        roles.add(Role.ROLE_USER);
        int userId = 1;
        rights.add(new AccountRights(1, acctId, userId, roles));
        EasyMock.expect(this.rightsRepo.findByAccountId(acctId)).andReturn(
            rights);
        EasyMock.expect(this.userRepo.findById(userId)).andReturn(
            newDuracloudUser(userId, "test"));
        EasyMock.expectLastCall();
        replayMocks();
        Set<DuracloudUser> users = this.acctService.getUsers();
        Assert.assertNotNull(users);
        Assert.assertTrue(users.size() == 1);
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
