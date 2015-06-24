/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 *     http://duracloud.org/license/
 */
package org.duracloud.account.app.controller;

import org.duracloud.account.db.model.AccountInfo;
import org.duracloud.account.db.util.error.AccountNotFoundException;
import org.duracloud.account.db.util.error.DBNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

/**
 * 
 * @author Daniel Bernstein
 *
 */
@Controller
@Lazy
public class AccountInfoController extends AbstractAccountController {
    public static final String ACCOUNT_DETAILS_VIEW_ID = "account-details";

    public static final String ACCOUNT_INFO_EDIT_ID = "account-info-edit";
    public static final String EDIT_ACCOUNT_INFO_FORM_KEY = "accountInfoEditForm";

    public static final String INFO_EDIT_MAPPING = 
                                    AccountDetailsController.ACCOUNT_DETAILS_MAPPING + 
                                        EDIT_PATH;

    @RequestMapping(value = INFO_EDIT_MAPPING, method = RequestMethod.GET)
    public String getEditForm(@PathVariable Long accountId, Model model)
        throws AccountNotFoundException {
        log.info("getEditForm account {}", accountId);

        loadAccountInfo(accountId, model);
        AccountInfo accountInfo = (AccountInfo) model.asMap().get("accountInfo");
        
        
        AccountEditForm editForm = new AccountEditForm();
        editForm.setDepartment(accountInfo.getDepartment());
        editForm.setOrgName(accountInfo.getOrgName());
        editForm.setAcctName(accountInfo.getAcctName());
        model.addAttribute(EDIT_ACCOUNT_INFO_FORM_KEY, editForm);

        return ACCOUNT_INFO_EDIT_ID;
    }


    @Transactional
    @RequestMapping(value = INFO_EDIT_MAPPING, method = RequestMethod.POST)
    public ModelAndView editInfo(
                           @PathVariable Long accountId,
                           @ModelAttribute(EDIT_ACCOUNT_INFO_FORM_KEY) 
                           @Valid AccountEditForm accountEditForm,
					   BindingResult result,
					   Model model) throws AccountNotFoundException, 
					                       DBNotFoundException {
        
        log.info("editInfo account {}", accountId);

        if (result.hasErrors()) {
			return new ModelAndView(ACCOUNT_INFO_EDIT_ID, model.asMap());
		}

        getAccountManagerService().getAccount(accountId).
        storeAccountInfo(accountEditForm.getAcctName(),
                         accountEditForm.getOrgName(),
                         accountEditForm.getDepartment());

        
        return createAccountRedirectModelAndView(accountId,
                                                 AccountDetailsController.ACCOUNT_DETAILS_PATH);

    }
}
