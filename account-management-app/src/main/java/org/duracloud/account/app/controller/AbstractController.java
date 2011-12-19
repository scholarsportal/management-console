/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.util.DuracloudUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Set;

/**
 * Base class for all controllers.
 * 
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 */

public abstract class AbstractController {

    protected Logger log = LoggerFactory.getLogger(AbstractController.class);

    public static final String NEW_MAPPING = "/new";
    public static final String EDIT_MAPPING = "/edit";

    /**
     * @param user
     */
    protected void reauthenticate(
        DuracloudUser user, AuthenticationManager authenticationManager) {
        reauthenticate(user.getUsername(),
                       user.getPassword(),
                       authenticationManager);
    }

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

    @ModelAttribute("ownerRole")
    public Role getOwnerRole() {
        return Role.ROLE_OWNER;
    }

    @ModelAttribute("adminRole")
    public Role getAdminRole() {
        return Role.ROLE_ADMIN;
    }

    @ModelAttribute("userRole")
    public Role getUserRole() {
        return Role.ROLE_USER;
    }

    protected void setUserRights(DuracloudUserService userService,
                                 int accountId, int userId, Role role) {
        Set<Role> roles = role.getRoleHierarchy();

        userService.setUserRights(accountId,
                                  userId,
                                  roles.toArray(new Role[roles.size()]));
    }

    /**
     * @param users
     * @return
     */
    protected boolean accountHasMoreThanOneOwner(
        Set<DuracloudUser> users, int accountId) {
        int ownerCount = 0;
        for (DuracloudUser u : users) {
            if (u.isOwnerForAcct(accountId) && !u.isRootForAcct(accountId)) {
                ownerCount++;
                if (ownerCount > 1) {
                    return true;
                }
            }
        }

        return false;
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
