
package org.duracloud.duradmin.dashboard.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * 
 * @author Daniel Bernstein
 *
 */
public class DashboardController implements Controller {

    protected final Logger log = Logger.getLogger(getClass());
    
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("dashboard-manager");
        return mav;
	}


}