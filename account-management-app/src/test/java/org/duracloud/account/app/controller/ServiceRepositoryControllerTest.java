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

/**
 * 
 * @author Daniel Bernstein
 *
 */
public class ServiceRepositoryControllerTest extends AmaControllerTestBase {
    private ServiceRepositoryController serviceRepositoryController;
    private RootAccountManagerService rootAccountManagerService;

    @Before
    public void before() throws Exception {
        super.before();
        rootAccountManagerService = createMock(RootAccountManagerService.class);
        serviceRepositoryController = new ServiceRepositoryController();
        serviceRepositoryController.setRootAccountManagerService(rootAccountManagerService);
        addFlashAttribute();
    }

    @Test
    public void testAdd() throws Exception {
        // set up mocks, and args
        ServiceRepoForm serviceRepoForm = new ServiceRepoForm();
        serviceRepoForm.setServiceRepoType(ServiceRepositoryType.PRIVATE);
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
        serviceRepositoryController.create(serviceRepoForm, result, model, redirectAttributes);
        EasyMock.verify(rootAccountManagerService);
    }

    @Test
    public void testDelete() throws Exception {
        rootAccountManagerService.deleteServiceRepository(1);
        EasyMock.expectLastCall();
        replayMocks();
        this.serviceRepositoryController.delete(1, redirectAttributes);
    }

    @Test
    public void testEdit() throws Exception {
        // set up mocks, and args
        int repoId = 7;

        ServiceRepoForm serviceRepoForm = new ServiceRepoForm();
        serviceRepoForm.setServiceRepoType(ServiceRepositoryType.PRIVATE);
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
        serviceRepositoryController.update(repoId,
                                          serviceRepoForm,
                                          result,
                                          model,
                                          redirectAttributes);

    }
}