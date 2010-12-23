/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.error.InvalidPasswordException;
import org.duracloud.account.util.error.InvalidRedemptionCodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * The default view for this application
 * 
 * @contributor dbernstein
 */
@Controller
@Lazy
@RequestMapping("/users")
public class UserController extends AbstractController {

    /**
     * 
     */
    public static final String USER_KEY = "user";
    public static final String NEW_USER_VIEW = "user-new";
    public static final String NEW_USER_WELCOME = "user-welcome";

    public static final String USER_HOME = "user-home";
    public static final String USER_ACCOUNTS = "user-accounts";

    public static final String USER_MAPPING = "/byid/{username}";

    public static final String USER_WELCOME_MAPPING = USER_MAPPING + "/welcome";

    public static final String USER_EDIT_MAPPING = USER_MAPPING + EDIT_MAPPING;

    public static final String USER_ACCOUNTS_MAPPING =
        USER_MAPPING + "/accounts";

    private static final String USER_EDIT_VIEW = "user-edit";

    private static final String CHANGE_PASSWORD_MAPPING =
        USER_MAPPING + "/change-password";
    private static final String CHANGE_PASSWORD_VIEW = "user-change-password";
    private static final String USER_PROFILE_FORM_KEY = "userProfileEditForm";
    private static final String CHANGE_PASSWORD_FORM_KEY = "changePasswordForm";
    @Autowired
    private AccountManagerService accountManagerService;

    @Autowired
    private DuracloudUserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 
     * @param userService
     */
    public void setUserService(DuracloudUserService userService) {
        if (userService == null) {
            throw new NullPointerException("userService must be non-null");
        }

        this.userService = userService;
        log.info("new instance created: " + getClass().getName());
    }

    public DuracloudUserService getUserService() {
        return this.userService;
    }

    @RequestMapping(value = { NEW_MAPPING }, method = RequestMethod.GET)
    public ModelAndView getNewForm(HttpServletRequest request) {
        log.info("serving up NewUserForm");
        NewUserForm newUserForm = new NewUserForm();
        newUserForm.setRedemptionCode(removeRedemptionCodeFromSession(request));
        return new ModelAndView(NEW_USER_VIEW, "newUserForm", newUserForm);
    }

    @RequestMapping(value = { "/profile" }, method = RequestMethod.GET)
    public String profileRedirect() {
        String username =
            SecurityContextHolder.getContext().getAuthentication().getName();
        return "redirect:/users/byid/" + username;

    }

    @RequestMapping(value = { USER_MAPPING }, method = RequestMethod.GET)
    public ModelAndView getUser(
        @PathVariable String username, HttpServletRequest request)
        throws DBNotFoundException {
        log.debug("getting user {}", username);
        // if there's a redemption code in the session, it means that
        // the user logged in in order to redeem an invitation
        String redemptionCode = removeRedemptionCodeFromSession(request);
        if (redemptionCode != null) {
            log.debug("redemption code found in session: {}", redemptionCode);
            DuracloudUser user =
                this.userService.loadDuracloudUserByUsername(username);
            int accountId;
            try {
                accountId =
                    this.userService.redeemAccountInvitation(user.getId(),
                        redemptionCode);
                // FIXME
                sleepMomentarily();
                user = this.userService.loadDuracloudUserByUsername(username);
                reauthenticate(user, this.authenticationManager);
                return getUserWelcome(username, accountId);
            } catch (InvalidRedemptionCodeException e) {
                log.error("redemption failed for {} on redemption {}",
                    username,
                    redemptionCode);
                addRedemptionFailedMessage();
            }
        }

        ModelAndView mav = new ModelAndView(USER_HOME);
        prepareModel(username, mav);
        return mav;
    }

    /**
     * 
     */
    private void addRedemptionFailedMessage() {
        addErrorMessage("The redemption code you supplied is invalid. We are unable to add you to an account.");
    }

    /**
     * @param request
     * @return
     */
    private String removeRedemptionCodeFromSession(HttpServletRequest request) {
        String redemptionCode =
            (String) request.getSession().getAttribute("redemptionCode");
        if (redemptionCode != null) {
            request.getSession().removeAttribute("redemptionCode");
        }
        return redemptionCode;
    }

    @RequestMapping(value = { USER_ACCOUNTS_MAPPING }, method = RequestMethod.GET)
    public ModelAndView getUserAccounts(@PathVariable String username)
        throws DBNotFoundException {
        log.debug("getting user accounts for {}", username);
        ModelAndView mav = new ModelAndView(USER_ACCOUNTS);
        prepareModel(username, mav);
        return mav;
    }

    @RequestMapping(value = { USER_EDIT_MAPPING }, method = RequestMethod.GET)
    public String edit(@PathVariable String username, Model model)
        throws DBNotFoundException {
        log.debug("getting user accounts for {}", username);
        UserProfileEditForm form = new UserProfileEditForm();
        DuracloudUser user =
            this.userService.loadDuracloudUserByUsername(username);
        form.setFirstName(user.getFirstName());
        form.setLastName(user.getLastName());
        form.setEmail(user.getEmail());
        model.addAttribute(USER_PROFILE_FORM_KEY, form);
        addUserToModel(user, model);
        return USER_EDIT_VIEW;
    }

    @RequestMapping(value = { USER_EDIT_MAPPING }, method = RequestMethod.POST)
    public String update(
        @PathVariable String username,
        @ModelAttribute(USER_PROFILE_FORM_KEY) @Valid UserProfileEditForm form,
        BindingResult result, Model model) throws Exception {

        if (result.hasErrors()) {
            log.debug("profile form has errors for {}: returning...", username);
            model.addAttribute(USER_KEY,
                this.userService.loadDuracloudUserByUsername(username));
            return USER_EDIT_VIEW;
        }

        log.info("updating user profile for {}", username);
        int id = this.userService.loadDuracloudUserByUsername(username).getId();
        this.userService.storeUserDetails(id,
            form.getFirstName(),
            form.getLastName(),
            form.getEmail());
        return "redirect:" + PREFIX + "/users/byid/" + username;
    }

