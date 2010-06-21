/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.mainwebapp.control;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.UrlFilenameViewController;

public class SubDirController
        extends UrlFilenameViewController {

    protected final Logger log = LoggerFactory.getLogger(SubDirController.class);

    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request,
                                              HttpServletResponse response) {
        String now = (new Date()).toString();

        String viewName = this.getViewNameForRequest(request);
        log.info("Returning subDir view with " + now + ", and view: '"
                + viewName + "'");

        ModelAndView mav = new ModelAndView(viewName);
        mav.addObject("now", now);

        return mav;
    }

}