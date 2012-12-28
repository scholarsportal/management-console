package org.duracloud.aitsync.controller;

import java.util.HashMap;
import java.util.Map;

import org.duracloud.aitsync.domain.StatusSummary;
import org.duracloud.aitsync.service.SyncManager;
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
public class SyncController extends BaseController {

    private Logger log = LoggerFactory.getLogger(SyncController.class);

    private SyncManager syncManager;
    
    @Autowired
    public SyncController(SyncManager syncManager){
        this.syncManager = syncManager;
    }
    
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public ModelAndView start() {
        this.syncManager.start();
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "The sync process is starting up...");
        ModelAndView mav = new ModelAndView("command", "result", map);
        return mav;
    }

    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    public ModelAndView stop()  {
        this.syncManager.stop();
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "The sync process is stopping...");
        ModelAndView mav = new ModelAndView("command", "result", map);
        return mav;
    }

    @RequestMapping(value = "/pause", method = RequestMethod.POST)
    public ModelAndView pause()  {
        this.syncManager.pause();
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "Pausing...");
        ModelAndView mav = new ModelAndView("command", "result", map);
        return mav;
    }

    @RequestMapping(value = "/resume", method = RequestMethod.POST)
    public ModelAndView resume()  {
        this.syncManager.resume();
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "The sync process is resuming...");
        ModelAndView mav = new ModelAndView("command", "result", map);
        return mav;
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ModelAndView status() {
        ModelAndView mav =
            new ModelAndView("command",
                             "status",
                             new StatusSummary(this.syncManager.getState()));
        return mav;
    }

}
