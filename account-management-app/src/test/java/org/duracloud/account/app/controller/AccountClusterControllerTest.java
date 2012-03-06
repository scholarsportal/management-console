/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.util.AccountClusterService;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.RootAccountManagerService;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class AccountClusterControllerTest extends AmaControllerTestBase {
    private AccountClusterController accountClusterController;
    private RootAccountManagerService rootAccountManagerService;
    private AccountManagerService accountManagerService;
    private AccountClusterService accountClusterService;
    @Before
    public void before() throws Exception {
        super.before();
        rootAccountManagerService = createMock(RootAccountManagerService.class);
        accountManagerService = createMock(AccountManagerService.class);
        accountClusterController = new AccountClusterController();
        accountClusterController.setRootAccountManagerService(rootAccountManagerService);
        accountClusterController.setAccountManagerService(accountManagerService);

        accountClusterService = createMock(AccountClusterService.class);

        addFlashAttribute();
    }

    @Test
    public void testAdd() throws Exception {
        // set up mocks, and args
        AccountClusterForm form = new AccountClusterForm();
        form.setName("Test Cluster");

        EasyMock.expect(result.hasErrors()).andReturn(false);
        EasyMock.expect(accountManagerService.createAccountCluster(EasyMock.isA(String.class))).andReturn(accountClusterService);
        EasyMock.expectLastCall();
        replayMocks();
        // method under test
        accountClusterController.create(form, result, model, redirectAttributes);

    }

    @Test
    public void testDelete() throws Exception {
        this.rootAccountManagerService.deleteAccountCluster(1);
        EasyMock.expectLastCall();
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
        replayMocks();

        // method under test
        accountClusterController.update(clusterId,
                                          form,
                                          result,
                                          model,
                                          redirectAttributes);

    }
}