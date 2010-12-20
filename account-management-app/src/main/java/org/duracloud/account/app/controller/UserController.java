/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.DuracloudUserService;
import org.duracloud.account.util.error.InvalidRedemptionCodeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
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

    public static final String NEW_USER_VIEW = "user-new";
    public static final String NEW_USER_WELCOME = "user-welcome";

    public static final String USER_HOME = "user-home";
    public static final String USER_ACCOUNTS = "user-accounts";

    @Autowired
    private AccountManagerService accountManagerService;

    @Autowired
    private DuracloudUserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private Validator validator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(new org.springframework.validation.Validator() {
            @Override
            public boolean supports(Class<?> clazz) {
                return true;
            }

            @Override
            public void validate(Object target, Errors errors) {
                NewUserForm nuf = (NewUserForm) target;
                // ValidatorFactory factory =
                // Validation.buildDefaultValidatorFactory();
                // Validator validator = factory.getValidator();

                Set<ConstraintViolation<NewUserForm>> constraintViolations =
                    validator.validate(nuf, Default.class);
                for (ConstraintViolation<NewUserForm> cv : constraintViolations) {
                    errors.rejectValue(cv.getPropertyPath().toString(),
                        cv.getMessage(),
                        cv.getMessage());
                }

                if (nuf.getPassword() == null
                    || !nuf.getPassword().equals(nuf.getPasswordConfirm())) {
                    nuf.setPassword(null);
                    nuf.setPasswordConfirm(null);
                    errors.rejectValue("passwordConfirm",
                        "password.nomatch",
                        "Passwords do not match. Please reenter the password and confirmation.");
                }
            }
        });
    }

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

    @RequestMapping(value = { "/byid/{username}" }, method = RequestMethod.GET)
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

    @RequestMapping(value = { "/byid/{username}/accounts" }, method = RequestMethod.GET)
    public ModelAndView getUserAccounts(@PathVariable String username)
        throws DBNotFoundException {
        log.debug("getting user accounts for {}", username);
        ModelAndView mav = new ModelAndView(USER_ACCOUNTS);
        prepareModel(username, mav);
        return mav;
    }

    @RequestMapping(value = { "/byid/{username}/welcome" }, method = RequestMethod.GET)
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
        mav.addObject("user", user);

        Set<AccountInfo> accounts =
            this.accountManagerService.findAccountsByUserId(user.getId());
        mav.addObject("accounts", accounts);

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
        if (redemptionCode != null) {
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

        user = this.userService.loadDuracloudUserByUsername(user.getUsername());
        reauthenticate(user,
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

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public Validator getValidator() {
        return validator;
    }

}
