/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.duradmin.control;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author dbernstein@duraspace.org
 */
public class HomeController
        extends AbstractController {

    private ControllerSupport controllerSupport;

    public HomeController(ControllerSupport controllerSupport) {
        this.controllerSupport = controllerSupport;
    }

    @Override
    public ModelAndView handleRequest(HttpServletRequest request,
                                      HttpServletResponse response)
            throws Exception {

        ModelAndView mav = super.handleRequest(request, response);
        return controllerSupport.handle(mav, request, response);
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
                                                 HttpServletResponse response)
            throws Exception {
        ModelAndView m = new ModelAndView("home");
        return m;
    }

}