/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import javax.validation.Valid;

import org.duracloud.account.db.model.DuracloudMill;
import org.duracloud.account.db.util.DuracloudMillConfigService;
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
 * @author Daniel Bernstein
 * Date: 05/07/2015
 */
@Controller
@RequestMapping(DuracloudMillController.BASE_MAPPING)
public class DuracloudMillController {

    @Autowired
    private DuracloudMillConfigService duracloudMillConfigService;

    public static final String BASE_MAPPING = "/root/duracloudmill";

    public void setDuracloudMillConfigService(DuracloudMillConfigService duracloudMillConfigService) {
        this.duracloudMillConfigService = duracloudMillConfigService;
    }

    /**
     * @return
     */
    @RequestMapping("")
    public ModelAndView get() {
        ModelAndView mav = new ModelAndView(BASE_MAPPING);
        return mav;
    }

    @ModelAttribute("duracloudMill")
    public DuracloudMillForm form() {
        DuracloudMillForm form = new DuracloudMillForm();
        DuracloudMill entity = this.duracloudMillConfigService.get();

        if (entity == null) {
            return new DuracloudMillForm();
        } else {
            form.setDbHost(entity.getDbHost());
            form.setDbPort(entity.getDbPort());
            form.setDbName(entity.getDbName());
            form.setDbUsername(entity.getDbUsername());
            form.setDbPassword(entity.getDbPassword());
            form.setAuditQueue(entity.getAuditQueue());
            form.setS3Type(entity.getS3Type());
            form.setAuditLogSpaceId(entity.getAuditLogSpaceId());
            form.setAuditQueueType(entity.getAuditQueueType());
            form.setRabbitmqHost(entity.getRabbitmqHost());
            form.setRabbitmqPort(entity.getRabbitmqPort());
            form.setRabbitmqVhost(entity.getRabbitmqVhost());
            form.setRabbitmqExchange(entity.getRabbitmqExchange());
            form.setRabbitmqUsername(entity.getRabbitmqUsername());
            form.setRabbitmqPassword(entity.getRabbitmqPassword());
            form.setAwsAccessKey(entity.getAwsAccessKey());
            form.setAwsSecretKey(entity.getAwsSecretKey());
            form.setSwiftEndpoint(entity.getSwiftEndpoint());
            form.setSwiftSignerType(entity.getSwiftSignerType());
            return form;
        }
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public ModelAndView edit() {
        return new ModelAndView(BASE_MAPPING + "/edit");
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @Transactional
    public ModelAndView update(@ModelAttribute("duracloudMill") @Valid DuracloudMillForm form,
                               BindingResult bindingResult, Model model,
                               RedirectAttributes redirectAttributes) {
        boolean hasErrors = bindingResult.hasErrors();
        if (hasErrors) {
            return new ModelAndView(BASE_MAPPING + "/edit");
        }

        this.duracloudMillConfigService.set(form.getDbHost(),
                                            form.getDbPort(),
                                            form.getDbName(),
                                            form.getDbUsername(),
                                            form.getDbPassword(),
                                            form.getAuditQueue(),
                                            form.getS3Type(),
                                            form.getAuditLogSpaceId(),
                                            form.getAuditQueueType(),
                                            form.getRabbitmqHost(),
                                            form.getRabbitmqPort(),
                                            form.getRabbitmqVhost(),
                                            form.getRabbitmqExchange(),
                                            form.getRabbitmqUsername(),
                                            form.getRabbitmqPassword(),
                                            form.getAwsAccessKey(),
                                            form.getAwsSecretKey(),
                                            form.getSwiftEndpoint(),
                                            form.getSwiftSignerType());
        UserFeedbackUtil.addSuccessFlash("Successfully updated!", redirectAttributes);
        return new ModelAndView(new RedirectView(BASE_MAPPING, true));
    }

}
