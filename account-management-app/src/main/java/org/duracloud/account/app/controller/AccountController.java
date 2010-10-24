/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import java.text.MessageFormat;

import javax.validation.Valid;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.common.domain.DuracloudUser;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.AccountService;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.duracloud.account.util.error.SubdomainAlreadyExistsException;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
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
public class AccountController extends AbstractAccountController {

	@RequestMapping(value = {ACCOUNT_PATH}, method = RequestMethod.GET)
	public String getHome(@PathVariable String accountId, Model model)
			throws AccountNotFoundException {
		AccountService accountService = accountManagerService
				.getAccount(accountId);
		AccountInfo info = accountService.retrieveAccountInfo();
		model.addAttribute("accountInfo", info);
		return ACCOUNT_HOME;
	}

	@RequestMapping(value = { NEW_MAPPING }, method = RequestMethod.GET)
	public ModelAndView getNewForm() {
		log.info("serving up new AccountForm");
		return new ModelAndView(NEW_ACCOUNT_VIEW, "newAccountForm",
				new NewAccountForm());
	}

	@RequestMapping(value = { NEW_MAPPING }, method = RequestMethod.POST)
	public String add( @ModelAttribute("newAccountForm") @Valid NewAccountForm newAccountForm,
					   BindingResult result, 
					   Model model) {

		if (result.hasErrors()) {
			return NEW_ACCOUNT_VIEW;
		}

		try {

			SecurityContext securityContext = SecurityContextHolder.getContext();
			String username = securityContext.getAuthentication().getName();
			DuracloudUser user = this.userService.loadDuracloudUserByUsername(username);
			AccountInfo accountInfo = new AccountInfo(
										newAccountForm.getSubdomain(), 
										newAccountForm.getAcctName(),
										newAccountForm.getOrgName(),
										newAccountForm.getDepartment(),
										user, 
										newAccountForm.getStorageProviders());
		
			AccountService service = this.accountManagerService.createAccount(accountInfo);
			String id = service.retrieveAccountInfo().getId();
			return formatAccountRedirect(id, "/");
		} catch (SubdomainAlreadyExistsException ex) {
			result.addError(new ObjectError("subdomain",
					"The subdomain you selected is already in use. Please choose another."));
			return NEW_ACCOUNT_VIEW;
		} catch (DBNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new Error("This should never happen", e);
		}

	}



}
