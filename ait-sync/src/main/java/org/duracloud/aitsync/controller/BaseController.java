package org.duracloud.aitsync.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author Daniel Bernstein
 * @created 12/17/2012
 * 
 */
public class BaseController {
 
    @Autowired
    protected HttpServletRequest request;

}