    @RequestMapping(value = { CHANGE_PASSWORD_MAPPING }, method = RequestMethod.GET)
    public String changePassword(@PathVariable String username, Model model)
        throws DBNotFoundException {
        log.debug("opening change password form  for {}", username);
        model.addAttribute(CHANGE_PASSWORD_FORM_KEY, new ChangePasswordForm());
        // add related model objects
        addUserToModel(this.userService.loadDuracloudUserByUsername(username),
            model);
        return CHANGE_PASSWORD_VIEW;
    }

    @RequestMapping(value = { CHANGE_PASSWORD_MAPPING }, method = RequestMethod.POST)
    public String changePassword(
        @PathVariable String username,
        @ModelAttribute(CHANGE_PASSWORD_FORM_KEY) @Valid ChangePasswordForm form,
        BindingResult result, Model model) throws DBNotFoundException {

        DuracloudUser user =
            this.userService.loadDuracloudUserByUsername(username);

        // check for errors
        if (!result.hasErrors()) {
            log.info("changing user password for {}", username);
            int id = user.getId();
            try {
                this.userService.changePassword(id,
                    form.getOldPassword(),
                    form.getPassword());
                return "redirect:" + PREFIX + "/users/byid/" + username;
            } catch (InvalidPasswordException e) {
                result.addError(new FieldError(CHANGE_PASSWORD_FORM_KEY,
                    "oldPassword",
                    "The old password is not correct"));
            }
        }

        log.debug("password form has errors for {}: returning...", username);
        addUserToModel(user, model);
        return CHANGE_PASSWORD_VIEW;

    }

    /**
     * @param user
     * @param mav
     */
    private void prepareModel(DuracloudUser user, ModelAndView mav) {
        mav.addObject(USER_KEY, user);
        Set<AccountInfo> accounts =
            this.accountManagerService.findAccountsByUserId(user.getId());
        mav.addObject("accounts", accounts);
    }

    private void addUserToModel(DuracloudUser user, Model model) {
        model.addAttribute(USER_KEY, user);
    }

    @RequestMapping(value = { USER_WELCOME_MAPPING }, method = RequestMethod.GET)
    public ModelAndView getUserWelcome(
        @PathVariable String username,
        @RequestParam(value = "accountId", required = false) Integer accountId)
        throws DBNotFoundException {

        log.debug("opening welcome page for for {}: accountId={}",
            username,
            accountId);
        ModelAndView mav = new ModelAndView(NEW_USER_WELCOME);
        if (accountId != null) {
            mav.addObject("accountId", accountId);
        }
        prepareModel(username, mav);
        return mav;
    }

    /**
     * @param mav
     */
    private void prepareModel(String username, ModelAndView mav)
        throws DBNotFoundException {
        DuracloudUser user =
            this.userService.loadDuracloudUserByUsername(username);
        prepareModel(user, mav);
    }

    @RequestMapping(value = { NEW_MAPPING }, method = RequestMethod.POST)
    public String add(
        @ModelAttribute("newUserForm") @Valid NewUserForm newUserForm,
        BindingResult result, Model model, HttpServletRequest request)
        throws Exception {
        if (result.hasErrors()) {
            return NEW_USER_VIEW;
        }

        DuracloudUser user =
            this.userService.createNewUser(newUserForm.getUsername(),
                newUserForm.getPassword(),
                newUserForm.getFirstName(),
                newUserForm.getLastName(),
                newUserForm.getEmail());

        // FIXME seems like there is some latency between successfully creating
        // a new user and the user actually being visible to subsequent calls
        // to the repository (due to amazon async I believe).
        sleepMomentarily();

        String redemptionCode = newUserForm.getRedemptionCode();
        int accountId = -1;
        if (!StringUtils.isEmpty(redemptionCode)) {
            try {

                accountId =
                    this.userService.redeemAccountInvitation(user.getId(),
                        redemptionCode);
                // FIXME credentials aren't propagating immediately
                sleepMomentarily();
            } catch (InvalidRedemptionCodeException ex) {
                addRedemptionFailedMessage();
            }
        }

        reauthenticate(newUserForm.getUsername(),
            newUserForm.getPassword(),
            this.authenticationManager);

        String redirect =
            "redirect:"
                + PREFIX + "/users/byid/" + newUserForm.getUsername()
                + "/welcome";

        if (accountId > -1) {
            redirect += "?accountId=" + accountId;
        }

        return redirect;
    }

    /**
     * @param string
     */
    private void addErrorMessage(String string) {
        // TODO Auto-generated method stub

    }

    @RequestMapping(value = { "/redeem/{redemptionCode}" }, method = RequestMethod.GET)
    public ModelAndView redeemUser(
        HttpServletRequest request, @PathVariable String redemptionCode)
        throws DBNotFoundException {
        log.debug("getting redeem invitation {}", redemptionCode);

        // force logout
        request.getSession().invalidate();
        // add the redemption code to the session
        request.getSession(true).setAttribute("redemptionCode", redemptionCode);
        ModelAndView mav = new ModelAndView(HomeController.HOME_VIEW_ID);
        mav.addObject("redemptionCode", redemptionCode);
        return mav;
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(
        AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setAccountManagerService(
        AccountManagerService accountManagerService) {
        this.accountManagerService = accountManagerService;
    }

    public AccountManagerService getAccountManagerService() {
        return accountManagerService;
    }
}
