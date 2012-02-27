/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.duracloud.account.common.domain.ServerImage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

/**
 * 
 * @author Daniel Bernstein
 *         Date: Feb 17, 2012
 */
@Controller
@RequestMapping(ServerImageController.BASE_MAPPING)
public class ServerImageController extends AbstractRootController{
    public static final String BASE_MAPPING =
        RootConsoleHomeController.BASE_MAPPING + "/serverimages";
    public static final String NEW_MAPPING = AbstractController.NEW_MAPPING;
    private static final String BASE_VIEW = "/root/serverimages";
    private static final String NEW_VIEW = BASE_VIEW + "/new";
    private static final String EDIT_VIEW = BASE_VIEW + "/edit";
   
    private String getBaseViewId() {
        return BASE_VIEW;
    }


    private String getNewViewId() {
        return NEW_VIEW;
    }

    private String getEditViewId() {
        return EDIT_VIEW;
    }


    
    @RequestMapping(NEW_MAPPING)
    public ModelAndView getNew() {
        return new ModelAndView(getNewViewId());
    }

    @ModelAttribute("serverImageForm")
    public ServerImageForm serverImageForm() {
        return new ServerImageForm();
    }

    @ModelAttribute("providerAccountIds")
    public List<String> getProviderAccounts() {
        List<String> list = new ArrayList<String>();
        list.add("0");
        return list;
    }

    @RequestMapping("")
    public ModelAndView get() {
        ModelAndView mav = new ModelAndView(getBaseViewId());
        mav.addObject("serverImages",
                      getRootAccountManagerService().listAllServerImages(null));
        return mav;
    }
    
    @RequestMapping(value = NEW_MAPPING, method = RequestMethod.POST)
    public ModelAndView
        create(@ModelAttribute("serverImageForm") @Valid ServerImageForm sif,
                          BindingResult bindingResult, Model model,RedirectAttributes redirectAttributes) {
        boolean hasErrors = bindingResult.hasErrors();

        if(hasErrors){
            return new ModelAndView(getNewViewId());
        }

        getRootAccountManagerService().createServerImage(sif.getProviderAccountId(),
                                                       sif.getProviderImageId(),
                                                       sif.getVersion(),
                                                       sif.getDescription(),
                                                       sif.getPassword(),
                                                       sif.isLatest());
        log.info("created server image: {}" + sif);
        setSuccessFeedback("Successfully created server image.",
                        redirectAttributes);
        return new ModelAndView(new RedirectView(getBaseViewId(), true));
    }
    
    @RequestMapping(value = BY_ID_EDIT_MAPPING, method = RequestMethod.GET)
    public ModelAndView edit(@PathVariable int id){
        ServerImage entity = getRootAccountManagerService().getServerImage(id);
        log.debug("retrieved: id={}, entity={}", id, entity);
        ServerImageForm form = new ServerImageForm();
        loadForm(form,entity);
        return new ModelAndView(getEditViewId(), "serverImageForm", form);
    }
    
    @RequestMapping(value = BY_ID_EDIT_MAPPING, method = RequestMethod.POST)
    public ModelAndView
        update(@PathVariable int id, @ModelAttribute("serverImageForm") @Valid ServerImageForm sif,
                          BindingResult bindingResult, Model model,RedirectAttributes redirectAttributes) {
        boolean hasErrors = bindingResult.hasErrors();
        if(hasErrors){
            return new ModelAndView(getEditViewId());
        }
        
        getRootAccountManagerService().editServerImage(id,
                                                       sif.getProviderAccountId(),
                                                       sif.getProviderImageId(),
                                                       sif.getVersion(),
                                                       sif.getDescription(),
                                                       sif.getPassword(),
                                                       sif.isLatest());
        
        setSuccessFeedback("Successfully updated server image.",
                        redirectAttributes);
        log.info("updated server image: id={} -> form=", id, sif);
        return new ModelAndView(new RedirectView(getBaseViewId(), true));
    }

    @RequestMapping(value = BY_ID_DELETE_MAPPING, method = RequestMethod.POST)
    public ModelAndView
        delete(@PathVariable int id, RedirectAttributes redirectAttributes) {
        getRootAccountManagerService().deleteServerImage(id);
        setSuccessFeedback("Successfully deleted server image.",
                        redirectAttributes);
        log.info("deleted server image: id={}", id);
        
        return new ModelAndView(new RedirectView(getBaseViewId(), true));
    }
    
    private void loadForm(ServerImageForm form, ServerImage entity) {
        form.setProviderAccountId(entity.getProviderAccountId());
        form.setProviderImageId(entity.getProviderImageId());
        form.setVersion(entity.getVersion());
        form.setDescription(entity.getDescription());
        form.setPassword(entity.getDcRootPassword());
        form.setLatest(entity.isLatest());
    }

}
