/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.duracloud.account.util.DuracloudUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

	private DuracloudUserService userService;
	public static final String NEW_USER_VIEW = "user-new";
	public static final String USER_HOME = "user-home";

	@Autowired
	private AuthenticationManager authenticationManager;
	/**
	 * 
	 * @param userService
	 */
	@Autowired
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

	@RequestMapping(value = { "/byid/{userId}" }, method = RequestMethod.GET)
	@PreAuthorize("#userId == authentication.name OR hasRole(\'ROLE_ROOT\')")
	public ModelAndView get(@PathVariable String userId){
		log.debug("getting user {}", userId);
		return new ModelAndView(USER_HOME, "username", userId);
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

}
