/*
 * Copyright (c) 2009-2013 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.util.HashSet;
import java.util.Set;

import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.storage.domain.StorageProviderType;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

/**
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 * 
 */
public class AccountDetailsControllerTest extends AmaControllerTestBase {
    private AccountDetailsController controller;
    @Before
    public void before() throws Exception {
        super.before();
        setupGenericAccountAndUserServiceMocks(TEST_ACCOUNT_ID);
        controller = new AccountDetailsController();
        controller.setAccountManagerService(this.accountManagerService);
        controller.setUserService(this.userService);
    }
  
    
    @Test
    public void testRemoveProvider() throws Exception {
        Set<StorageProviderAccount> spa = new HashSet<StorageProviderAccount>();
        spa.add(new StorageProviderAccount(0,StorageProviderType.RACKSPACE, "test", "test", false));
        EasyMock.expect(this.accountService.getSecondaryStorageProviders()).andReturn(spa);
        this.accountService.removeStorageProvider(EasyMock.anyInt());
        addFlashAttribute();
        replayMocks();
        this.controller.removeProvider(TEST_ACCOUNT_ID, StorageProviderType.RACKSPACE.toString(), redirectAttributes);
    }
  }
