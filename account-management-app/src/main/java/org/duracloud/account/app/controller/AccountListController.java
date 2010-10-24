/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.util.RootAccountManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The default view for this application
 * 
 * @contributor dbernstein
 */
@Controller
@Lazy
@RequestMapping("/accounts")
public class AccountListController extends AbstractController {
	
	private RootAccountManagerService rootAccountManagerService;
	/**
	 * 
	 * @param accountManagerService
	 */
	@Autowired
	public AccountListController(RootAccountManagerService rootAccountManagerService){
		if(rootAccountManagerService == null){
			throw new NullPointerException("rootAccountManagerService must not be null");
		}
		this.rootAccountManagerService = rootAccountManagerService;
	}


	@RequestMapping(value = { "/" }, method = RequestMethod.GET)
	public String getAll(Model model) {
		model.addAttribute("accounts", rootAccountManagerService.listAllAccounts(null));
		return "accounts-home";
	}
}
