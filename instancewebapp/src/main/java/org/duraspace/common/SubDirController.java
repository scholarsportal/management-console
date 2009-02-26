package org.duraspace.common;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.UrlFilenameViewController;


public class SubDirController extends UrlFilenameViewController
{

    protected final Logger log = Logger.getLogger(getClass());

    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request,
    		HttpServletResponse response)
    {
        String now = (new Date()).toString();

        String viewName = this.getViewNameForRequest(request);
        log.info("Returning subDir view with "+ now +", and view: '"+viewName+"'");

        ModelAndView mav = new ModelAndView(viewName);
        mav.addObject("now", now);

        return mav;
    }


}