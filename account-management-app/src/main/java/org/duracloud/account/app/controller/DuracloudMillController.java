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
import org.duracloud.account.db.model.RabbitMQConfig;
import org.duracloud.account.db.util.DuracloudMillConfigService;
import org.duracloud.account.db.util.RabbitMQConfigService;
import org.duracloud.account.util.UserFeedbackUtil;
import org.duracloud.common.queue.QueueType;
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

    @Autowired
    private RabbitMQConfigService rabbitMQConfigService;

    public void setRabbitMQConfigService(RabbitMQConfigService rabbitMQConfigService) {
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
            form.setAuditLogSpaceId(entity.getAuditLogSpaceId());
            form.setQueueType(entity.getQueueType());
            form.setRabbitmqExchange(entity.getRabbitmqExchange());

            RabbitMQConfig rabbitMQConfig = entity.getRabbitmqConfig();
            if (rabbitMQConfig != null) {
                form.setRabbitmqHost(rabbitMQConfig.getHost());
                form.setRabbitmqPort(rabbitMQConfig.getPort());
                form.setRabbitmqVhost(rabbitMQConfig.getVhost());
                form.setRabbitmqUsername(rabbitMQConfig.getUsername());
                form.setRabbitmqPassword(rabbitMQConfig.getPassword());
                form.setGlobalPropsRmqConf(rabbitMQConfig.getId() == 1L);
            }

            // Hidden value to ensure we can't use global props rmq conf
            // if there is no gloabl props rmq conf available
            form.setGlobalPropsAvailable(rabbitMQConfigService.get(1L) != null);

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

        // DuraCloud Mill RabbitMqConfig id can be either 1 or 2 depending on
        // whether user specifies to use the config set in global properties
        Long rabbitMQConfigId = null;

        if (form.getQueueType().equalsIgnoreCase(QueueType.RABBITMQ.toString())) {
            if (form.getGlobalPropsRmqConf()) {
                // ID of rmq conf for global properties is always 1
                rabbitMQConfigId = 1L;
                // No need to set anything in the db
            } else {
                rabbitMQConfigId = 2L;
                this.rabbitMQConfigService.set(rabbitMQConfigId,
                                               form.getRabbitmqHost(),
                                               form.getRabbitmqPort(),
                                               form.getRabbitmqVhost(),
                                               form.getRabbitmqUsername(),
                                               form.getRabbitmqPassword());
            }
        }

        this.duracloudMillConfigService.set(form.getDbHost(),
                                            form.getDbPort(),
                                            form.getDbName(),
                                            form.getDbUsername(),
                                            form.getDbPassword(),
                                            form.getAuditQueue(),
                                            form.getAuditLogSpaceId(),
                                            form.getQueueType(),
                                            rabbitMQConfigId,
                                            form.getRabbitmqExchange());
        UserFeedbackUtil.addSuccessFlash("Successfully updated!", redirectAttributes);
        return new ModelAndView(new RedirectView(BASE_MAPPING, true));
    }

}
