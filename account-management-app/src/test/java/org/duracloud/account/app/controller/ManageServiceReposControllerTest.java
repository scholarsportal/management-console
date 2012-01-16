/*
 * Copyright (c) 2009-2012 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.common.domain.ServicePlan;
import org.duracloud.account.common.domain.ServiceRepository;
import org.duracloud.account.common.domain.ServiceRepository.ServiceRepositoryType;
import org.duracloud.account.util.RootAccountManagerService;
import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

public class ManageServiceReposControllerTest extends AmaControllerTestBase {
    private ManageServiceReposController manageServiceReposController;
    private RootAccountManagerService rootAccountManagerService;

    @Before
    public void before() throws Exception {
        super.before();

        manageServiceReposController = new ManageServiceReposController();
        rootAccountManagerService = EasyMock.createMock("RootAccountManagerService",
                                                        RootAccountManagerService.class);
    }

    @Test
    public void testAdd() throws Exception {
        // set up mocks, and args
        ServiceRepoForm serviceRepoForm = new ServiceRepoForm();
        serviceRepoForm.setServiceRepoType("PRIVATE");
        serviceRepoForm.setServicePlan(ServicePlan.PROFESSIONAL.getText());
        serviceRepoForm.setHostName("hostname");
        serviceRepoForm.setVersion("version");
        serviceRepoForm.setSpaceId("spaceId");
        serviceRepoForm.setXmlId("xmlId");
        serviceRepoForm.setUserName("username");
        serviceRepoForm.setPassword("password");

        BindingResult bindingResult = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(bindingResult.hasErrors()).andReturn(false);
        EasyMock.replay(bindingResult);
        Model model = new ExtendedModelMap();

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


        this.manageServiceReposController.setRootAccountManagerService(
            rootAccountManagerService);
        EasyMock.replay(rootAccountManagerService);

        // method under test
        manageServiceReposController.add(serviceRepoForm, bindingResult, model);

        EasyMock.verify(rootAccountManagerService);
    }

    @Test
    public void testDelete() throws Exception {
        rootAccountManagerService.deleteServiceRepository(1);
        EasyMock.expectLastCall();

        this.manageServiceReposController.setRootAccountManagerService(
            rootAccountManagerService);
        EasyMock.replay(rootAccountManagerService);

        Model model = new ExtendedModelMap();
        this.manageServiceReposController.delete(1, model);
        EasyMock.verify(rootAccountManagerService);
    }

    @Test
    public void testEdit() throws Exception {
        // set up mocks, and args
        int repoId = 7;

        ServiceRepoForm serviceRepoForm = new ServiceRepoForm();
        serviceRepoForm.setServiceRepoType("PRIVATE");
        serviceRepoForm.setServicePlan(ServicePlan.PROFESSIONAL.getText());
        serviceRepoForm.setHostName("hostname");
        serviceRepoForm.setVersion("version");
        serviceRepoForm.setSpaceId("spaceId");
        serviceRepoForm.setXmlId("xmlId");
        serviceRepoForm.setUserName("username");
        serviceRepoForm.setPassword("password");

        BindingResult bindingResult = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(bindingResult.hasErrors()).andReturn(false);
        EasyMock.replay(bindingResult);
        Model model = new ExtendedModelMap();

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


        this.manageServiceReposController.setRootAccountManagerService(
            rootAccountManagerService);
        EasyMock.replay(rootAccountManagerService);

        // method under test
        manageServiceReposController.edit(repoId,
                                          serviceRepoForm,
                                          bindingResult,
                                          model);

        EasyMock.verify(rootAccountManagerService);
    }
}