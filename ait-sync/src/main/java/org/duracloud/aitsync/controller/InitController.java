package org.duracloud.aitsync.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.duracloud.aitsync.domain.ArchiveItConfig;
import org.duracloud.aitsync.service.RestUtils;
import org.duracloud.aitsync.util.ArchiveItConfigMarshaller;
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

    @Autowired
    public InitController(RestUtils restUtils) {
        this.restUtils = restUtils;
    }


    private Logger log = LoggerFactory.getLogger(InitController.class);

    
    @RequestMapping(value = "/init", method = RequestMethod.POST)
    public ModelAndView init() throws IOException {
        log.debug("initializing...");
        InputStream is = restUtils.getInputStream(request);
        ArchiveItConfig config = ArchiveItConfigMarshaller.unmarshall(is);
        log.info("successfully initialized archiveit-ingest: {}", config);
        Map<String,String> map = new HashMap<String,String>();
        map.put("message", "You have successfully initialized archive-it ingest application.");
        ModelAndView mav = new ModelAndView("command", "result", map);
        return mav;
    }

}
