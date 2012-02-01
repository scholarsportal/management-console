package org.duracloud.account.app.controller;

import javax.validation.Valid;

import org.duracloud.account.common.domain.AccountInfo;
import org.duracloud.account.db.error.DBConcurrentUpdateException;
import org.duracloud.account.db.error.DBNotFoundException;
import org.duracloud.account.util.error.AccountNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
@Lazy
public class AccountInfoController extends AbstractAccountController {
    public static final String ACCOUNT_DETAILS_VIEW_ID = "account-details";

    public static final String ACCOUNT_INFO_EDIT_ID = "account-info-edit";
    public static final String EDIT_ACCOUNT_INFO_FORM_KEY = "accountInfoEditForm";

    public static final String INFO_EDIT_MAPPING = AccountDetailsController.ACCOUNT_DETAILS_MAPPING + EDIT_PATH;

    @RequestMapping(value = INFO_EDIT_MAPPING, method = RequestMethod.GET)
    public String getEditForm(@PathVariable int accountId, Model model)
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



    @RequestMapping(value = INFO_EDIT_MAPPING, method = RequestMethod.POST)
    public ModelAndView editInfo(@PathVariable int accountId,
                           @ModelAttribute(EDIT_ACCOUNT_INFO_FORM_KEY) @Valid AccountEditForm accountEditForm,
					   BindingResult result,
					   Model model) throws AccountNotFoundException, DBConcurrentUpdateException, DBNotFoundException {
        log.info("editInfo account {}", accountId);

        if (result.hasErrors()) {
			return new ModelAndView(ACCOUNT_INFO_EDIT_ID, model.asMap());
		}

        getAccountManagerService().getAccount(accountId).
        storeAccountInfo(accountEditForm.getAcctName(),
                         accountEditForm.getOrgName(),
                         accountEditForm.getDepartment());

        
        return createAccountRedirectModelAndView(Integer.toString(accountId), AccountDetailsController.ACCOUNT_DETAILS_PATH);

    }
}
