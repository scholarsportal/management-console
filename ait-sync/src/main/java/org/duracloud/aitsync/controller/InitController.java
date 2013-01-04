package org.duracloud.aitsync.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.duracloud.aitsync.config.ConfigManager;
import org.duracloud.aitsync.domain.Configuration;
import org.duracloud.aitsync.util.ConfigurationMarshaller;
import org.duracloud.aitsync.util.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
public class InitController extends BaseController{
    
    protected RestUtils restUtils;
    private ConfigManager configManager;
    
    @Autowired
    public InitController(RestUtils restUtils, ConfigManager configManager) {
        this.restUtils = restUtils;
        this.configManager = configManager;
    }


    private Logger log = LoggerFactory.getLogger(InitController.class);

    
    @RequestMapping(value = "/init", method = RequestMethod.POST)
    public ModelAndView init() throws IOException {
        log.debug("initializing...");
        InputStream is = restUtils.getInputStream(request);
        Configuration config = ConfigurationMarshaller.unmarshall(is);
        
        configManager.initialize(config);
        log.info("successfully initialized archiveit-ingest: {}", config);
        Map<String,String> map = new HashMap<String,String>();
        
        map.put("message", "You have successfully initialized archive-it sync application.");
        ModelAndView mav = new ModelAndView("command", "result", map);
        return mav;
    }

}
