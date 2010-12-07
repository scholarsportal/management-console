/*
 * Copyright (c) 2009-2010 DuraSpace. All rights reserved.
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.util.error.AccountNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 
 * @contributor "Daniel Bernstein (dbernstein@duraspace.org)"
 *
 */
@Controller
@Lazy
public class AccountDetailsController extends AbstractAccountController {
	public static final String ACCOUNT_DETAILS_VIEW_ID = "account-details";
	public static final String ACCOUNT_DETAILS_PATH = "/details/";
	public static final String ACCOUNT_DETAILS_MAPPING = ACCOUNT_PATH + ACCOUNT_DETAILS_PATH;
	

	@RequestMapping(value=ACCOUNT_DETAILS_MAPPING, method = RequestMethod.GET)
	public String get(@PathVariable int accountId, Model model) throws AccountNotFoundException{
		loadAccountInfo(accountId, model);
		return ACCOUNT_DETAILS_VIEW_ID;
	}
}
