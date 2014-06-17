/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.db.model.AccountCluster;
import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.util.AccountClusterService;
import org.duracloud.account.db.util.RootAccountManagerService;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class AccountClusterControllerTest extends AmaControllerTestBase {
    private AccountClusterController accountClusterController;
    private RootAccountManagerService rootAccountManagerService;
    private AccountClusterService accountClusterService;
    @Before
    public void before() throws Exception {
        super.before();
        setupGenericAccountAndUserServiceMocks(TEST_ACCOUNT_ID);
        rootAccountManagerService = createMock(RootAccountManagerService.class);
        accountClusterController = new AccountClusterController();
        accountClusterService = createMock(AccountClusterService.class);

        accountClusterController.setRootAccountManagerService(rootAccountManagerService);
        accountClusterController.setAccountManagerService(accountManagerService);
    }

    @Test
    public void testAdd() throws Exception {
        // set up mocks, and args
        AccountClusterForm form = new AccountClusterForm();
        form.setName("Test Cluster");

        EasyMock.expect(result.hasErrors()).andReturn(false);
        EasyMock.expect(accountManagerService.createAccountCluster(EasyMock.isA(String.class))).andReturn(accountClusterService);
        EasyMock.expectLastCall();
        addFlashAttribute();

        replayMocks();
        // method under test
        accountClusterController.create(form, result, model, redirectAttributes);

    }

    @Test
    public void testDelete() throws Exception {
        this.rootAccountManagerService.deleteAccountCluster(1L);
        EasyMock.expectLastCall();
        addFlashAttribute();

        replayMocks();
        this.accountClusterController.delete(1L, redirectAttributes);
    }

    @Test
    public void testEdit() throws Exception {
        Long clusterId = 1L;
        AccountClusterForm form = new AccountClusterForm();
        form.setName("cluster");
        EasyMock.expect(result.hasErrors()).andReturn(false);
        
        EasyMock.expect(accountManagerService.getAccountCluster(clusterId)).andReturn(accountClusterService);
        accountClusterService.renameAccountCluster(EasyMock.isA(String.class));
        EasyMock.expectLastCall();
        addFlashAttribute();

        replayMocks();

        // method under test
        accountClusterController.update(clusterId,
                                          form,
                                          result,
                                          model,
                                          redirectAttributes);

    }
    
    @Test
    public void testClusterDetail() throws Exception{
        Long clusterId = 1L;
        setGetClusterAndLoadedAccounts(clusterId);
        replayMocks();

        // method under test
        accountClusterController.details(clusterId);
        
    }

    
    private void setGetClusterAndLoadedAccounts(Long clusterId) throws Exception{
        AccountInfo accountInfo = createAccountInfo(TEST_ACCOUNT_ID);
        AccountCluster cluster = new AccountCluster();
        cluster.setId(0L);
        cluster.setClusterName("test");
        cluster.setClusterAccounts(new HashSet<AccountInfo>());
        cluster.getClusterAccounts().add(accountInfo);

        EasyMock.expect(accountManagerService.getAccountCluster(clusterId))
                .andReturn(accountClusterService);
        EasyMock.expect(accountClusterService.retrieveAccountCluster())
                .andReturn(cluster);
    }

    @Test
    public void testGetAddAccounts() throws Exception{
        Long clusterId = 1L;
        setGetClusterAndLoadedAccounts(clusterId);
        this.rootAccountManagerService.listAllAccounts(EasyMock.anyObject(String.class));
        Set<AccountInfo> set = new HashSet<AccountInfo>();
        set.add(createAccountInfo());
        EasyMock.expectLastCall().andReturn(set);
        replayMocks();
        // method under test
        accountClusterController.getAddAccounts(clusterId);
    }

    @Test
    public void testPostAddAccounts() throws Exception{
        Long clusterId = 1L;
        AccountSelectionForm form = new AccountSelectionForm();
        form.setAccountIds(new Long[]{TEST_ACCOUNT_ID,TEST_ACCOUNT_ID+1});

        EasyMock.expect(accountManagerService.getAccountCluster(clusterId))
                .andReturn(accountClusterService);
        
        
        this.accountClusterService.addAccountToCluster(EasyMock.anyLong());
        EasyMock.expectLastCall().times(2);
        addFlashAttribute();
        replayMocks();
        accountClusterController.postAddAccounts(clusterId, form, redirectAttributes);
    }

    @Test
    public void testRemoveAccounts() throws Exception{
        Long clusterId = 1L;
        AccountSelectionForm form = new AccountSelectionForm();
        form.setAccountIds(new Long[]{TEST_ACCOUNT_ID,TEST_ACCOUNT_ID+1});

        EasyMock.expect(accountManagerService.getAccountCluster(clusterId))
                .andReturn(accountClusterService);
        
        
        this.accountClusterService.removeAccountFromCluster(EasyMock.anyLong());
        EasyMock.expectLastCall().times(2);
        addFlashAttribute();
        replayMocks();
        accountClusterController.removeAccounts(clusterId, form, redirectAttributes);
        
    }

}