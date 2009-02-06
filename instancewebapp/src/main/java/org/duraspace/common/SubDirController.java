package org.duraspace.common;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.UrlFilenameViewController;


public class SubDirController extends UrlFilenameViewController
{

    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request,
    		HttpServletResponse response)
    {
        String now = (new Date()).toString();

        String viewName = this.getViewNameForRequest(request);
        logger.info("Returning subDir view with "+ now +", and view: '"+viewName+"'");

        ModelAndView mav = new ModelAndView(viewName);
        mav.addObject("now", now);

        return mav;
    }


}