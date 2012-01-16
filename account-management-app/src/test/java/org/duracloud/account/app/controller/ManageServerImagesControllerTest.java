/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.util.RootAccountManagerService;
import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

public class ManageServerImagesControllerTest extends AmaControllerTestBase {
    private ManageServerImagesController manageServerImagesController;
    private RootAccountManagerService rootAccountManagerService;

    @Before
    public void before() throws Exception {
        super.before();

        manageServerImagesController = new ManageServerImagesController();
        rootAccountManagerService = EasyMock.createMock("RootAccountManagerService",
                                                        RootAccountManagerService.class);
    }

    @Test
    public void testAdd() throws Exception {
        // set up mocks, and args
        ServerImageForm serverImageForm = new ServerImageForm();
        serverImageForm.setProviderAccountId(1);
        serverImageForm.setProviderImageId("id");
        serverImageForm.setDescription("desc");
        serverImageForm.setVersion("version");
        serverImageForm.setPassword("password");
        serverImageForm.setLatest(true);

        BindingResult bindingResult = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(bindingResult.hasErrors()).andReturn(false);
        EasyMock.replay(bindingResult);
        Model model = new ExtendedModelMap();

        rootAccountManagerService.createServerImage(serverImageForm.getProviderAccountId(),
                          serverImageForm.getProviderImageId(),
                          serverImageForm.getVersion(),
                          serverImageForm.getDescription(),
                          serverImageForm.getPassword(),
                          serverImageForm.isLatest());
        EasyMock.expectLastCall();


        this.manageServerImagesController.setRootAccountManagerService(
            rootAccountManagerService);
        EasyMock.replay(rootAccountManagerService);

        // method under test
        manageServerImagesController.add(serverImageForm, bindingResult, model);

        EasyMock.verify(rootAccountManagerService);
    }

    @Test
    public void testDelete() throws Exception {
        rootAccountManagerService.deleteServerImage(1);
        EasyMock.expectLastCall();

        this.manageServerImagesController.setRootAccountManagerService(
            rootAccountManagerService);
        EasyMock.replay(rootAccountManagerService);

        Model model = new ExtendedModelMap();
        this.manageServerImagesController.delete(1, model);
        EasyMock.verify(rootAccountManagerService);
    }

    @Test
    public void testEdit() throws Exception {
        // set up mocks, and args
        int imageId = 7;

        ServerImageForm serverImageForm = new ServerImageForm();
        serverImageForm.setProviderAccountId(1);
        serverImageForm.setProviderImageId("id");
        serverImageForm.setDescription("desc");
        serverImageForm.setVersion("version");
        serverImageForm.setPassword("password");
        serverImageForm.setLatest(true);

        BindingResult bindingResult = EasyMock.createMock(BindingResult.class);
        EasyMock.expect(bindingResult.hasErrors()).andReturn(false);
        EasyMock.replay(bindingResult);
        Model model = new ExtendedModelMap();

        rootAccountManagerService.editServerImage(imageId,
                          serverImageForm.getProviderAccountId(),
                          serverImageForm.getProviderImageId(),
                          serverImageForm.getVersion(),
                          serverImageForm.getDescription(),
                          serverImageForm.getPassword(),
                          serverImageForm.isLatest());
        EasyMock.expectLastCall();


        this.manageServerImagesController.setRootAccountManagerService(
            rootAccountManagerService);
        EasyMock.replay(rootAccountManagerService);

        // method under test
        manageServerImagesController.edit(imageId,
                                          serverImageForm,
                                          bindingResult,
                                          model);

        EasyMock.verify(rootAccountManagerService);
    }
}