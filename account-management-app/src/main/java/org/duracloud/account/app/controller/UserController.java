/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountManagerService;
import org.duracloud.account.util.DuracloudUserService;
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
import org.springframework.web.servlet.ModelAndView;

/**
 * The default view for this application
 * 
 * @contributor dbernstein
 */
@Controller
@Lazy
@RequestMapping("/users")
public class UserController extends  AbstractController {

	public static final String NEW_USER_VIEW = "user-new";
	public static final String USER_HOME = "user-home";

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
		binder.setValidator(new org.springframework.validation.Validator(){
			@Override
			public boolean supports(Class<?> clazz) {
				return true;
			}

			@Override
			public void validate(Object target, Errors errors) {
				NewUserForm nuf = (NewUserForm)target;
				//ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		        //Validator validator = factory.getValidator();
		        
		        Set<ConstraintViolation<NewUserForm>> constraintViolations = validator.validate(nuf, Default.class);
		        for(ConstraintViolation<NewUserForm> cv : constraintViolations){
		        	errors.rejectValue(cv.getPropertyPath().toString(), cv.getMessage(),cv.getMessage());
		        }

		        if(nuf.getPassword() == null || !nuf.getPassword().equals(nuf.getPasswordConfirm())){
		        	nuf.setPassword(null);
		        	nuf.setPasswordConfirm(null);
		        	errors.rejectValue("passwordConfirm", "password.nomatch", "Passwords do not match. Please reenter the password and confirmation.");
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
			throw new NullPointerException(
					"userService must be non-null");
		}

		this.userService = userService;
		log.info("new instance created: " + getClass().getName());
	}

	public DuracloudUserService getUserService(){
		return this.userService;
	}

	@RequestMapping(value = { NEW_MAPPING }, method = RequestMethod.GET)
	public ModelAndView getNewForm() {
		log.info("serving up NewUserForm");
		return new ModelAndView(NEW_USER_VIEW, "newUserForm",
				new NewUserForm());
	}

	@RequestMapping(value = { "/byid/{username}" }, method = RequestMethod.GET)
	//@PreAuthorize("#userId == authentication.name OR hasRole('ROLE_ROOT')")
	public ModelAndView getUser(@PathVariable String username) throws DBNotFoundException{
		log.debug("getting user {}", username);
		DuracloudUser user = this.userService.loadDuracloudUserByUsername(username);
		ModelAndView mav =  new ModelAndView(USER_HOME, 
				"user", user);
		
		List<AccountInfo> accounts = this.accountManagerService.lookupAccountsByUsername(user.getUsername());
		mav.addObject("accounts", accounts);
		return mav;
	}

	@RequestMapping(value = {NEW_MAPPING }, method = RequestMethod.POST)
	public String add(
			@ModelAttribute("newUserForm") @Valid NewUserForm newUserForm,
			BindingResult result, Model model, HttpServletRequest request) throws Exception {
		if (result.hasErrors()) {
			return NEW_USER_VIEW;
		}
		
		this.userService.createNewUser(
				newUserForm.getUsername(), 
				newUserForm.getPassword(), 
				newUserForm.getFirstName(),
				newUserForm.getLastName(),
				newUserForm.getEmail());
		
		SecurityContext ctx = SecurityContextHolder.getContext();
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						newUserForm.getUsername(), newUserForm.getPassword()));
		ctx.setAuthentication(auth);
	
		
		return "redirect:"+PREFIX+"/users/byid/"+newUserForm.getUsername();
	}

	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	public void setAccountManagerService(AccountManagerService accountManagerService) {
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
