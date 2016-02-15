/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import javax.validation.Valid;

import org.duracloud.account.db.model.GlobalProperties;
import org.duracloud.account.db.util.GlobalPropertiesConfigService;
import org.duracloud.account.util.UserFeedbackUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

/**
 * 
 * @author Daniel Bernstein 
 *         Date: 01/05/2015
 * 
 */
@Controller
@RequestMapping(GlobalPropertiesController.BASE_MAPPING)
public class GlobalPropertiesController {

    @Autowired
    private GlobalPropertiesConfigService globalPropertiesConfigService;

    public static final String BASE_MAPPING = "/root/globalproperties";

    public void setGlobalPropertiesConfigService(
            GlobalPropertiesConfigService globalPropertiesConfigService) {
        this.globalPropertiesConfigService = globalPropertiesConfigService;
    }
    /**
     * 
     * @return
     */
    @RequestMapping("")
    public ModelAndView get() {
        ModelAndView mav = new ModelAndView(BASE_MAPPING);
        return mav;
    }

    @ModelAttribute("globalProperties")
    public GlobalPropertiesForm form() {
        GlobalPropertiesForm form = new GlobalPropertiesForm();
        GlobalProperties entity = this.globalPropertiesConfigService.get();
        if(entity == null){
            return new GlobalPropertiesForm();
        } else {
            form.setInstanceNotificationTopicArn(entity.getInstanceNotificationTopicArn());
            form.setDuracloudRootUsername(entity.getDuracloudRootUsername());
            form.setDuracloudRootPassword(entity.getDuracloudRootPassword());
            return form;
        }
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public ModelAndView edit() {
        return new ModelAndView(BASE_MAPPING + "/edit");
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @Transactional
    public ModelAndView update(
            @ModelAttribute("GlobalProperties") @Valid GlobalPropertiesForm form,
            BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) {
        boolean hasErrors = bindingResult.hasErrors();
        if (hasErrors) {
            return new ModelAndView(BASE_MAPPING + "/edit");
        }
        
        this.globalPropertiesConfigService.set(
                form.getInstanceNotificationTopicArn(),
                form.getDuracloudRootUsername(),
                form.getDuracloudRootPassword());

        UserFeedbackUtil.addSuccessFlash("Successfully updated!", redirectAttributes);
        return new ModelAndView(new RedirectView(BASE_MAPPING, true));
    }

}
