/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.util.ArrayList;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.StorageProviderAccount;
import org.duracloud.account.util.RootAccountManagerService;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;

/**
 * 
 * @author Daniel Bernstein
 *         Date: Mar 1, 2012
 *
 */
public class AccountsControllerTest extends AmaControllerTestBase {
    private AccountsController accountsController;
    private RootAccountManagerService rootAccountManagerService;

    @Before
    public void before() throws Exception {
        super.before();

        accountsController = new AccountsController();
        rootAccountManagerService = EasyMock.createMock("RootAccountManagerService",
                                                        RootAccountManagerService.class);
        mocks.add(rootAccountManagerService);
        accountsController.setRootAccountManagerService(rootAccountManagerService);
        accountsController.setAccountManagerService(accountManagerService);        
        
        setupGenericAccountAndUserServiceMocks(TEST_ACCOUNT_ID);
    }

    @Test
    public void testDeleteAccount() throws Exception {
        rootAccountManagerService.deleteAccount(1);
        EasyMock.expectLastCall();
        addFlashAttribute();
        replayMocks();
        this.accountsController.delete(1, redirectAttributes);
    }

    @Test
    public void testGetSetupAccount() throws Exception {
        EasyMock.expect(rootAccountManagerService.getSecondaryStorageProviders(TEST_ACCOUNT_ID))
            .andReturn(new ArrayList<StorageProviderAccount>());
        replayMocks();
        this.accountsController.getSetupAccount(TEST_ACCOUNT_ID, new ExtendedModelMap());
    }

    @Test
    public void testSetupAccount() throws Exception {

        rootAccountManagerService.setupStorageProvider(EasyMock.anyInt(),
                                                       EasyMock.isA(String.class),
                                                       EasyMock.isA(String.class));
        EasyMock.expectLastCall().anyTimes();

        rootAccountManagerService.setupComputeProvider(EasyMock.anyInt(),
                                                       EasyMock.isA(String.class),
                                                       EasyMock.isA(String.class),
                                                       EasyMock.isA(String.class),
                                                       EasyMock.isA(String.class),
                                                       EasyMock.isA(String.class));
        EasyMock.expectLastCall();

        rootAccountManagerService.activateAccount(EasyMock.anyInt());
        EasyMock.expectLastCall();

        AccountSetupForm setupForm = new AccountSetupForm();
        String test = "test";
        setupForm.setPrimaryStorageUsername(test);
        setupForm.setPrimaryStoragePassword(test);
        setupForm.setComputeCredentialsSame(true);
        setupForm.setComputeElasticIP(test);
        setupForm.setComputeKeypair(test);
        setupForm.setComputeSecurityGroup(test);
        setupNoBindingResultErrors();
        addFlashAttribute();
        replayMocks();
        
        this.accountsController.setupAccount(TEST_ACCOUNT_ID,
                                                setupForm,
                                                result,
                                                model,
                                                redirectAttributes);
    }


    @Test
    public void testActivate() throws Exception {
        accountService.storeAccountStatus(AccountInfo.AccountStatus.ACTIVE);
        EasyMock.expectLastCall();
        addFlashAttribute();
        replayMocks();
        accountsController.activate(TEST_ACCOUNT_ID, redirectAttributes);
    }

    @Test
    public void testDeactivate() throws Exception {

        accountService.storeAccountStatus(AccountInfo.AccountStatus.INACTIVE);
        EasyMock.expectLastCall();
        addFlashAttribute();
        replayMocks();
        accountsController.deactivate(TEST_ACCOUNT_ID, redirectAttributes);

    }

}
