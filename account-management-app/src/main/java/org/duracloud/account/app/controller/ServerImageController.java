/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.db.model.ServerImage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Daniel Bernstein
 *         Date: Feb 17, 2012
 */
@Controller
@RequestMapping(ServerImageController.BASE_MAPPING)
public class ServerImageController extends AbstractRootCrudController<ServerImageForm>{

    private static final String RELATIVE_MAPPING = "/serverimages";
    public static final String BASE_MAPPING =
        RootConsoleHomeController.BASE_MAPPING + RELATIVE_MAPPING;

    public ServerImageController() {
        super(ServerImageForm.class);
    }
   
    @Override
    protected String getBaseViewId() {
        return super.getBaseView() + RELATIVE_MAPPING;
    }
    
    @ModelAttribute("providerAccountIds")
    public List<String> getProviderAccounts() {
        List<String> list = new ArrayList<String>();
        list.add("1");
        return list;
    }

    @Override
    public ModelAndView get() {
        ModelAndView mav = new ModelAndView(getBaseViewId());
        mav.addObject("serverImages",
                      getRootAccountManagerService().listAllServerImages(null));
        return mav;
    }

    @Override
    protected void create(ServerImageForm form) {
        getRootAccountManagerService().createServerImage(form.getProviderAccountId(),
                                                         form.getProviderImageId(),
                                                         form.getVersion(),
                                                         form.getDescription(),
                                                         form.getPassword(),
                                                         form.isLatest());
        
    }

    @Override
    protected Object getEntity(Long id) {
        return getRootAccountManagerService().getServerImage(id);
    }

    @Override
    protected ServerImageForm loadForm(Object obj) {
        ServerImage entity = (ServerImage)obj;
        ServerImageForm form = form();
        form.setProviderAccountId(entity.getProviderAccount().getId());
        form.setProviderImageId(entity.getProviderImageId());
        form.setVersion(entity.getVersion());
        form.setDescription(entity.getDescription());
        form.setPassword(entity.getDcRootPassword());
        form.setLatest(entity.isLatest());
        return form;
    }

    @Override
    protected void update(Long id, ServerImageForm form) {
        getRootAccountManagerService().editServerImage(id,
                                                       form.getProviderAccountId(),
                                                       form.getProviderImageId(),
                                                       form.getVersion(),
                                                       form.getDescription(),
                                                       form.getPassword(),
                                                       form.isLatest());        
    }

    @Override
    protected void delete(Long id) {
        getRootAccountManagerService().deleteServerImage(id);
    }

}
