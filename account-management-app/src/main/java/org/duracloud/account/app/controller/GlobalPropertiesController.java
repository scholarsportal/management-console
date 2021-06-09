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
import org.duracloud.account.db.model.RabbitMQConfig;
import org.duracloud.account.db.util.GlobalPropertiesConfigService;
import org.duracloud.account.db.util.RabbitMQConfigService;
import org.duracloud.account.util.UserFeedbackUtil;
import org.duracloud.common.changenotifier.NotifierType;
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
 * Date: 01/05/2015
 */
@Controller
@RequestMapping(GlobalPropertiesController.BASE_MAPPING)
public class GlobalPropertiesController {

    private static final String GLOBAL_PROPERTIES_ATTRIBUTE = "globalProperties";

    @Autowired
    private GlobalPropertiesConfigService globalPropertiesConfigService;

    public static final String BASE_MAPPING = "/root/globalproperties";

    public void setGlobalPropertiesConfigService(
        GlobalPropertiesConfigService globalPropertiesConfigService) {
        this.globalPropertiesConfigService = globalPropertiesConfigService;
    }

    @Autowired
    private RabbitMQConfigService rabbitMQConfigService;

    public void setRabbitMQConfigService(
        RabbitMQConfigService rabbitMQConfigService) {
        this.rabbitMQConfigService = rabbitMQConfigService;
    }

    /**
     * @return
     */
    @RequestMapping("")
    public ModelAndView get() {
        ModelAndView mav = new ModelAndView(BASE_MAPPING);
        return mav;
    }

    @ModelAttribute(GLOBAL_PROPERTIES_ATTRIBUTE)
    public GlobalPropertiesForm form() {
        GlobalPropertiesForm form = new GlobalPropertiesForm();
        GlobalProperties entity = this.globalPropertiesConfigService.get();
        if (entity == null) {
            return new GlobalPropertiesForm();
        } else {
            form.setNotifierType(entity.getNotifierType());
            form.setRabbitmqExchange(entity.getRabbitmqExchange());
            form.setInstanceNotificationTopicArn(entity.getInstanceNotificationTopicArn());
            form.setCloudFrontAccountId(entity.getCloudFrontAccountId());
            form.setCloudFrontKeyId(entity.getCloudFrontKeyId());
            form.setCloudFrontKeyPath(entity.getCloudFrontKeyPath());

            RabbitMQConfig rabbitMQConfig = entity.getRabbitmqConfig();
            if (rabbitMQConfig != null) {
                form.setRabbitmqHost(rabbitMQConfig.getHost());
                form.setRabbitmqPort(rabbitMQConfig.getPort());
                form.setRabbitmqVhost(rabbitMQConfig.getVhost());
                form.setRabbitmqUsername(rabbitMQConfig.getUsername());
                form.setRabbitmqPassword(rabbitMQConfig.getPassword());
            }

            return form;
        }
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public ModelAndView edit() {
        return new ModelAndView(BASE_MAPPING + "/edit");
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @Transactional
    public ModelAndView update(@ModelAttribute(GLOBAL_PROPERTIES_ATTRIBUTE) @Valid GlobalPropertiesForm form,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        boolean hasErrors = bindingResult.hasErrors();
        if (hasErrors) {
            return new ModelAndView(BASE_MAPPING + "/edit");
        }

        // GlobalProperties RabbitMQConfig id is always 1
        // We make it null if we are not setting any RabbitMQ params
        Long rabbitMQConfigId = null;
        if (form.getNotifierType().equalsIgnoreCase(NotifierType.RABBITMQ.toString())) {
            rabbitMQConfigId = 1L;
            this.rabbitMQConfigService.set(rabbitMQConfigId,
                                           form.getRabbitmqHost(),
                                           form.getRabbitmqPort(),
                                           form.getRabbitmqVhost(),
                                           form.getRabbitmqUsername(),
                                           form.getRabbitmqPassword());
        }

        this.globalPropertiesConfigService.set(form.getNotifierType(),
                                               rabbitMQConfigId,
                                               form.getRabbitmqExchange(),
                                               form.getInstanceNotificationTopicArn(),
                                               form.getCloudFrontAccountId(),
                                               form.getCloudFrontKeyId(),
                                               form.getCloudFrontKeyPath());

        UserFeedbackUtil.addSuccessFlash("Successfully updated!", redirectAttributes);
        return new ModelAndView(new RedirectView(BASE_MAPPING, true));
    }

}
