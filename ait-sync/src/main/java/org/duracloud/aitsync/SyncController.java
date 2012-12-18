package org.duracloud.aitsync;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    @Autowired
    public SyncController(RestUtils restUtils) {
        super(restUtils);
    }

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public ModelAndView start() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "The sync process is starting up...");
        ModelAndView mav = new ModelAndView("command", "result", map);
        return mav;
    }

    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    public ModelAndView stop()  {
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", "The sync process is stopping...");
        ModelAndView mav = new ModelAndView("command", "result", map);
        return mav;
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ModelAndView status() {
        ModelAndView mav =
            new ModelAndView("command",
                             "status",
                             new StatusSummary(State.READY));
        return mav;
    }

}
