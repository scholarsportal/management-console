
package org.duracloud.duradmin.spaces.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * 
 * @author Daniel Bernstein
 *
 */
public class SpacesController implements Controller {

    protected final Logger log = LoggerFactory.getLogger(SpacesController.class);
    
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("spacesManager");
        return mav;
	}


}