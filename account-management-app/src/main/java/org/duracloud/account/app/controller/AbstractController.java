/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.util.Set;

import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.common.domain.Role;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.UserFeedbackUtil;
import org.duracloud.common.error.DuraCloudRuntimeException;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Base class for all controllers.
 * 
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 */

public abstract class AbstractController {

    protected Logger log = LoggerFactory.getLogger(AbstractController.class);
    public static final String USERS_MAPPING = "/users";
    public static final String USER_MAPPING = "/byid/{username:[a-z0-9.\\-_@]*}";

    public static final String NEW_MAPPING = "/new";
    public static final String EDIT_MAPPING = "/edit";

    public static final String BY_ID_MAPPING = "/byid/{id}";
    public static final String BY_ID_EDIT_MAPPING = BY_ID_MAPPING + EDIT_MAPPING;
    public static final String BY_ID_DELETE_MAPPING = BY_ID_MAPPING + "/delete";



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
    public ModelAndView handleException(Exception e) {
        log.error(e.getMessage(), e);
        return new ModelAndView("exception", "ex", e);
    }

    /*
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e) {
        setError(e);
        String username =
            SecurityContextHolder.getContext().getAuthentication().getName();
        return createRedirectMav(UserController.formatUserUrl(username));
    }
    */

    protected static String formatUserUrl(String username) {
        String url =  USERS_MAPPING + USER_MAPPING;
        url = url.replaceAll("\\{username.*\\}", username);
        return url;
    }

    protected void setError(Exception e) {
        setError(e,null);
    }
    
    protected void setError(Exception e, RedirectAttributes redirectAttributes) {
        log.error(e.getMessage(), e);
        UserFeedbackUtil.addFailureFlash(e.getMessage(), redirectAttributes);
    }
    

    
    protected ModelAndView createRedirectMav(String url) {
        RedirectView view = new RedirectView(url, true);
        view.setExposeModelAttributes(false);
        return new ModelAndView(view);
    }

    protected void setSuccessFeedback(String message, RedirectAttributes redirectAttributes) {
        UserFeedbackUtil.addSuccessFlash(message, redirectAttributes);
    }

    protected void setFailureFeedback(String message, RedirectAttributes redirectAttributes) {
        UserFeedbackUtil.addFailureFlash(message, redirectAttributes);
    }

}
