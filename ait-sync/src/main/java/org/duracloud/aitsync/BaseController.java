package org.duracloud.aitsync;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author Daniel Bernstein
 * @created 12/17/2012
 * 
 */
public class BaseController {
 
    protected RestUtils restUtils;

    @Autowired
    protected HttpServletRequest request;

    public BaseController(RestUtils restUtils) {
        this.restUtils = restUtils;
    }

}
