/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import java.util.ArrayList;
import java.util.HashMap;

import org.duracloud.account.app.controller.AccountSetupForm.StorageProviderSettings;
import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.model.AccountInfo.AccountStatus;
import org.duracloud.account.db.model.ComputeProviderAccount;
import org.duracloud.account.db.model.StorageProviderAccount;
import org.duracloud.account.db.util.RootAccountManagerService;
import org.duracloud.computeprovider.domain.ComputeProviderType;
import org.duracloud.storage.domain.StorageProviderType;
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
        rootAccountManagerService = createMock(RootAccountManagerService.class);
        accountsController.setRootAccountManagerService(rootAccountManagerService);
        accountsController.setAccountManagerService(accountManagerService);        
        
        setupGenericAccountAndUserServiceMocks(TEST_ACCOUNT_ID);
    }

    @Test
    public void testDeleteAccount() throws Exception {
        rootAccountManagerService.deleteAccount(1L);
        EasyMock.expectLastCall();
        addFlashAttribute();
        replayMocks();
        this.accountsController.delete(1L, redirectAttributes);
    }

    @Test
    public void testGetSetupAccount() throws Exception {
        AccountInfo info = createMock(AccountInfo.class);
        EasyMock.expect(info.getStatus()).andReturn(AccountStatus.PENDING);
        EasyMock.expect(rootAccountManagerService.getAccount(TEST_ACCOUNT_ID)).andReturn(info);
        
        StorageProviderAccount spa = new StorageProviderAccount();
        spa.setId(0L);
        spa.setProviderType(StorageProviderType.AMAZON_S3);
        spa.setUsername("username");
        spa.setPassword("password");
        spa.setRrs(true);
        
        EasyMock.expect(accountService.getPrimaryStorageProvider())
                .andReturn(spa);

        ComputeProviderAccount cpa = new ComputeProviderAccount();
        cpa.setProviderType(ComputeProviderType.AMAZON_EC2);
        cpa.setUsername("username");
        cpa.setPassword("password");
        cpa.setElasticIp("ip");
        cpa.setSecurityGroup("security");
        cpa.setKeypair("keypair");

        EasyMock.expect(accountService.getComputeProvider()).andReturn(cpa);
        
        EasyMock.expect(rootAccountManagerService.getSecondaryStorageProviders(TEST_ACCOUNT_ID))
            .andReturn(new ArrayList<StorageProviderAccount>());
        replayMocks();
        this.accountsController.getSetupAccount(TEST_ACCOUNT_ID, new ExtendedModelMap());
    }

    @Test
    public void testSetupAccount() throws Exception {
        AccountInfo info = createMock(AccountInfo.class);
        EasyMock.expect(info.getStatus()).andReturn(AccountStatus.PENDING);
        EasyMock.expect(rootAccountManagerService.getAccount(TEST_ACCOUNT_ID)).andReturn(info);
        EasyMock.expect(info.getAcctName()).andReturn("test");
        
        rootAccountManagerService.setupStorageProvider(EasyMock.anyLong(),
                                                       EasyMock.isA(String.class),
                                                       EasyMock.isA(String.class),
                                                       EasyMock.isA(new HashMap<String,String>().getClass()),
                                                       EasyMock.anyInt());
        EasyMock.expectLastCall().anyTimes();

        rootAccountManagerService.setupComputeProvider(EasyMock.anyLong(),
                                                       EasyMock.isA(String.class),
                                                       EasyMock.isA(String.class),
                                                       EasyMock.isA(String.class),
                                                       EasyMock.isA(String.class),
                                                       EasyMock.isA(String.class));
        EasyMock.expectLastCall();

        rootAccountManagerService.activateAccount(EasyMock.anyLong());
        EasyMock.expectLastCall();

        AccountSetupForm setupForm = new AccountSetupForm();
        String test = "test";
        StorageProviderSettings p = setupForm.getPrimaryStorageProviderSettings();
        p.setUsername(test);
        p.setPassword(test);
        setupForm.setComputeUsername(test);
        setupForm.setComputePassword(test);
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
