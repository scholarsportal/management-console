/*
 * Copyright (c) 2009-2011 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.common.domain.ServerImage;
import org.duracloud.account.util.RootAccountManagerService;
import org.springframework.validation.BindingResult;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@Controller
@Lazy
public class ManageServerImagesController  extends AbstractController {
    public static final String REDIRECT_USERS_MANAGE = "redirect:/users/manage";

    public static final String MANAGE_SERVER_IMAGES = "/manage/serverImage";

    public static final String DELETE_MAPPING =
        MANAGE_SERVER_IMAGES + "/byid/{imageId}/delete";
    public static final String EDIT_MAPPING =
        MANAGE_SERVER_IMAGES + "/byid/{imageId}/edit";

    public static final String NEW_FORM_KEY = "serverImageForm";
    public static final String NEW_VIEW = "serverImage-new";
    public static final String EDIT_VIEW = "serverImage-edit";

    @Autowired(required = true)
    protected RootAccountManagerService rootAccountManagerService;

    public void setRootAccountManagerService(
        RootAccountManagerService rootAccountManagerService) {
        this.rootAccountManagerService = rootAccountManagerService;
    }

    @RequestMapping(value = MANAGE_SERVER_IMAGES + NEW_MAPPING, method = RequestMethod.GET)
    public String openAddForm(Model model)
        throws Exception {
        log.info("serving up new ServerImageForm");

        model.addAttribute(NEW_FORM_KEY, new ServerImageForm());

        return NEW_VIEW;
    }

    @RequestMapping(value = { MANAGE_SERVER_IMAGES + NEW_MAPPING }, method = RequestMethod.POST)
    public String add(
        @ModelAttribute(NEW_FORM_KEY) @Valid ServerImageForm serverImageForm,
        BindingResult result, Model model) throws Exception {
        if (!result.hasErrors()) {
            //Add new Server Image
            rootAccountManagerService.createServerImage(serverImageForm.getProviderAccountId(),
                                                        serverImageForm.getProviderImageId(),
                                                        serverImageForm.getVersion(),
                                                        serverImageForm.getDescription(),
                                                        serverImageForm.getPassword(),
                                                        serverImageForm.isLatest());

            return REDIRECT_USERS_MANAGE;
        }

        return NEW_VIEW;
    }

    @ModelAttribute("providerAccountIds")
    public List<String> getProviderAccounts() {
        List<String> list = new ArrayList<String>();
        list.add("0");
        return list;
    }

    @RequestMapping(value = DELETE_MAPPING, method = RequestMethod.POST)
    public String delete(
        @PathVariable int imageId, Model model)
        throws Exception {
        log.info("delete server image {}", imageId);

        //delete server image
        rootAccountManagerService.deleteServerImage(imageId);

        return REDIRECT_USERS_MANAGE;
    }

    @RequestMapping(value = EDIT_MAPPING, method = RequestMethod.GET)
    public String getEditForm(@PathVariable int imageId, Model model)
        throws Exception {
        log.info("getEditForm image {}", imageId);
        ServerImageForm form = new ServerImageForm();

        ServerImage serverImage =
            rootAccountManagerService.getServerImage(imageId);

        //Fill form
        form.setProviderAccountId(serverImage.getProviderAccountId());
        form.setProviderImageId(serverImage.getProviderImageId());
        form.setVersion(serverImage.getVersion());
        form.setDescription(serverImage.getDescription());
        form.setPassword(serverImage.getDcRootPassword());
        form.setLatest(serverImage.isLatest());

        model.addAttribute("latest", serverImage.isLatest());
        model.addAttribute(NEW_FORM_KEY, form);

        return EDIT_VIEW;
    }

    @RequestMapping(value = EDIT_MAPPING, method = RequestMethod.POST)
    public String edit(@PathVariable int imageId,
                       @ModelAttribute(NEW_FORM_KEY) @Valid ServerImageForm serverImageForm,
					   BindingResult result,
					   Model model) throws Exception {
        log.debug("editRepo image {}", imageId);

        if (!result.hasErrors()) {
            //Update image info
            rootAccountManagerService.editServerImage(imageId,
                                                      serverImageForm.getProviderAccountId(),
                                                      serverImageForm.getProviderImageId(),
                                                      serverImageForm.getVersion(),
                                                      serverImageForm.getDescription(),
                                                      serverImageForm.getPassword(),
                                                      serverImageForm.isLatest());

            return REDIRECT_USERS_MANAGE;
        }

        return EDIT_VIEW;        
    }
}
