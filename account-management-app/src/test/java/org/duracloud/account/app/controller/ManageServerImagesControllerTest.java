/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.util.RootAccountManagerService;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class ManageServerImagesControllerTest extends AmaControllerTestBase {
    private ManageServerImagesController manageServerImagesController;
    private RootAccountManagerService rootAccountManagerService;

    @Before
    public void before() throws Exception {
        super.before();
        rootAccountManagerService = EasyMock.createMock("RootAccountManagerService",
                                                        RootAccountManagerService.class);
        mocks.add(rootAccountManagerService);
        manageServerImagesController = new ManageServerImagesController();
        manageServerImagesController.setRootAccountManagerService(rootAccountManagerService);

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

        EasyMock.expect(result.hasErrors()).andReturn(false);

        rootAccountManagerService.createServerImage(serverImageForm.getProviderAccountId(),
                          serverImageForm.getProviderImageId(),
                          serverImageForm.getVersion(),
                          serverImageForm.getDescription(),
                          serverImageForm.getPassword(),
                          serverImageForm.isLatest());
        EasyMock.expectLastCall();
        replayMocks();
        // method under test
        manageServerImagesController.add(serverImageForm, result, model);

    }

    @Test
    public void testDelete() throws Exception {
        rootAccountManagerService.deleteServerImage(1);
        EasyMock.expectLastCall();

        replayMocks();
        this.manageServerImagesController.delete(1, model);
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

        EasyMock.expect(result.hasErrors()).andReturn(false);

        rootAccountManagerService.editServerImage(imageId,
                          serverImageForm.getProviderAccountId(),
                          serverImageForm.getProviderImageId(),
                          serverImageForm.getVersion(),
                          serverImageForm.getDescription(),
                          serverImageForm.getPassword(),
                          serverImageForm.isLatest());
        EasyMock.expectLastCall();

        replayMocks();

        // method under test
        manageServerImagesController.edit(imageId,
                                          serverImageForm,
                                          result,
                                          model);

    }
}