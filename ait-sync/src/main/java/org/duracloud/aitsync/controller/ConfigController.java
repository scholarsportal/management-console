package org.duracloud.aitsync.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.duracloud.aitsync.domain.Mapping;
import org.duracloud.aitsync.domain.MappingForm;
import org.duracloud.aitsync.service.MappingAlreadyExistsException;
import org.duracloud.aitsync.service.MappingManager;
import org.duracloud.aitsync.service.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Daniel Bernstein
 * @created 12/17/2012
 * 
 */
@Controller
public class ConfigController extends BaseController {

    private Logger log = LoggerFactory.getLogger(ConfigController.class);

    private MappingManager mappingManager;
    private RestUtils restUtils;

    @Autowired
    public ConfigController(MappingManager mappingManager, RestUtils restUtils) {
        this.mappingManager = mappingManager;
        this.restUtils = restUtils;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ModelAndView add(@Valid @ModelAttribute("mapping") MappingForm form,
                            BindingResult result) {

        String message = "mapping added successfully";
        ModelAndView mav = new ModelAndView("command");
        Map<String, String> map = new HashMap<String, String>();

        if (result.hasErrors()) {
            message = "failed to add map: invalid parameters";
            restUtils.setStatus(response, HttpStatus.BAD_REQUEST);
        } else {
            try {
                this.mappingManager.addMapping(form.toMapping());
            } catch (MappingAlreadyExistsException e) {
                message = "failed to add map: mapping already exists";
                log.warn(message, e);
            }
        }

        map.put("message", message);
        mav.addObject(form.toMapping());
        return mav;
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public ModelAndView remove(long archiveItAccountId) {
        Mapping mapping = this.mappingManager.removeMapping(archiveItAccountId);
        Map<String, String> map = new HashMap<String, String>();
        ModelAndView mav = new ModelAndView("command");
        String message = "successfully removed.";
        if (mapping != null) {
            mav.addObject(mapping);
            message =
                "no mapping found associated with archive-it account ("
                    + archiveItAccountId + ")";
        }
        map.put("message", message);
        return mav;
    }

}
