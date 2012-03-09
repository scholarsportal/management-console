/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.duracloud.account.common.domain.AccountCluster;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.util.AccountClusterService;
import org.duracloud.account.util.RootAccountManagerService;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

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
        this.rootAccountManagerService.deleteAccountCluster(1);
        EasyMock.expectLastCall();
        addFlashAttribute();

        replayMocks();
        this.accountClusterController.delete(1, redirectAttributes);
    }

    @Test
    public void testEdit() throws Exception {
        int clusterId = 1;
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
        int clusterId = 1;
        setGetClusterAndLoadedAccounts(clusterId);
        replayMocks();

        // method under test
        accountClusterController.details(clusterId);
        
    }

    
    private void setGetClusterAndLoadedAccounts(int clusterId) throws Exception{
        List<Integer> ids = Arrays.asList(new Integer[]{TEST_ACCOUNT_ID});
        AccountCluster cluster = new AccountCluster(0, "test", new HashSet<Integer>(ids));
        EasyMock.expect(accountManagerService.getAccountCluster(clusterId))
                .andReturn(accountClusterService);
        EasyMock.expect(accountClusterService.retrieveAccountCluster())
                .andReturn(cluster);
    }

    @Test
    public void testGetAddAccounts() throws Exception{
        int clusterId = 1;
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
        int clusterId = 1;
        AccountSelectionForm form = new AccountSelectionForm();
        form.setAccountIds(new Integer[]{TEST_ACCOUNT_ID,TEST_ACCOUNT_ID+1});

        EasyMock.expect(accountManagerService.getAccountCluster(clusterId))
                .andReturn(accountClusterService);
        
        
        this.accountClusterService.addAccountToCluster(EasyMock.anyInt());
        EasyMock.expectLastCall().times(2);
        addFlashAttribute();
        replayMocks();
        accountClusterController.postAddAccounts(clusterId, form, redirectAttributes);
    }

    @Test
    public void testRemoveAccounts() throws Exception{
        int clusterId = 1;
        AccountSelectionForm form = new AccountSelectionForm();
        form.setAccountIds(new Integer[]{TEST_ACCOUNT_ID,TEST_ACCOUNT_ID+1});

        EasyMock.expect(accountManagerService.getAccountCluster(clusterId))
                .andReturn(accountClusterService);
        
        
        this.accountClusterService.removeAccountFromCluster(EasyMock.anyInt());
        EasyMock.expectLastCall().times(2);
        addFlashAttribute();
        replayMocks();
        accountClusterController.removeAccounts(clusterId, form, redirectAttributes);
        
    }

}