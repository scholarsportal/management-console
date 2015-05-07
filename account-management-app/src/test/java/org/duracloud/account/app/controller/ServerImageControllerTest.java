/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.db.util.RootAccountManagerService;
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
        ServerImageForm serverImageForm = createServerImageForm();
        EasyMock.expect(result.hasErrors()).andReturn(false);

        rootAccountManagerService.createServerImage(serverImageForm.getProviderImageId(),
                                                      serverImageForm.getVersion(),
                                                      serverImageForm.getDescription(),
                                                      serverImageForm.getPassword(),
                                                      serverImageForm.isLatest(),
                                                      serverImageForm.getIamRole());
        EasyMock.expectLastCall();
        replayMocks();
        // method under test
        serverImagesController.create(serverImageForm, result, model, redirectAttributes);

    }

    private ServerImageForm createServerImageForm() {
        ServerImageForm serverImageForm = new ServerImageForm();
        serverImageForm.setProviderImageId("id");
        serverImageForm.setDescription("desc");
        serverImageForm.setVersion("version");
        serverImageForm.setPassword("password");
        serverImageForm.setLatest(true);
        serverImageForm.setIamRole("iamRole");
        return serverImageForm;
    }

    @Test
    public void testDelete() throws Exception {
        rootAccountManagerService.deleteServerImage(1L);
        EasyMock.expectLastCall();

        replayMocks();
        this.serverImagesController.delete(1L, redirectAttributes);
    }

    @Test
    public void testEdit() throws Exception {
        // set up mocks, and args
        Long imageId = 7L;

        ServerImageForm serverImageForm = createServerImageForm();

        EasyMock.expect(result.hasErrors()).andReturn(false);

        rootAccountManagerService.editServerImage(imageId,
                          serverImageForm.getProviderImageId(),
                          serverImageForm.getVersion(),
                          serverImageForm.getDescription(),
                          serverImageForm.getPassword(),
                          serverImageForm.isLatest(),
                          serverImageForm.getIamRole());
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