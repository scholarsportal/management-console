/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.util.RootAccountManagerService;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class ServerImageControllerTest extends AmaControllerTestBase {
    private ServerImageController serverImagesController;
    private RootAccountManagerService rootAccountManagerService;

    @Before
    public void before() throws Exception {
        super.before();
        rootAccountManagerService = createMock(RootAccountManagerService.class);
        serverImagesController = new ServerImageController();
        serverImagesController.setRootAccountManagerService(rootAccountManagerService);
        addFlashAttribute();
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
        serverImagesController.create(serverImageForm, result, model, redirectAttributes);

    }

    @Test
    public void testDelete() throws Exception {
        rootAccountManagerService.deleteServerImage(1);
        EasyMock.expectLastCall();

        replayMocks();
        this.serverImagesController.delete(1, redirectAttributes);
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
        serverImagesController.update(imageId,
                                          serverImageForm,
                                          result,
                                          model,
                                          redirectAttributes);

    }
}