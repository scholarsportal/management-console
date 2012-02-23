/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.common.domain.ServicePlan;
import org.duracloud.account.common.domain.ServiceRepository;
import org.duracloud.account.common.domain.ServiceRepository.ServiceRepositoryType;
import org.duracloud.account.util.RootAccountManagerService;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class ManageServiceReposControllerTest extends AmaControllerTestBase {
    private ManageServiceReposController manageServiceReposController;
    private RootAccountManagerService rootAccountManagerService;

    @Before
    public void before() throws Exception {
        super.before();
        rootAccountManagerService = EasyMock.createMock("RootAccountManagerService",
                                                        RootAccountManagerService.class);
        mocks.add(rootAccountManagerService);
        manageServiceReposController = new ManageServiceReposController();
        manageServiceReposController.setRootAccountManagerService(rootAccountManagerService);
    }

    @Test
    public void testAdd() throws Exception {
        // set up mocks, and args
        ServiceRepoForm serviceRepoForm = new ServiceRepoForm();
        serviceRepoForm.setServiceRepoType("PRIVATE");
        serviceRepoForm.setServicePlan(ServicePlan.PROFESSIONAL);
        serviceRepoForm.setHostName("hostname");
        serviceRepoForm.setVersion("version");
        serviceRepoForm.setSpaceId("spaceId");
        serviceRepoForm.setXmlId("xmlId");
        serviceRepoForm.setUserName("username");
        serviceRepoForm.setPassword("password");

        EasyMock.expect(result.hasErrors()).andReturn(false);
        rootAccountManagerService.createServiceRepository(EasyMock.isA(
            ServiceRepository.ServiceRepositoryType.class),
                                                          EasyMock.isA(
                                                              ServicePlan.class),
                                                          EasyMock.isA(String.class),
                                                          EasyMock.isA(String.class),
                                                          EasyMock.isA(String.class),
                                                          EasyMock.isA(String.class),
                                                          EasyMock.isA(String.class),
                                                          EasyMock.isA(String.class));
        EasyMock.expectLastCall();

        replayMocks();

        // method under test
        manageServiceReposController.add(serviceRepoForm, result, model);

        EasyMock.verify(rootAccountManagerService);
    }

    @Test
    public void testDelete() throws Exception {
        rootAccountManagerService.deleteServiceRepository(1);
        EasyMock.expectLastCall();
        replayMocks();
        this.manageServiceReposController.delete(1, model);
    }

    @Test
    public void testEdit() throws Exception {
        // set up mocks, and args
        int repoId = 7;

        ServiceRepoForm serviceRepoForm = new ServiceRepoForm();
        serviceRepoForm.setServiceRepoType("PRIVATE");
        serviceRepoForm.setServicePlan(ServicePlan.PROFESSIONAL);
        serviceRepoForm.setHostName("hostname");
        serviceRepoForm.setVersion("version");
        serviceRepoForm.setSpaceId("spaceId");
        serviceRepoForm.setXmlId("xmlId");
        serviceRepoForm.setUserName("username");
        serviceRepoForm.setPassword("password");

        EasyMock.expect(result.hasErrors()).andReturn(false);
        rootAccountManagerService.editServiceRepository(repoId,
                                                        ServiceRepositoryType.PRIVATE,
                                                        ServicePlan.PROFESSIONAL,
                                                        serviceRepoForm.getHostName(),
                                                        serviceRepoForm.getSpaceId(),
                                                        serviceRepoForm.getXmlId(),
                                                        serviceRepoForm.getVersion(),
                                                        serviceRepoForm.getUserName(),
                                                        serviceRepoForm.getPassword());
        EasyMock.expectLastCall();

        replayMocks();

        // method under test
        manageServiceReposController.edit(repoId,
                                          serviceRepoForm,
                                          result,
                                          model);

    }
}