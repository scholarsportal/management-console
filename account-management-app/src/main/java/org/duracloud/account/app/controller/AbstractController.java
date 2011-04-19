/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.common.domain.DuracloudUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

/**
 * Base class for all controllers.
 * 
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 */

public abstract class AbstractController {

    protected Logger log = LoggerFactory.getLogger(AbstractController.class);

    protected static final String PREFIX = "";

    public static final String NEW_MAPPING = "/new";
    public static final String EDIT_MAPPING = "/edit";

    /**
     * 
     */
    protected void sleepMomentarily() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param user
     */
    protected void reauthenticate(
        DuracloudUser user, AuthenticationManager authenticationManager) {
        reauthenticate(user.getUsername(),
            user.getPassword(),
            authenticationManager);
    }

    /**
     * @param user
     */
    protected void reauthenticate(
        String username, String password,
        AuthenticationManager authenticationManager) {
        SecurityContext ctx = SecurityContextHolder.getContext();
        UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken(username, password);
        Authentication auth = authenticationManager.authenticate(token);
        ctx.setAuthentication(auth);
    }

    public void init() {
        log.info("initializing " + this.toString());
    }

    public void destroy() {
        log.info("destroying " + this.toString());
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e, HttpSession session) {
        log.error(e.getMessage(), e);
        session.setAttribute("error", e.getMessage());
        String username =
            SecurityContextHolder.getContext().getAuthentication().getName();
        return new ModelAndView("redirect:/users/byid/" + username);

    }
}
